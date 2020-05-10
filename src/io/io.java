package io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import model.*;
import model.JSON.*;

import static io.FileIO.*;
import static io.Strings.*;

public class io {
    public static HashMap<String, ArrayList<Row>> allRows = new HashMap<>();
    public static HashMap<String, ArrayList<Column>> allColumns = new HashMap<>();
    public static HashMap<String, ArrayList<MinimalRow<?>>> allMinimalRows = new HashMap<>();

    public static String readJSONObject(InputStream in) {
        Scanner scanner = new Scanner(in);
        String nextLine = scanner.nextLine();
        String json = nextLine;
        while (!nextLine.equals("}")) {
            nextLine = scanner.nextLine();
            json = json.concat(nextLine);
        }
        json = json.concat("}");
        return json;
    }

    public static void processInput(String input) throws IOException {
        // todo Entity validation
        JsonObject obj = new JsonObject(input);
        obj.trimInput();
        obj.processInput();
        String command = obj.getString(COMMAND);

        if (command.equals(EXIT)) {
            System.exit(0);
        }

        String tableName = obj.getString(TABLE);

        if (command.equals(CREATE_TABLE)) {
            String primary = obj.getString(PRIMARY);
            cacheAllColumnsFromJson(tableName, primary, obj);
            createTable(tableName, primary, allColumns.get(tableName));
            System.out.println("Table Created Successfully.");
        }
        else if (command.equals(INSERT)) {
            insertToTable(tableName, obj.getObject(DATA));
            cacheAllRows(tableName);
            System.out.println("Row Inserted Successfully.");
        }
        else if (command.equals(DELETE)) {
            deleteRow(tableName, obj);
            cacheAllRows(tableName);
            System.out.println("Row Deleted Successfully.");
        }
        else if (command.equals(EDIT)) {
            // todo edit row
            cacheAllRows(tableName);
            System.out.println("Row Deleted Successfully.");
        }
        else if (command.equals(SEARCH)) {
            // todo
            filter()
        }
        else if (command.equals(SHOW_TABLE)) {
            showTable(tableName, System.out);
        }
    }

    private static void cacheAllRows(String tableName) throws IOException {
        cacheAllColumns(tableName);
        String directory = "Tables/" + tableName + "/";

        RandomAccessFile dbReader = new RandomAccessFile(new File(directory + DB_FILE_NAME), "r");
        RandomAccessFile indexReader = new RandomAccessFile(new File(directory + INDEX_FILE_NAME), "r");

        int tableRowCount = getTableRowCount(tableName);
        Column primaryCol = null;
        ArrayList<Row> rows = new ArrayList<>();

        for (int i = 0; i < tableRowCount; i++) {
            ArrayList<Column> cols = allColumns.get(tableName);
            Row row = new Row();

            for (Column col : cols) {
                if (col.isPrimary()) {
                    primaryCol = new Column(col.getName(), col.getType(), col.getSize());
                }

                if (col.getType().equals(DOUBLE)) {
                    double value = dbReader.readDouble();
                    row.addCell(new Cell<Double>(value));
                }
                else if (col.getType().equals(STRING)) {
                    byte[] b = new byte[col.getSize()];
                    dbReader.readFully(b);
                    String value = new String(b, StandardCharsets.UTF_8);
                    row.addCell(new Cell<String>(value));
                }
            }
            // index file
            indexReader.seek(i * primaryCol.getSize());
            boolean deleted = indexReader.readBoolean();
            row.setDeleted(deleted);
            rows.add(row);
        }
        allRows.put(tableName, rows);
        dbReader.close();
        indexReader.close();
    }

    private static void cacheAllMinimalRows(String tableName) throws IOException {
        String directory = "Tables/" + tableName + "/";

        RandomAccessFile dbReader = new RandomAccessFile(new File(directory + DB_FILE_NAME), "r");
        RandomAccessFile indexReader = new RandomAccessFile(new File(directory + INDEX_FILE_NAME), "r");

        Column primaryCol = getPrimary(tableName);
        boolean deleted = indexReader.readBoolean();
        int rowCount = getTableRowCount(tableName);
        ArrayList<MinimalRow<?>> minimalRows = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            if (primaryCol.getType().equals(DOUBLE)) {
                double value = indexReader.readDouble();
                minimalRows.add(new MinimalRow<Double>(value, deleted));
            }
            else if (primaryCol.getType().equals(STRING)) {
                byte[] b = new byte[primaryCol.getSize()];
                indexReader.readFully(b);
                String value = new String(b, StandardCharsets.UTF_8);
                minimalRows.add(new MinimalRow<String>(value, deleted));
            }
        }
        allMinimalRows.put(tableName, minimalRows);
    }

    private static void deleteRow(String tableName, JsonObject obj) throws IOException {
        String directory = "Tables/" + tableName + "/";
        RandomAccessFile writer = new RandomAccessFile(new File(directory + INDEX_FILE_NAME), "rw");
        Column primaryCol = getPrimary(tableName);
        int indexRowSize = primaryCol.getSize() + 1; // bool deleted : 1 Byte

        if (primaryCol.getType().equals(STRING)) {
            String wantedPrimary = obj.getString(primaryCol.getName());
            int index = getIndex(tableName, wantedPrimary);
            writer.seek(index * indexRowSize);
        }
        else {
            double wantedPrimary = obj.getDouble(primaryCol.getName());
            int index = getIndex(tableName, wantedPrimary);
            writer.seek(index * indexRowSize);
        }

        writer.writeBoolean(true); // deleted = true
    }

    public static ArrayList<Row> filter(String tableName, Filterable obj) {
        ArrayList<Row> wantedRows = new ArrayList<>();
        for (Row r : allRows.get(tableName)) {
            if (obj.isAcceptable(r)) {
                wantedRows.add(r);
            }
        }
        return wantedRows;
    }


    private static void insertToTable(String tableName, JsonObject obj) throws IOException {
        // inserts in the last row
        // todo insert in the first null row
        // todo check if the primary already exists
        String directory = "Tables/" + tableName + "/";
        Column primaryCol = getPrimary(tableName);

        RandomAccessFile indexWriter = new RandomAccessFile(new File(directory + INDEX_FILE_NAME), "rw");
        int indexRowSize = primaryCol.getSize() + 1; // bool deleted : 1 Byte
        indexWriter.seek(firstDeletedRowIndex(tableName) * indexRowSize);

        indexWriter.writeBoolean(false); // deleted = false

        if (primaryCol.getType().equals(DOUBLE)) {
            double value = obj.getDouble(primaryCol.getName());
            indexWriter.writeDouble(value);

        }
        else {
            String value = obj.getString(primaryCol.getName());
            while (value.length() < primaryCol.getSize()) {
                value = value.concat(" ");
            }
            byte[] b = value.getBytes();
            indexWriter.write(b);
        }

        int rowSize = getRowSizeInByte(tableName);

        for (Column col : allColumns.get(tableName)) {
            String columnName = col.getName();
            String type = col.getType();
            int size = col.getSize();

            if (type.equals(DOUBLE)) {
                double value = obj.getDouble(columnName);
                appendToFileNthByte(directory + DB_FILE_NAME, value);

            }
            else if (type.equals(STRING)) {
                String value = obj.getString(columnName);
                while (value.length() < size) {
                    value = value.concat(" ");
                }
                appendToFileNthByte(directory + DB_FILE_NAME, value);
            }
        }
    }

    private static int firstDeletedRowIndex(String tableName) {
        int index = 0;
        for (MinimalRow<?> row : allMinimalRows.get(tableName)) {
            if (row.isDeleted()) {
                return index;
            }
            index++;
        }
        return index;
    }

    private static void cacheAllColumns(String tableName) throws IOException {
        try {
            if (!allColumns.get(tableName).isEmpty()) {
                return;
            }
        }
        catch (NullPointerException e) {

        }


        String directory = "Tables/" + tableName + "/";
        Scanner schemaScanner = new Scanner(new File(directory + SCHEMA_FILE_NAME));

        ArrayList<Column> cols = new ArrayList<>();
        String primary = schemaScanner.next();

        while (schemaScanner.hasNext()) {
            String columnName = schemaScanner.next();
            String type = schemaScanner.next();
            int size = schemaScanner.nextInt();
            if (columnName.equals(primary)) {
                cols.add(new Column(columnName, type, size, true));
            }
            else {
                cols.add(new Column(columnName, type, size, false));
            }
        }
        allColumns.put(tableName, cols);
        schemaScanner.close();
    }

    private static void showTable(String tableName, PrintStream out) throws IOException {
        cacheAllColumns(tableName);
        for (Row row : allRows.get(tableName)) {
            out.println(row.toString());
        }
    }


    private static void createTable(String tableName, String primary, ArrayList<Column> cols) throws IOException {
        createFolder("Tables/" + tableName);

        String directory = "Tables/" + tableName + "/";

        writeSchemaToFile(directory + SCHEMA_FILE_NAME, primary, cols);
        createFile(directory + DB_FILE_NAME);
        createFile(directory + INDEX_FILE_NAME);
    }

    private static void cacheAllColumnsFromJson(String tableName, String primary, JsonObject jsonObject) {
        ArrayList<Column> cols = new ArrayList<>();
        ArrayList<JsonValue<?>> jsonCols = jsonObject.getArrayList(COLUMNS);
        for (JsonValue<?> jsonValueCol : jsonCols) {
            if (jsonValueCol instanceof JsonNull) {
                continue;
            }

            JsonObject colObj = (JsonObject) jsonValueCol;
            Column c;
            if (colObj.getString(TYPE).equals(STRING)) {
                c = new Column(colObj.getString(COLUMN_NAME), colObj.getString(TYPE), colObj.getInt(LENGTH));
            }
            else {
                c = new Column(colObj.getString(COLUMN_NAME), colObj.getString(TYPE));
            }

            if (c.getName().equals(primary)) {
                c.setPrimary(true);
            }

            cols.add(c);
        }
        allColumns.put(tableName, cols);
    }


}

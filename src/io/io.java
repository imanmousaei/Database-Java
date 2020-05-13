package io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import model.*;
import model.JSON.*;


import static Engine.Engine.*;
import static io.FileIO.*;
import static io.Strings.*;

// why everything is static ??
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
            System.out.println("{\"Status\": \"OK\", \"Message\": \"Table Created Successfully.\"}");
        }
        else if (command.equals(INSERT)) {
            try {
                cacheAllColumns(tableName);
                cacheAllRows(tableName);
                cacheAllMinimalRows(tableName);
            }
            catch (EOFException eof){

            }

            insertToTable(tableName, obj.getObject(DATA));
            System.out.println("{\"Status\": \"OK\", \"Message\": \"Row Inserted Successfully.\"}");
        }
        else if (command.equals(DELETE)) {
            deleteRow(tableName, obj);
            System.out.println("{\"Status\": \"OK\", \"Message\": \"Row Deleted Successfully.\"}");
        }
        else if (command.equals(EDIT)) {
            // todo edit row
            cacheAllRows(tableName);
            System.out.println("{\"Status\": \"OK\", \"Message\": \"Row Updated Successfully.\"}");
        }
        else if (command.equals(SEARCH)) {
            // todo
//            filter()
        }
        else if (command.equals(SHOW_TABLE)) {
            cacheAllRows(tableName);
            cacheAllMinimalRows(tableName);
            showTable(tableName, System.out);
        }
    }

    private static void cacheAllRows(String tableName) throws IOException {// bad name
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
                    row.addCell(new Cell<Double>(value, col.getName()));
                }
                else if (col.getType().equals(STRING)) {
                    byte[] b = new byte[col.getSize()];
                    dbReader.readFully(b);
                    String value = new String(b, StandardCharsets.UTF_8);
                    row.addCell(new Cell<String>(value, col.getName()));
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

    private static void showTable(String tableName, PrintStream out) throws IOException {
        for (Row row : allRows.get(tableName)) {
            out.println(row.toString());
        }
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
                // extra hint: try factory design pattern  or builder design pattern
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

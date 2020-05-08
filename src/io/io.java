package io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import model.*;
import model.JSON.*;

import static io.FileIO.*;
import static io.Strings.*;

public class io {
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
        String tableName = obj.getString(TABLE);

        if (command.equals(CREATE_TABLE)) {
            createTable(tableName, obj.getString(PRIMARY), extractColumnFromJson(obj));
            System.out.println("Table Created Successfully.");
        }
        else if (command.equals(INSERT)) {
            insertToTable(tableName, obj.getObject(DATA));
            System.out.println("Row Inserted Successfully.");
        }
        else if (command.equals(DELETE)) {
            deleteRow(tableName, obj);
            System.out.println("Row Deleted Successfully.");
        }
        else if (command.equals(EDIT)) {
            // todo
        }
        else if (command.equals(SEARCH)) {
            // todo
        }
        else if (command.equals(SHOW_TABLE)) {
            showTable(tableName, System.out);
        }


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

    private static void insertToTable(String tableName, JsonObject obj) throws IOException {
        // inserts in the last row
        // todo insert in the first null row
        // todo check if the primary already exists
        String directory = "Tables/" + tableName + "/";

        Scanner schemaScanner = new Scanner(new File(directory + SCHEMA_FILE_NAME));

        String primary = schemaScanner.next();

        while (schemaScanner.hasNext()) {
            String columnName = schemaScanner.next();
            String type = schemaScanner.next();
            int size = schemaScanner.nextInt();

            if (type.equals(DOUBLE)) {
                double value = obj.getDouble(columnName);
                appendToFile(directory + DB_FILE_NAME, value);

                if (columnName.equals(primary)) {
                    appendToFile(directory + INDEX_FILE_NAME, false); // deleted = false
                    appendToFile(directory + INDEX_FILE_NAME, value);
                }

            }
            else if (type.equals(STRING)) {
                String value = obj.getString(columnName);
                while (value.length() < size) {
                    value = value.concat(" ");
                }
                appendToFile(directory + DB_FILE_NAME, value);

                if (columnName.equals(primary)) {
                    appendToFile(directory + INDEX_FILE_NAME, false); // deleted = false
                    appendToFile(directory + INDEX_FILE_NAME, value);
                }
            }
        }
        schemaScanner.close();
    }

    private static void showTable(String tableName, PrintStream out) throws IOException {
        String directory = "Tables/" + tableName + "/";

        RandomAccessFile dbReader = new RandomAccessFile(new File(directory + DB_FILE_NAME), "r");
        RandomAccessFile indexReader = new RandomAccessFile(new File(directory + INDEX_FILE_NAME), "r");

        int tableRowCount = getTableRowCount(tableName);

        for (int i = 0; i < tableRowCount; i++) {
            Scanner schemaScanner = new Scanner(new File(directory + SCHEMA_FILE_NAME));
            Column primaryCol = new Column(schemaScanner.next());

            while (schemaScanner.hasNext()) {
                String columnName = schemaScanner.next();
                String type = schemaScanner.next();
                int size = schemaScanner.nextInt();

                if (columnName.equals(primaryCol.getName())) {
                    primaryCol.setType(type);
                    primaryCol.setSize(size);
                }

                if (type.equals(DOUBLE)) {
                    double value = dbReader.readDouble();
                    out.print(value + "  ");
                }
                else if (type.equals(STRING)) {
                    byte[] b = new byte[size];
                    dbReader.readFully(b);
                    String value = new String(b, StandardCharsets.UTF_8);
                    out.print(value.trim() + "  ");
                }
            }
            schemaScanner.close();

            // print index file

            boolean deleted = indexReader.readBoolean();
            out.print(deleted);
            out.println();

            if (primaryCol.getType().equals(DOUBLE)) {
                double value = indexReader.readDouble();
//                out.print(value + " ");
            }
            else if (primaryCol.getType().equals(STRING)) {
                byte[] b = new byte[primaryCol.getSize()];
                indexReader.readFully(b);
                String value = new String(b, StandardCharsets.UTF_8);
//                out.print(value + " ");
            }
//            out.println();
        }
    }

    private static void createTable(String tableName, String primary, ArrayList<Column> cols) throws IOException {
        createFolder("Tables/" + tableName);

        String directory = "Tables/" + tableName + "/";

        writeSchemaToFile(directory + SCHEMA_FILE_NAME, primary, cols);
        createFile(directory + DB_FILE_NAME);
        createFile(directory + INDEX_FILE_NAME);
    }

    private static ArrayList<Column> extractColumnFromJson(JsonObject jsonObject) {
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
            cols.add(c);
        }
        return cols;
    }


}

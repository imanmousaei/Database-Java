package io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
            // todo
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

    private static void insertToTable(String tableName, JsonObject obj) throws IOException {
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
//                addPrimaryIndex();
                        appendToFile(directory + INDEX_FILE_NAME, getTableRowCount(tableName));
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
//                addPrimaryIndex();
                        appendToFile(directory + INDEX_FILE_NAME, getTableRowCount(tableName));
                        appendToFile(directory + INDEX_FILE_NAME, value);
                    }
            }
//            appendToFile(directory + DB_FILE_NAME, "\n");
        }
        schemaScanner.close();
    }

    private static void showTable(String tableName, PrintStream out) throws IOException {
        String directory = "Tables/" + tableName + "/";

        Scanner schemaScanner = new Scanner(new File(directory + SCHEMA_FILE_NAME));
        RandomAccessFile dbReader = new RandomAccessFile(new File(directory + DB_FILE_NAME), "rw");

        String primary = schemaScanner.next();

        for(int i=0;i<getTableRowCount(tableName);i++) {
            while (schemaScanner.hasNext()) {
                String columnName = schemaScanner.next();
                String type = schemaScanner.next();
                int size = schemaScanner.nextInt();

                if (type.equals(DOUBLE)) {
                    double value = dbReader.readDouble();
                    out.print(value + " ");
                }
                else if (type.equals(STRING)) {
                    byte[] b = new byte[size];
                    dbReader.readFully(b);
                    String value = new String(b, StandardCharsets.UTF_8);
                    out.print(value + " ");
                }
            }
            out.println();
        }
        schemaScanner.close();
    }

    private static void addPrimaryIndex() {
        // todo : add it in the first null row
    }

    private static int getIndex(String primary) { // O(n)
        return -1;
    }

    private static void createTable(String tableName, String primary, ArrayList<Column> cols) throws IOException {
        createFolder("Tables/" + tableName);

        String directory = "Tables/" + tableName + "/";

        writeSchemaToFile(directory + SCHEMA_FILE_NAME, primary, cols);
        createFile(directory + DB_FILE_NAME);
        createFile(directory + INDEX_FILE_NAME);
        appendToFile(directory + INDEX_FILE_NAME, "0");
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
            System.out.println(c);
            cols.add(c);
        }
        return cols;
    }


}

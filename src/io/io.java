package io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
        JsonObject obj = new JsonObject(input);
        obj.trimInput();
        obj.processInput();
        String command = obj.getString(COMMAND);
        String tableName = obj.getString(TABLE);

        if (command.equals(CREATE_TABLE)) {
            createTable(tableName, obj.getString(PRIMARY), extractColumnFromJson(obj));
        }
        else if (command.equals(INSERT)) {
            insertToTable(tableName,obj.getObject(DATA));
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


    }

    private static void insertToTable(String tableName,JsonObject obj) throws IOException {
        Scanner schemaScanner = new Scanner(new File("Tables/" + tableName));

        String primary = schemaScanner.nextLine();
        String directory = "Tables/" + tableName + "/" ;

//        appendToFile(directory + INDEX_FILE_NAME ,  ); todo

        while(schemaScanner.hasNextLine()){
            String columnName = schemaScanner.next();
            String type = schemaScanner.next();
            int size = schemaScanner.nextInt();
            if(type.equals(DOUBLE)){
                double value = obj.getDouble(columnName);
                appendToFile(DB_FILE_NAME,Double.toString(value));
            }
            else if(type.equals(STRING)){
                String value = obj.getString(columnName);
                while(value.length() < size){
                    value = value.concat(" ");
                }
                appendToFile(DB_FILE_NAME,value);
            }
            appendToFile(DB_FILE_NAME,"\n");
        }
        schemaScanner.close();
    }

    private static int searchPrimaryIndex(String primary){ // O(n)
        return -1;
    }

    private static int searchAnyIndex(String columnName){ // O(n)
        return -1;
    }

    private static void createTable(String tableName, String primary, ArrayList<Column> cols) throws IOException {
        createFolder("Tables/" + tableName);

        String directory = "Tables/" + tableName + "/" ;

        writeSchemaToFile(directory + SCHEMA_FILE_NAME, primary, cols);
        createFile(directory + DB_FILE_NAME);
        createFile(directory + INDEX_FILE_NAME);
        appendToFile(directory+INDEX_FILE_NAME,"0");
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

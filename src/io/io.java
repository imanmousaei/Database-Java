package io;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import model.Column;
import model.JsonObject;
import model.JsonValue;

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

    public static void processInput(String input) {
        JsonObject obj = new JsonObject(input);
        String command = obj.getString(COMMAND);
        if (command.equals(CREATE_TABLE)) {
            createTable(obj.getString(TABLE), extractColumnFromJson(obj));
        }
        else if (command.equals(INSERT)) {
            // todo
        }
        else if (command.equals(DELETE)) {
            // todo
        }
        else if (command.equals(EDIT)) {
            // todo
        }


    }

    private static void createTable(String tableName, ArrayList<Column> cols) {
        createFolder("Tables/" + tableName);
        // TODO
    }

    private static ArrayList<Column> extractColumnFromJson(JsonObject jsonObject) {
        ArrayList<Column> cols = new ArrayList<>();
        ArrayList<JsonValue<?>> jsonCols = jsonObject.getArrayList("Column");
        for (JsonValue<?> jsonValueCol : jsonCols) {
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

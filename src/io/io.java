package io;

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
        if (command.equals(CREATE_TABLE)) {
            createTable(obj.getString(TABLE), obj.getString(PRIMARY), extractColumnFromJson(obj));
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

    private static void createTable(String tableName, String primary, ArrayList<Column> cols) throws IOException {
        createFolder("Tables/" + tableName);
        writeSchemaToFile("Tables/" + tableName + "/schema.json", primary, cols);
        // TODO
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

package io;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import model.Column;
import model.JsonObject;

import static io.Commands.*;
import static io.FileIO.*;

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
            // todo
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

    private static


}

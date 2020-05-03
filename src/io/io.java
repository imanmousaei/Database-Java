package io;

import java.io.InputStream;
import java.util.Scanner;
import model.*;
import io.Commands.*;

public class io {
    public static String readJSONObject(InputStream in){
        Scanner scanner = new Scanner(in);
        String nextLine = scanner.nextLine();
        String json = nextLine;
        while(!nextLine.equals("}") ){
            nextLine = scanner.nextLine();
            json = json.concat(nextLine);
        }
        json = json.concat("}");
        return json;
    }

    public static void processInput(String input){
        JsonObject obj = new JsonObject(input);
        obj.getString(Command);


    }

    private static void createTable(String tableName , ArrayList<Column> cols){
        createFolder("Tables/"+tableName);
        // TODO
    }



}

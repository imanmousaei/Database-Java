package io;

import java.io.InputStream;
import java.util.Scanner;

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
}

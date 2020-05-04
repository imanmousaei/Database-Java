package io;

import model.Column;

import java.io.*;
import java.util.ArrayList;

public class FileIO {
    public static void createFolder(String folderName) {
        File folder = new File(folderName);
        folder.mkdir();
        // todo : dont let them create another table with the same name
    }

    public static void createFile(String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
    }

    public static void writeSchemaToFile(String fileName, String primary, ArrayList<Column> cols) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(fileName);
        out.println(primary);
        for (Column col : cols) {
            out.println(col);
        }
        out.close();
    }

    public static void appendToFile(String fileName,String textToAppend) throws IOException {
        BufferedWriter writer = new BufferedWriter(
                new FileWriter(fileName, true)  //Set true for append mode
        );
        writer.write(textToAppend);
        writer.close();
    }

}

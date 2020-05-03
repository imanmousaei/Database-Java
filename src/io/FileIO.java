package io;

import model.Column;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
}

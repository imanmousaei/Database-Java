package io;

import model.Column;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.Scanner;

import static io.Strings.*;

public class FileIO {
    public static void createFolder(String folderName) throws FileAlreadyExistsException {
        File folder = new File(folderName);
        if(folder.isDirectory()){
            throw new FileAlreadyExistsException("Table Name Already Used");
        }
        folder.mkdirs();
    }

    public static void createFile(String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
    }

    public static int getTableSize(String tableName) throws FileNotFoundException{
        Scanner scanner = new Scanner(new File(tableName+"/"+ INDEX_FILE_NAME));
        int size = scanner.nextInt();
        scanner.close();
        return size;
    }

    public static void writeSchemaToFile(String fileName, String primary, ArrayList<Column> cols) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(fileName);
        out.println(primary);
        for (Column col : cols) {
            out.println(col);
        }
        out.close();
    }

    public static void appendToFile(String fileName,String textToAppend) throws IOException { // todo do it with RandomAccessFile
        BufferedWriter writer = new BufferedWriter(
                new FileWriter(fileName, true)  //Set true for append mode
        );
        writer.write(textToAppend);
        writer.close();
    }
    public static void appendToFile(String fileName,double numberToAppend) throws IOException {
//        RandomAccessFile
    }

}

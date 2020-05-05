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
        if (folder.isDirectory()) {
            throw new FileAlreadyExistsException("Table Name Already Used");
        }
        folder.mkdirs();
    }

    public static void createFile(String fileName) throws IOException {
        File file = new File(fileName);
        file.createNewFile();
    }

    public static int getTableRowCount(String tableName) throws IOException { // todo do it with RandomAccessFile
        String fileName = "Tables/" + tableName + "/" + tableName;
        RandomAccessFile writer = new RandomAccessFile(new File(fileName), "rw");
        long rowCount = writer.length() / getRowSizeInByte(tableName) ;
        return (int)rowCount;
    }

    public static void writeSchemaToFile(String fileName, String primary, ArrayList<Column> cols) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(fileName);
        out.println(primary);
        for (Column col : cols) {
            out.println(col);
        }
        out.close();
    }

    public static void appendToFile(String fileName, String textToAppend) throws IOException {
        RandomAccessFile writer = new RandomAccessFile(new File(fileName), "rw");
        writer.writeBytes(textToAppend);
        writer.close();
    }

    public static void appendToFile(String fileName, double numberToAppend) throws IOException {
        RandomAccessFile writer = new RandomAccessFile(new File(fileName), "rw");
        writer.writeDouble(numberToAppend);
        writer.close();
    }

    public static int getRowSizeInByte(String tableName) throws FileNotFoundException {
        String directory = "Tables/" + tableName + "/";

        Scanner schemaScanner = new Scanner(new File(directory + SCHEMA_FILE_NAME));
        int rowSizeInByte = 0;

        while (schemaScanner.hasNext()) {
            String columnName = schemaScanner.next();
            String type = schemaScanner.next();
            int size = schemaScanner.nextInt();
            rowSizeInByte += size;
        }
        return rowSizeInByte;
    }

}

package io;

import com.sun.istack.internal.Nullable;
import model.Column;

import javax.xml.ws.Action;
import java.io.*;
import java.nio.charset.StandardCharsets;
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

    public static int getTableRowCount(String tableName) throws IOException {
        String fileName = "Tables/" + tableName + "/" + DB_FILE_NAME;
        RandomAccessFile writer = new RandomAccessFile(new File(fileName), "rw");
        long fileLen = writer.length();
        long rowSize = getRowSizeInByte(tableName);
        long rowCount = fileLen / rowSize;
        writer.close();
        return (int) rowCount;
    }

    public static Column getPrimary(String tableName) throws IOException {
        String directory = "Tables/" + tableName + "/";
        Scanner schemaScanner = new Scanner(new File(directory + SCHEMA_FILE_NAME));
        String primary = schemaScanner.next();

        while (schemaScanner.hasNext()) {
            String columnName = schemaScanner.next();
            String type = schemaScanner.next();
            int size = schemaScanner.nextInt();
            if (columnName.equals(primary)) {
                schemaScanner.close();
                return new Column(columnName, type, size);
            }
        }
        return null;
    }

    public static int getIndex(String tableName, String wantedPrimary) throws IOException { // O(n)
        String directory = "Tables/" + tableName + "/";
        RandomAccessFile indexReader = new RandomAccessFile(new File(directory + INDEX_FILE_NAME), "r"); // todo
        Column primaryCol = getPrimary(tableName);

        int tableRowCount = getTableRowCount(tableName);

        for (int i = 0; i < tableRowCount; i++) {
            boolean deleted = indexReader.readBoolean();
            byte[] b = new byte[primaryCol.getSize()];
            indexReader.readFully(b);
            String value = new String(b, StandardCharsets.UTF_8);

            if (value.equals(wantedPrimary)) {
                return i;
            }

        }

        return -1;
    }

    public static int getIndex(String tableName, double wantedPrimary) throws IOException { // O(n)
        String directory = "Tables/" + tableName + "/";
        RandomAccessFile indexReader = new RandomAccessFile(new File(directory + INDEX_FILE_NAME), "r"); // todo
        Column primaryCol = getPrimary(tableName);

        int tableRowCount = getTableRowCount(tableName);

        for (int i = 0; i < tableRowCount; i++) {
            boolean deleted = indexReader.readBoolean();
            double value = indexReader.readDouble();

            if (value == wantedPrimary) {
                return i;
            }
        }

        return -1;
    }

    public static void writeSchemaToFile(String fileName, String primary, ArrayList<Column> cols) throws FileNotFoundException {
        PrintWriter out = new PrintWriter(fileName);
        out.println(primary);
        for (Column col : cols) {
            out.println(col);
        }
        out.close();
    }

    public static void appendToFileNthByte(String fileName, String textToAppend, long n) throws IOException { // appends in  binary format
        RandomAccessFile writer = new RandomAccessFile(new File(fileName), "rw");
        if (n == -1) {
            n = writer.length();
        }

        writer.seek(n);
        byte[] b = textToAppend.getBytes();
        writer.write(b);
        writer.close();
    }

    public static void appendToFileNthByte(String fileName, boolean bool, long n) throws IOException { // appends in  binary format
        RandomAccessFile writer = new RandomAccessFile(new File(fileName), "rw");
        if (n == -1) {
            n = writer.length();
        }
        writer.seek(n);
        writer.writeBoolean(bool);
        writer.close();
    }

    public static void appendToFileNthByte(String fileName, double numberToAppend, long n) throws IOException { // appends in  binary format
        RandomAccessFile writer = new RandomAccessFile(new File(fileName), "rw");
        if (n == -1) {
            n = writer.length();
        }

        writer.seek(n);
        writer.writeDouble(numberToAppend);
        writer.close();
    }

    public static int getRowSizeInByte(String tableName) throws FileNotFoundException {
        String directory = "Tables/" + tableName + "/";

        Scanner schemaScanner = new Scanner(new File(directory + SCHEMA_FILE_NAME));
        schemaScanner.nextLine();
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

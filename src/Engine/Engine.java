package Engine;

import model.Column;
import model.Filterable;
import model.JSON.JsonObject;
import model.MinimalRow;
import model.Row;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

import static io.FileIO.*;
import static io.Strings.*;
import static io.io.*;

public class Engine {
    public static void deleteRow(String tableName, JsonObject obj) throws IOException {
        String directory = "Tables/" + tableName + "/";
        RandomAccessFile writer = new RandomAccessFile(new File(directory + INDEX_FILE_NAME), "rw");
        Column primaryCol = getPrimary(tableName);
        int indexRowSize = primaryCol.getSize() + 1; // bool deleted : 1 Byte

        if (primaryCol.getType().equals(STRING)) {
            String wantedPrimary = obj.getString(primaryCol.getName());
            int index = getIndex(tableName, wantedPrimary);
            writer.seek(index * indexRowSize);
        }
        else {
            double wantedPrimary = obj.getDouble(primaryCol.getName());
            int index = getIndex(tableName, wantedPrimary);
            writer.seek(index * indexRowSize);
        }

        writer.writeBoolean(true); // deleted = true
    }

    public static void insertRow(String tableName, JsonObject obj) throws IOException {
        // todo check if the primary already exists (validation Responsibility)
        Column primaryCol = getPrimary(tableName);
        int firstDeletedRow = firstDeletedRowIndex(tableName);
        insertToNthRow(obj,tableName,firstDeletedRow,primaryCol);
    }

    public static void editRow(String tableName, JsonObject obj) throws IOException {
        Column primaryCol = getPrimary(tableName);
        int rowIndex = 0;
        if(primaryCol.getType().equals(STRING)){
            rowIndex = getIndex(tableName,obj.getString(primaryCol.getName()));
        }
        else if(primaryCol.getType().equals(DOUBLE)){
            rowIndex = getIndex(tableName,obj.getDouble(primaryCol.getName()));
        }
        insertToNthRow(obj,tableName,rowIndex,primaryCol);
    }

    private static void insertToNthRow(JsonObject obj,String tableName,int rowIndex, Column primaryCol) throws IOException {
        String directory = "Tables/" + tableName + "/";
        int indexRowSize = primaryCol.getSize() + 1; // bool deleted : 1 Byte
        int sizeUsed = 0;
        appendToFileNthByte(directory + INDEX_FILE_NAME, false, rowIndex * indexRowSize + sizeUsed); // deleted = false

        sizeUsed += 1;
        insertValue(directory + INDEX_FILE_NAME, obj, rowIndex * indexRowSize + sizeUsed, primaryCol);

        int rowSize = getRowSizeInByte(tableName);
        sizeUsed = 0;

        for (Column col : allColumns.get(tableName)) {
            insertValue(directory + DB_FILE_NAME, obj, rowIndex * rowSize + sizeUsed, col);
            sizeUsed += col.getSize();;
        }
    }

    private static void insertValue(String fileName, JsonObject obj, long byteToAppend, Column col) throws IOException {
        if (col.getType().equals(DOUBLE)) { // What todo if there is int type?
            double value = obj.getDouble(col.getName());
            appendToFileNthByte(fileName, value, byteToAppend);
        }
        else {
            String value = obj.getString(col.getName());
            while (value.length() < col.getSize()) {
                value = value.concat(" ");
            }
            appendToFileNthByte(fileName, value, byteToAppend);
        }
    }


    public static int firstDeletedRowIndex(String tableName) { // bad name ...
        int index = 0;
        ArrayList<MinimalRow<?>> minimals = allMinimalRows.get(tableName);
        if (minimals == null) {
            return 0;
        }
        for (MinimalRow<?> row : minimals) {
            if (row.isDeleted()) {
                return index;
            }
            index++;
        }
        return index;
    }

    public static void cacheAllColumns(String tableName) throws IOException {
        try {
            if (!allColumns.get(tableName).isEmpty()) {
                return;
            }
        }
        catch (NullPointerException e) {

        }


        String directory = "Tables/" + tableName + "/";
        Scanner schemaScanner = new Scanner(new File(directory + SCHEMA_FILE_NAME));

        ArrayList<Column> cols = new ArrayList<>();
        String primary = schemaScanner.next();

        while (schemaScanner.hasNext()) {
            String columnName = schemaScanner.next();
            String type = schemaScanner.next();
            int size = schemaScanner.nextInt();
            if (columnName.equals(primary)) {
                cols.add(new Column(columnName, type, size, true));
            }
            else {
                cols.add(new Column(columnName, type, size, false));
            }
        }
        allColumns.put(tableName, cols);
        schemaScanner.close();
    }


    public static void createTable(String tableName, String primary, ArrayList<Column> cols) throws IOException {
        createFolder("Tables/" + tableName);// check if "tables" folder exists

        String directory = "Tables/" + tableName + "/";

        writeSchemaToFile(directory + SCHEMA_FILE_NAME, primary, cols);
        createFile(directory + DB_FILE_NAME);
        createFile(directory + INDEX_FILE_NAME);
    }

    public static ArrayList<Row> filter(String tableName, Filterable obj) {
        ArrayList<Row> wantedRows = new ArrayList<>();
        for (Row r : allRows.get(tableName)) {
            if (obj.isAcceptable(r)) {
                wantedRows.add(r);
            }
        }
        return wantedRows;
    }


}
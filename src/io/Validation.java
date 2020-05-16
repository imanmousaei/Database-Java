package io;

import Engine.InvalidJsonException;
import Engine.NoSuchRowException;
import Engine.PrimaryAlreadyExistsException;
import model.Column;
import model.JSON.JsonObject;

import java.io.IOException;
import java.util.NoSuchElementException;

import static io.FileIO.getIndex;
import static io.Strings.DOUBLE;
import static io.Strings.STRING;

public class Validation {
    public static void validatePrimary(String tableName, Column primaryCol, JsonObject obj) throws PrimaryAlreadyExistsException, IOException {
        int index = -1;
        if (primaryCol.getType().equals(STRING)) {
            index = getIndex(tableName, obj.getString(primaryCol.getName()));
        }
        else if (primaryCol.getType().equals(DOUBLE)) {
            index = getIndex(tableName, obj.getDouble(primaryCol.getName()));
        }

        if (index != -1) {
            throw new PrimaryAlreadyExistsException("Can't have multiple rows with same Primary value");
        }
    }

    public static void validateJson(String json) throws InvalidJsonException {
        JsonObject obj = new JsonObject(json);
        try {
            obj.trimInput();
            obj.processInput();
        }
        catch (NullPointerException npe){
            throw new InvalidJsonException("Please Enter a valid JSON");
        }
    }

    public static void validateRowExistance(int index) throws NoSuchRowException {
        if(index==-1){
            throw new NoSuchRowException("Wanted row doesn't exist");
        }
    }


}

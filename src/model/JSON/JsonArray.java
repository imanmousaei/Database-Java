package model.JSON;

import java.util.ArrayList;

public class JsonArray extends JsonValue<ArrayList<JsonValue<?>>> {
    JsonArray(ArrayList<JsonValue<?>> value) {
        super(value);
    }

    public void print() {
        for (int i = 0; i < value.size(); i++) {
            System.out.print(value.get(i).value + " ");
        }
    }
}
package model.JSON;

import java.util.ArrayList;
import java.util.HashMap;

public class JsonObject extends JsonValue<HashMap<String, JsonValue<?>>> {
    private String json;
    private int index = 0;

    public JsonObject(String json) {
        value = new HashMap<>();
        this.json = json;
//        trimInput();
//        processInput();
    }

    // <getters>
    public JsonValue<?> getValue(String key) {
        return value.get(key);
    }

    public double getDouble(String key) {
        JsonDouble tmp = (JsonDouble) value.get(key);
        return tmp.getValue();
    }

    public int getInt(String key) {
        return (int)this.getDouble(key);
    }

    public String getString(String key) {
        JsonString tmp = (JsonString) value.get(key);
        return tmp.getValue();
    }

    public boolean getBool(String key) {
        JsonBool tmp = (JsonBool) value.get(key);
        return tmp.getValue();
    }

    public ArrayList<JsonValue<?>> getArrayList(String key) {
        JsonArray tmp = (JsonArray) value.get(key);
        return tmp.getValue();
    }

    public JsonObject getObject(String key) {
        return (JsonObject) value.get(key);
    }
    // </getters>


    public void trimInput() {
        StringBuilder sb = new StringBuilder(json);
        sb.deleteCharAt(0);
//        sb.deleteCharAt(sb.length()-1);

        json = sb.toString();
        json = json.trim();
        json = json.replaceAll("\\s+", "");
    }

    private String getInsideString() {
        while (json.charAt(index) != '\"') {
            index++;
        }
        index++;
        int beginIndex = index, endIndex;
        while (index < json.length() - 1 && json.charAt(index) != '\"') {
            index++;
        }

        endIndex = index;
        index += 2; // " and :

        return json.substring(beginIndex, endIndex);
    }

    private String getInsideObject() {
        while (json.charAt(index) != '{') {
            index++;
        }
        index++;
        int cnt = 1, beginIndex = index, endIndex;
        while (index < json.length() - 1) {
            if (json.charAt(index) == '}') {
                cnt--;
            } else if (json.charAt(index) == '{') {
                cnt++;
            }

            if (cnt == 0) {
                break;
            }

            index++;
        }

        endIndex = index + 1;
        index += 2; // } and :

        return json.substring(beginIndex, endIndex);
    }

    private JsonValue<?> getInsideValue() {
        if (index >= json.length()) {
            return new JsonNull(null);
        } else if (json.charAt(index) == '\"') {
            return new JsonString(getInsideString());
        } else if (Character.toLowerCase(json.charAt(index)) == 'f') {
            index += 6; // false,
            return new JsonBool(false);
        } else if (Character.toLowerCase(json.charAt(index)) == 't') {
            index += 5; // true,
            return new JsonBool(true);
        } else if (Character.toLowerCase(json.charAt(index)) == 'n') {
            index += 5; // null,
            return new JsonNull(null);
        } else if (json.charAt(index) == '{') {
            JsonObject object = new JsonObject(getInsideObject());
            object.processInput();

            return object;
        } else if (json.charAt(index) == '[') {
            ArrayList<JsonValue<?>> temp = new ArrayList<>();
            index++;
            while (index < json.length() - 1 && json.charAt(index) != ']') {
                temp.add(getInsideValue());
            }
            index++;
            return new JsonArray(temp);
        } else if (json.charAt(index) == '-' || Character.isDigit(json.charAt(index))) {
            int beginIndex = index, endIndex;

            if (json.charAt(index) == '-') {
                index++;
            }

            while (Character.isDigit(json.charAt(index))) {
                index++;
            }
            endIndex = index;

            if (json.charAt(index) == '.') {
                index++;
                while (Character.isDigit(json.charAt(index))) {
                    index++;
                }
                endIndex = index;
            }

            index++; // ,
            return new JsonDouble(Double.parseDouble(json.substring(beginIndex, endIndex)));

        }

        index++;
        return getInsideValue();
    }

    public void processInput() {
        while (index < json.length() && json.charAt(index) != '}') {
            String key = getInsideString();
            JsonValue<?> val = getInsideValue();
            this.value.put(key, val);
        }
        index++;
    }


}
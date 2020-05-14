package model;


public class Cell<T> {
    T value;
    int size;
    String type;
    String columnName;


    public Cell(T value, String columnName) {
        this.value = value;
        this.columnName = columnName;
    }

    public String getColumnName() {
        return columnName;
    }

    public T getValue() {
        return value;
    }

    @Override
    public String toString() {
        String str = "\"" + columnName + "\" : ";
        if (value instanceof String) {
            str = str.concat("\"" + ((String) value).trim() + "\"");
        }
        else {
            str = str.concat(value.toString());
        }
        return str;
    }
}

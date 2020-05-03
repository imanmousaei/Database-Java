package model.JSON;

public class JsonValue<T> {
    protected T value;

    JsonValue() {
    }

    JsonValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public void print() {
        System.out.print(value);
    }
}
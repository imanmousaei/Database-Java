package model;

public class Cell<T> {
    T value;
    int size;
    String type;

    public Cell(T value) {
        this.value = value;
    }

    public T getValue(){
        return value;
    }

    @Override
    public String toString() {
        if(value instanceof String){
            return ((String) value).trim() + "  ";
        }
        return value + "  ";
    }
}

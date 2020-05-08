package model;

public class Column {
    private String name;
    private String type;
    private int size;

    public Column(String name) {
        this.name = name;
    }

    public Column(String name, String type, int size) {
        this.name = name;
        this.type = type;
        this.size = size;
    }

    public Column(String name, String type) {
        this.name = name;
        this.type = type;
        this.size = Double.BYTES;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return name + " " + type + " " + size;
    }

    public String toJson() {
        return "{" +
                " \"columnName\" : \"" + name + '\"' +
                ", \"type\" : \"" + type + '\"' +
                ", \"size\" : " + size +
                '}';
    }
}

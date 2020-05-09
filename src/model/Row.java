package model;

import java.util.ArrayList;

public class Row {
    private ArrayList<Cell<?>> cells = new ArrayList<>();
    private boolean deleted;

    public Cell<?> getCell(int index) {
        return cells.get(index);
    }

    public void addCell(Cell<?> cell){
        cells.add(cell);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public String toString() {
        String tmp = deleted + " ";
        for(Cell<?> cell : cells){
            tmp = tmp.concat(cell.toString());
        }
        return tmp;
    }
}

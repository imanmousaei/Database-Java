package model;

import java.util.ArrayList;

public class Row {
    private ArrayList<Cell<?>> cells = new ArrayList<>();
    private boolean deleted;

    public Cell<?> getCell(int index) {
        return cells.get(index);
    }

    public void addCell(Cell<?> cell) {
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
        String str = "{ \"deleted\" : " + deleted + " , ";
        for (int i = 0; i < cells.size(); i++) {
            Cell<?> cell = cells.get(i);
            str = str.concat(cell.toString());
            if (i + 1 < cells.size()) {
                str = str.concat(" , ");
            }
        }
        return str + " }";
    }
}

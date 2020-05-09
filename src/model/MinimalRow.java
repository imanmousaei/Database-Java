package model;

public class MinimalRow<T> {
    private T primary;
    private boolean deleted;

    public MinimalRow(T primary, boolean deleted) {
        this.primary = primary;
        this.deleted = deleted;
    }

    public T getPrimary() {
        return primary;
    }

    public void setPrimary(T primary) {
        this.primary = primary;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}

package model.ADT;

import java.util.ArrayList;
import java.util.List;

public class MyList <T> implements MyIList<T> {
    private List<T> list;
    public MyList() {
        this.list = new ArrayList<T>();
    }
    public void add(T t) {
        this.list.add(t);
    }
    public List<T> getAll() {
        return this.list;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(" {\n");
        for (T t : this.list) {
            sb.append(t.toString()).append("\n");
        }
        sb.append("}\n");
        return sb.toString();
    }
}

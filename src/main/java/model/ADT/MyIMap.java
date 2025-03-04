package model.ADT;

import java.util.Map;
import java.util.Set;

public interface MyIMap <K, V>{
    public void put(K key, V value);
    public V getValue(K key);
    public boolean containsKey(K key);
    public Set<K> getKeys();
    public void remove(K key);
    public MyIMap<K, V> deepCopy();
    public Map<K, V> getContent();
}

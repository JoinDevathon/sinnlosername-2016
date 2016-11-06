package org.devathon.contest2016.util;

import java.io.Serializable;

/**
 * Created by Florian on 06.11.16 in org.devathon.contest2016.util
 */
public class Pair<K, V> implements Serializable {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

}

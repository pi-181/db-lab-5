package com.demkom58.db_lab_5.util;

public class NamedObject<T> {
    private final String name;
    private final T object;

    public NamedObject(String name, T object) {
        this.name = name;
        this.object = object;
    }

    public String getName() {
        return name;
    }

    public T getObject() {
        return object;
    }

    @Override
    public String toString() {
        return name;
    }
}

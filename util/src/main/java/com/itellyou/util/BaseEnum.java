package com.itellyou.util;

public interface BaseEnum<E extends Enum<?>, T> {
    T getValue();

    String toString();
}

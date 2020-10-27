package com.itellyou.util;

public interface BaseEnum<E extends Enum<E>, T> {
    T getValue();

    String toString();
}

package com.pretzel.dev.villagertradelimiter.lib;

public interface Callback<T> {
    /**
     * Callback function
     * @param result Any type of result to be passed into the callback function
     * @param args Any extra arguments to be passed into the callback function
     */
    void call(T result, String... args);
}

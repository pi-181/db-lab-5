package com.demkom58.db_lab_5.util;

@FunctionalInterface
public interface ThrowableRunnable {
    void run() throws Throwable;
}

package com.wentong.ratelimiter.utils;

public final class AssertUtils {

    public static void check(boolean expression, String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

}

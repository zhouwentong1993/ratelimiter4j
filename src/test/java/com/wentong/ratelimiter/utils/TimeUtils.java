package com.wentong.ratelimiter.utils;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;

public final class TimeUtils {

    @SneakyThrows
    public static void sleep(int timeInMills) {
        TimeUnit.MILLISECONDS.sleep(timeInMills);
    }

    public static long now() {
        return System.currentTimeMillis();
    }

}

package com.wentong.ratelimiter.algorithm.single;

import com.google.common.base.Ticker;
import com.wentong.ratelimiter.algorithm.RateLimiter;
import com.wentong.ratelimiter.exception.InternalErrorException;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class SlidingWindowRateLimiterTest {

    @Test
    public void testTryAcquire() throws Exception {
        Ticker ticker = Mockito.mock(Ticker.class);
        when(ticker.read()).thenReturn(0L);
        RateLimiter ratelimiter = new SlidingWindowRateLimiter(10);

        when(ticker.read()).thenReturn(100 * 1000 * 1000L);
        boolean passed1 = ratelimiter.tryAcquire();
        assertTrue(passed1);

        when(ticker.read()).thenReturn(200 * 1000 * 1000L);
        boolean passed2 = ratelimiter.tryAcquire();
        assertTrue(passed2);

        when(ticker.read()).thenReturn(300 * 1000 * 1000L);
        boolean passed3 = ratelimiter.tryAcquire();
        assertTrue(passed3);

        when(ticker.read()).thenReturn(400 * 1000 * 1000L);
        boolean passed4 = ratelimiter.tryAcquire();
        assertTrue(passed4);

        when(ticker.read()).thenReturn(500 * 1000 * 1000L);
        boolean passed5 = ratelimiter.tryAcquire();
        assertTrue(passed5);

        when(ticker.read()).thenReturn(600 * 1000 * 1000L);
        boolean passed6 = ratelimiter.tryAcquire();
        assertFalse(passed6);

        when(ticker.read()).thenReturn(1001 * 1000 * 1000L);
        boolean passed7 = ratelimiter.tryAcquire();
        assertTrue(passed7);

        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(10);

    }

    @Test
    public void multiThread() throws Exception {
        SlidingWindowRateLimiter limiter = new SlidingWindowRateLimiter(10000);

        int threadCount = 150;
        int runPerThread = 100;
        AtomicInteger acquireCount = new AtomicInteger(0);
        AtomicInteger unAcquireCount = new AtomicInteger(0);

        CountDownLatch startLatch = new CountDownLatch(threadCount);
        CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                startLatch.countDown();
                for (int j = 0; j < runPerThread; j++) {
                    try {
                        if (limiter.tryAcquire()) {
                            acquireCount.incrementAndGet();
                        } else {
                            unAcquireCount.incrementAndGet();
                        }
                    } catch (InternalErrorException e) {
                        e.printStackTrace();
                    }
                }
                endLatch.countDown();
            }, "Thread:" + i).start();
        }
        startLatch.await();
        endLatch.await();
        assertEquals(10000, acquireCount.intValue());
        assertEquals(5000, unAcquireCount.intValue());

    }
}
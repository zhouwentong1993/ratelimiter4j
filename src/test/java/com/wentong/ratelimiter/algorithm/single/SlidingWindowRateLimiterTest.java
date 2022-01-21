package com.wentong.ratelimiter.algorithm.single;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.wentong.ratelimiter.algorithm.RateLimiter;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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
}
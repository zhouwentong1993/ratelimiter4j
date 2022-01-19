package com.wentong.ratelimiter.algorithm.single;

import com.wentong.ratelimiter.algorithm.RateLimiter;
import com.wentong.ratelimiter.exception.InternalErrorException;

/**
 * 滑动窗口限流
 */
public class SlidingWindowRateLimiter implements RateLimiter {

    @Override
    public boolean tryAcquire() throws InternalErrorException {
        return false;
    }
}

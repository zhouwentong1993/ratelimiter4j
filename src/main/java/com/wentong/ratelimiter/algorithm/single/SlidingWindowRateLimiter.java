package com.wentong.ratelimiter.algorithm.single;

import com.wentong.ratelimiter.algorithm.RateLimiter;
import com.wentong.ratelimiter.exception.InternalErrorException;
import com.wentong.ratelimiter.utils.AssertUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 滑动窗口限流
 */
public class SlidingWindowRateLimiter<T> implements RateLimiter {

    private final int qps;
    private final int sampleCount;
    private final long totalTimeInMills;
    private final AtomicReferenceArray<BucketWrap> array;
    private final Lock lock = new ReentrantLock();


    public SlidingWindowRateLimiter(int qps) {
        this(10, qps, 1000);
    }

    public SlidingWindowRateLimiter(int sampleCount, int qps, long totalTimeInMills) {
        AssertUtils.check(sampleCount > 0, "SampleCount must greater than 0");
        AssertUtils.check(qps > 0, "QPS must greater than 0");
        AssertUtils.check(totalTimeInMills > 0, "TotalTimeInMills must greater than 0");
        AssertUtils.check(totalTimeInMills % sampleCount == 0, "TotalTimeInMills must be sampleCount's * N");
        this.qps = qps;
        this.sampleCount = sampleCount;
        this.totalTimeInMills = totalTimeInMills;
        this.array = new AtomicReferenceArray<>(sampleCount);
    }

    public BucketWrap currentBuck(long timeInMills) {
        AssertUtils.check(timeInMills > 0, "timeInMills must greater than 0");
        int index = (int) (timeInMills % array.length());
        long bucketStartTime = (timeInMills - timeInMills % (totalTimeInMills / sampleCount));

        while (true) {
            BucketWrap oldBucket = array.get(index);
            if (oldBucket == null) {
                BucketWrap newBucket = new BucketWrap(bucketStartTime, new LongAdder());
                newBucket.setBucketStartTime(bucketStartTime);
                array.compareAndSet(index, null, newBucket);
                return newBucket;
            } else if (oldBucket.getBucketStartTime() < bucketStartTime) {
                if (lock.tryLock()) {
                    BucketWrap newBucket = new BucketWrap(bucketStartTime, new LongAdder());
                    array.compareAndSet(index, oldBucket, newBucket);
                    return newBucket;
                } else {
                    Thread.yield();
                }
            } else if (oldBucket.getBucketStartTime() == bucketStartTime) {
                return oldBucket;
            } else {
                throw new IllegalArgumentException("can't happens");
            }
        }
    }


    @Override
    public boolean tryAcquire() throws InternalErrorException {
        return false;
    }

    @Data
    @AllArgsConstructor
    static class BucketWrap {
        long bucketStartTime;
        LongAdder count;
    }

}


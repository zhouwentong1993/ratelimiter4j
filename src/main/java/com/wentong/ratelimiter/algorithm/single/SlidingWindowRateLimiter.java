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
public class SlidingWindowRateLimiter implements RateLimiter {

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
                if (array.compareAndSet(index, null, newBucket)) {
                    return newBucket;
                } else {
                    Thread.yield();
                }
            } else if (oldBucket.getBucketStartTime() < bucketStartTime) {
                if (lock.tryLock()) {
                    try {
                        resetBucket(oldBucket, bucketStartTime);
                    } finally {
                        lock.unlock();
                    }
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

    private int sum(long timeInMills) {
        int sum = 0;
        for (int i = 0; i < array.length(); i++) {
            BucketWrap bucketWrap = array.get(i);
            if (validBucket(bucketWrap, timeInMills)) {
                sum += bucketWrap.count.sum();
            }
        }
        return sum;
    }

    private boolean validBucket(BucketWrap bucketWrap, long timeInMills) {
        return bucketWrap != null && (timeInMills - bucketWrap.bucketStartTime) > (totalTimeInMills / sampleCount);
    }

    private void resetBucket(BucketWrap bucketWrap, long startTime) {
        bucketWrap.count.reset();
        bucketWrap.bucketStartTime = startTime;
    }

    private void add(long timeInMills) {
        BucketWrap bucketWrap = currentBuck(timeInMills);
        bucketWrap.count.add(1);
    }


    @Override
    public boolean tryAcquire() throws InternalErrorException {
        long now = System.currentTimeMillis();
        int sum = sum(now);
        if (sum + 1 < qps) {
            add(now);
            return true;
        } else {
            return false;
        }
    }

    @Data
    @AllArgsConstructor
    static class BucketWrap {
        long bucketStartTime;
        LongAdder count;
    }

}


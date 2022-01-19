package com.wentong.ratelimiter.algorithm;

import com.wentong.ratelimiter.exception.InternalErrorException;

public interface RateLimiter {

  /**
   * try to acquire an access token.
   * 
   * @return true if invoker get an access token successfully, otherwise, return false.
   * @throws InternalErrorException if some internal error occurs.
   */
  boolean tryAcquire() throws InternalErrorException;

}

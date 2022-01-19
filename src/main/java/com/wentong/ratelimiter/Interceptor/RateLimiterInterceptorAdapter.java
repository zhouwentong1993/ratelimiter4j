package com.wentong.ratelimiter.Interceptor;

import com.wentong.ratelimiter.rule.ApiLimit;

/**
 * Abstract adapter class for the {@link RateLimiterInterceptor} interface, for simplified
 * implementation of pre-only/post-only interceptors.
 */
public abstract class RateLimiterInterceptorAdapter implements RateLimiterInterceptor {

  @Override
  public void beforeLimit(String appId, String api) {}

  @Override
  public void afterLimit(String appId, String api, ApiLimit apiLimit, boolean result,
      Exception ex) {}

}

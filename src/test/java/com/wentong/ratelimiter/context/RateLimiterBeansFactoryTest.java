package com.wentong.ratelimiter.context;

import com.wentong.ratelimiter.Interceptor.RateLimiterInterceptor;
import com.wentong.ratelimiter.redis.DefaultJedisTaskExecutor;
import com.wentong.ratelimiter.redis.JedisTaskExecutor;
import com.wentong.ratelimiter.rule.RateLimitRule;
import com.wentong.ratelimiter.rule.UrlRateLimitRule;
import com.wentong.ratelimiter.rule.parser.RuleConfigParser;
import com.wentong.ratelimiter.rule.source.RuleConfigSource;
import com.wentong.ratelimiter.rule.source.UniformRuleConfigMapping;

import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

@Test
public class RateLimiterBeansFactoryTest {

  private RateLimiterBeansFactory factory = RateLimiterBeansFactory.BEANS_CONTEXT;

  public void testObtainLimiterInterceptors() {
    List<RateLimiterInterceptor> interceptors = factory.obtainLimiterInterceptors(null);
    assertNotNull(interceptors);

    List<RateLimiterInterceptor> assignedInteceptors = new ArrayList<>();
    interceptors = factory.obtainLimiterInterceptors(assignedInteceptors);
    assertSame(interceptors, assignedInteceptors);
  }

  public void testObtainRuleConfigSource() {
    RuleConfigSource source = factory.obtainRuleConfigSource(null);
    assertNotNull(source);

    RuleConfigSource assignedSource = new RuleConfigSource() {
      @Override
      public UniformRuleConfigMapping load() {
        return null;
      }
    };

    source = factory.obtainRuleConfigSource(assignedSource);
    assertSame(source, assignedSource);
  }

  public void testObtainRuleConfigParser() {
    RuleConfigParser parser = factory.obtainRuleConfigParser(null);
    assertNotNull(parser);

    RuleConfigParser assignedParser = new RuleConfigParser() {
      @Override
      public UniformRuleConfigMapping parse(String configurationText) {
        return null;
      }

      @Override
      public UniformRuleConfigMapping parse(InputStream in) {
        return null;
      }
    };

    parser = factory.obtainRuleConfigParser(assignedParser);
    assertSame(parser, assignedParser);
  }

  public void testObtainJedisTaskExecutor() {
    JedisTaskExecutor assignedExecutor = new DefaultJedisTaskExecutor("1.1.1.1:111", 111, null);
    JedisTaskExecutor executor = factory.obtainJedisTaskExecutor(assignedExecutor);
    assertSame(executor, assignedExecutor);
  }

  public void testObtainUrlRateLimitRule() {
    RateLimitRule rule = factory.obtainUrlRateLimitRule(null);
    assertNotNull(rule);

    RateLimitRule assignedRule = new UrlRateLimitRule();
    rule = factory.obtainUrlRateLimitRule(assignedRule);
    assertSame(rule, assignedRule);
  }

}

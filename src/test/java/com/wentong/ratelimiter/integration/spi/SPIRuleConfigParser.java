package com.wentong.ratelimiter.integration.spi;

import java.io.InputStream;

import com.wentong.ratelimiter.extension.Order;
import com.wentong.ratelimiter.rule.parser.RuleConfigParser;
import com.wentong.ratelimiter.rule.source.UniformRuleConfigMapping;

@Order(Order.HIGHEST_PRECEDENCE)
public class SPIRuleConfigParser implements RuleConfigParser {

  @Override
  public UniformRuleConfigMapping parse(String configurationText) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public UniformRuleConfigMapping parse(InputStream in) {
    // TODO Auto-generated method stub
    return null;
  }

}

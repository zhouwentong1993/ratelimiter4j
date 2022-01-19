package com.wentong.ratelimiter.rule.source;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TODO(zheng): add more tests.
 */
@Test
public class FileRuleConfigSourceTest {

  public void testLoad() {
    RuleConfigSource source = new FileRuleConfigSource();
    UniformRuleConfigMapping ruleConfigMapping = source.load();
    Assert.assertNotNull(ruleConfigMapping);
  }

}

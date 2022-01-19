package com.wentong.ratelimiter.env.loader;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.wentong.ratelimiter.env.PropertySource;
import org.hamcrest.collection.IsMapContaining;
import org.testng.annotations.Test;

@Test
public class JvmPropertySourceLoaderTest {

  public void testLoad() {
    Map<String, String> preSetValues = new HashMap<>();
    preSetValues.put("ratelimiter.rule.source", "zookeeper");
    preSetValues.put("ratelimiter.rule.parser", "yaml");
    preSetValues.put("ratelimiter.redis.ip", "12.34.22.123");

    Properties props = System.getProperties();
    for (Map.Entry<String, String> v : preSetValues.entrySet()) {
      props.setProperty(v.getKey(), v.getValue());
    }
    System.setProperties(props);

    PropertySourceLoader loader = new JvmPropertySourceLoader();
    PropertySource propertySource = loader.load();
    Map<String, Object> properties = propertySource.getProperties();

    for (Map.Entry<String, String> v : preSetValues.entrySet()) {
      assertThat(properties, IsMapContaining.hasEntry(v.getKey(), v.getValue()));
    }
    assertNull(properties.get("invalid-key"));
  }
}

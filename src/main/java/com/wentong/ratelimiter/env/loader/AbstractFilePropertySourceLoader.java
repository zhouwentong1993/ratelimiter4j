package com.wentong.ratelimiter.env.loader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.wentong.ratelimiter.env.PropertySource;
import com.wentong.ratelimiter.env.io.DefaultResourceLoader;
import com.wentong.ratelimiter.env.io.Resource;
import com.wentong.ratelimiter.env.io.ResourceLoader;
import com.wentong.ratelimiter.env.resolver.PropertiesPropertySourceResolver;
import com.wentong.ratelimiter.env.resolver.PropertySourceResolver;
import com.wentong.ratelimiter.env.resolver.YamlPropertySourceResolver;
import com.wentong.ratelimiter.exception.ConfigurationResolveException;

/**
 * Abstract property loader to load environment configuration from file.
 */
public abstract class AbstractFilePropertySourceLoader implements PropertySourceLoader {

  private ResourceLoader resourceLoader;

  private List<PropertySourceResolver> resolvers;

  public AbstractFilePropertySourceLoader() {
    this(null);
  }

  public AbstractFilePropertySourceLoader(ResourceLoader resourceLoader) {
    if (resourceLoader != null) {
      this.resourceLoader = resourceLoader;
    } else {
      this.resourceLoader = new DefaultResourceLoader();
    }

    resolvers = new ArrayList<>();
    resolvers.add(new YamlPropertySourceResolver());
    resolvers.add(new PropertiesPropertySourceResolver());
  }

  @Override
  public PropertySource load() {
    Map<String, Object> propertyMap = new HashMap<String, Object>();
    for (String file : getAllMatchedConfigFiles()) {
      Resource resource = resourceLoader.getResource(file);
      if (!resource.exists()) {
        continue;
      }
      for (PropertySourceResolver resolver : resolvers) {
        if (resolver.canResolvedExtension(resource.getExtension())) {
          try {
            propertyMap.putAll(resolver.resolve(resource.getInputStream()));
          } catch (IOException e) {
            throw new ConfigurationResolveException("load environment configuration failed.", e);
          }
        }
      }
    }
    PropertySource propertySource = new PropertySource();
    propertySource.addProperties(propertyMap);
    return propertySource;
  }

  public abstract String[] getAllMatchedConfigFiles();

}

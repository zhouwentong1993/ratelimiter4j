package com.wentong.ratelimiter.env.loader;

import com.wentong.ratelimiter.env.PropertySource;

/**
 * The interface represents the environment configuration loaders.
 */
public interface PropertySourceLoader {

	PropertySource load();

}

package com.sdet.framework.api.config;

import com.sdet.framework.commons.config.ConfigProvider;
import com.sdet.framework.commons.config.EnvironmentConfig;
import com.sdet.framework.commons.config.EnvironmentConfigLoader;

public final class ApiConfigProvider implements ConfigProvider {
  private static volatile ApiConfigProvider instance;
  private final EnvironmentConfig config = EnvironmentConfigLoader.load();

  private ApiConfigProvider() {}

  public static ApiConfigProvider getInstance() {
    if (instance == null)
      synchronized (ApiConfigProvider.class) {
        if (instance == null) instance = new ApiConfigProvider();
      }
    return instance;
  }

  @Override
  public String getBaseUrl(String serviceName) {
    return "jsonplaceholder".equals(serviceName) ? config.getApi().getBaseUrl() : null;
  }
}

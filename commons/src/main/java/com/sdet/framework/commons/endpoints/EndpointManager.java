package com.sdet.framework.commons.endpoints;

import com.sdet.framework.commons.config.ConfigProvider;
import java.util.Map;

/** Resolves endpoint definitions into absolute URLs for the active environment. */
public class EndpointManager<T extends Enum<T> & EndpointDefinition> {
  private final ConfigProvider configProvider;

  protected EndpointManager(ConfigProvider configProvider) {
    this.configProvider = configProvider;
  }

  public String getUrl(T endpoint) {
    String baseUrl = configProvider.getBaseUrl(endpoint.service());
    if (baseUrl == null)
      throw new IllegalStateException("No base URL configured for " + endpoint.service());
    return baseUrl.replaceAll("/$", "") + endpoint.path();
  }

  public String getUrl(T endpoint, Map<String, String> pathParameters) {
    String path = getUrl(endpoint);
    for (var entry : pathParameters.entrySet())
      path = path.replace("{" + entry.getKey() + "}", entry.getValue());
    return path;
  }
}

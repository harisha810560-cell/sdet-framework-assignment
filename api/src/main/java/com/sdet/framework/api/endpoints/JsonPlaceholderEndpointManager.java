package com.sdet.framework.api.endpoints;

import com.sdet.framework.api.config.ApiConfigProvider;
import com.sdet.framework.commons.endpoints.EndpointManager;
import java.util.Map;

public final class JsonPlaceholderEndpointManager
    extends EndpointManager<JsonPlaceholderEndpoints> {
  private static volatile JsonPlaceholderEndpointManager instance;

  private JsonPlaceholderEndpointManager() {
    super(ApiConfigProvider.getInstance());
  }

  public static JsonPlaceholderEndpointManager getInstance() {
    if (instance == null)
      synchronized (JsonPlaceholderEndpointManager.class) {
        if (instance == null) instance = new JsonPlaceholderEndpointManager();
      }
    return instance;
  }

  public static String url(JsonPlaceholderEndpoints endpoint) {
    return getInstance().getUrl(endpoint);
  }

  public static String url(JsonPlaceholderEndpoints endpoint, int id) {
    return getInstance().getUrl(endpoint, Map.of("id", String.valueOf(id)));
  }
}

package com.sdet.framework.api.endpoints;

import com.sdet.framework.commons.endpoints.EndpointDefinition;

public enum JsonPlaceholderEndpoints implements EndpointDefinition {
  POSTS("jsonplaceholder", "/posts"),
  POST_BY_ID("jsonplaceholder", "/posts/{id}"),
  UNKNOWN_RESOURCE("jsonplaceholder", "/unknown-resource");
  private final String service;
  private final String path;

  JsonPlaceholderEndpoints(String service, String path) {
    this.service = service;
    this.path = path;
  }

  public String service() {
    return service;
  }

  public String path() {
    return path;
  }
}

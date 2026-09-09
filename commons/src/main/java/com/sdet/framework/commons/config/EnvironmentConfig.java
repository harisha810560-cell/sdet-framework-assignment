package com.sdet.framework.commons.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class EnvironmentConfig {
  String environment;
  UiConfig ui;
  ApiConfig api;
  FrameworkOptions framework;

  @Value
  @Builder(toBuilder = true)
  public static class UiConfig {
    String baseUrl;
    String username;
    String password;
    String lockedOutUsername;
  }

  @Value
  @Builder(toBuilder = true)
  public static class ApiConfig {
    String baseUrl;
    int requestTimeoutMs;
  }

  @Value
  @Builder(toBuilder = true)
  public static class FrameworkOptions {
    String browser;
    boolean headless;
    int timeoutMs;
    String screenshotDirectory;
    String traceDirectory;
    boolean tracingEnabled;
  }
}

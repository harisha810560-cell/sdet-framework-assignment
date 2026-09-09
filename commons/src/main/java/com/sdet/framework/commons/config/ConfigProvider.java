package com.sdet.framework.commons.config;

/** Resolves the base URL of a named service for the active environment. */
public interface ConfigProvider {
  String getBaseUrl(String serviceName);
}

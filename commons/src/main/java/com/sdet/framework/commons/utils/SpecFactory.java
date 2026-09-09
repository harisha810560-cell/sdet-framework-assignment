package com.sdet.framework.commons.utils;

import com.sdet.framework.commons.config.EnvironmentConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import java.util.Map;

/** Shared REST Assured specifications: base URI, JSON headers and timeouts. */
public final class SpecFactory {
  private SpecFactory() {}

  public static RequestSpecification request(
      EnvironmentConfig config, Map<String, String> headers) {
    RequestSpecBuilder builder =
        new RequestSpecBuilder()
            .setBaseUri(config.getApi().getBaseUrl())
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .setConfig(timeoutConfig(config.getApi().getRequestTimeoutMs()));
    if (headers != null && !headers.isEmpty()) builder.addHeaders(headers);
    return builder.build();
  }

  public static ResponseSpecification expectJson(int statusCode) {
    return new ResponseSpecBuilder()
        .expectStatusCode(statusCode)
        .expectContentType(ContentType.JSON)
        .build();
  }

  /** For error paths, where the body format is not guaranteed to be JSON. */
  public static ResponseSpecification expectStatus(int statusCode) {
    return new ResponseSpecBuilder().expectStatusCode(statusCode).build();
  }

  private static RestAssuredConfig timeoutConfig(int timeoutMs) {
    return RestAssuredConfig.config()
        .httpClient(
            HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", timeoutMs)
                .setParam("http.socket.timeout", timeoutMs)
                .setParam("http.connection-manager.timeout", timeoutMs));
  }
}

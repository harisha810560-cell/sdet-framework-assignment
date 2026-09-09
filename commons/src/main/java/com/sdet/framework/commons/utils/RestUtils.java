package com.sdet.framework.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdet.framework.commons.config.EnvironmentConfig;
import com.sdet.framework.commons.config.EnvironmentConfigLoader;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/** Shared REST Assured wrapper. Helpers never invoke RestAssured directly. */
@Slf4j
public final class RestUtils {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final EnvironmentConfig CONFIG = EnvironmentConfigLoader.load();

  private RestUtils() {}

  public static Response get(
      String url, Map<String, String> headers, Map<String, Object> queryParams) {
    return execute("GET", url, headers, queryParams, null);
  }

  public static Response post(String url, Map<String, String> headers, Object payload) {
    return execute("POST", url, headers, null, payload);
  }

  public static Response put(String url, Map<String, String> headers, Object payload) {
    return execute("PUT", url, headers, null, payload);
  }

  public static Response patch(String url, Map<String, String> headers, Object payload) {
    return execute("PATCH", url, headers, null, payload);
  }

  public static Response delete(String url, Map<String, String> headers) {
    return execute("DELETE", url, headers, null, null);
  }

  /** Sends the body verbatim, for payloads Jackson would never produce. */
  public static Response postRaw(String url, Map<String, String> headers, String rawBody) {
    return execute("POST", url, headers, null, rawBody);
  }

  private static Response execute(
      String method,
      String url,
      Map<String, String> headers,
      Map<String, Object> queryParams,
      Object payload) {
    RequestSpecification request = RestAssured.given().spec(SpecFactory.request(CONFIG, headers));
    if (queryParams != null && !queryParams.isEmpty()) request.queryParams(queryParams);
    if (payload != null) request.body(payload);

    log.info("{} request: {}", method, buildUrl(url, queryParams));
    log.info(
        "cURL command:{}{}",
        System.lineSeparator(),
        buildCurl(method, url, headers, queryParams, payload));

    Response response =
        switch (method) {
          case "GET" -> request.get(url);
          case "POST" -> request.post(url);
          case "PUT" -> request.put(url);
          case "PATCH" -> request.patch(url);
          case "DELETE" -> request.delete(url);
          default -> throw new IllegalArgumentException("Unsupported HTTP method: " + method);
        };

    logResponse(method, response);
    return response;
  }

  private static void logResponse(String method, Response response) {
    log.info("{} response status: {}", method, response.getStatusLine());
    log.info("{} response time: {} ms", method, response.getTime());
    log.info("{} response headers:{}{}", method, System.lineSeparator(), response.getHeaders());
    log.info("{} response body:{}{}", method, System.lineSeparator(), response.asPrettyString());
  }

  private static String buildCurl(
      String method,
      String url,
      Map<String, String> headers,
      Map<String, Object> queryParams,
      Object payload) {
    StringBuilder command =
        new StringBuilder("curl --location --request ")
            .append(method)
            .append(" '")
            .append(buildUrl(url, queryParams))
            .append("' --header 'Content-Type: application/json'");
    if (headers != null) {
      headers.forEach(
          (name, value) ->
              command.append(" --header '").append(name).append(": ").append(value).append("'"));
    }
    if (payload != null) command.append(" --data-raw '").append(toJson(payload)).append("'");
    return command.toString();
  }

  private static String buildUrl(String url, Map<String, Object> queryParams) {
    if (queryParams == null || queryParams.isEmpty()) return url;
    StringBuilder queryString = new StringBuilder();
    queryParams.forEach(
        (name, value) -> {
          if (!queryString.isEmpty()) queryString.append('&');
          queryString
              .append(URLEncoder.encode(name, StandardCharsets.UTF_8))
              .append('=')
              .append(URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8));
        });
    return url + (url.contains("?") ? "&" : "?") + queryString;
  }

  private static String toJson(Object payload) {
    if (payload instanceof String raw) return raw;
    try {
      return OBJECT_MAPPER.writeValueAsString(payload);
    } catch (JsonProcessingException exception) {
      throw new IllegalArgumentException("Cannot serialize API request payload", exception);
    }
  }
}

package com.sdet.framework.commons.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.yaml.snakeyaml.Yaml;

/** Loads base + active environment + optional local override configuration. */
public final class EnvironmentConfigLoader {
  private static final Pattern VARIABLE = Pattern.compile("\\$\\{([A-Z0-9_]+)(?::-([^}]*))?}");

  private EnvironmentConfigLoader() {}

  /** Resolution order: {@code -Denv}, then {@code TEST_ENV}, then {@code local}. */
  public static String activeEnvironment() {
    String env = System.getProperty("env");
    return env == null || env.isBlank() ? System.getenv().getOrDefault("TEST_ENV", "local") : env;
  }

  public static EnvironmentConfig load() {
    String env = activeEnvironment();
    Path root = findProjectRoot();
    Map<String, Object> data = read(root.resolve("config/application.yml"));
    merge(data, readRequired(root.resolve("config/environments").resolve(env + ".yml"), env));
    merge(data, read(root.resolve("config/environments").resolve(env + ".local.yml")));
    return toConfig(env, data);
  }

  private static EnvironmentConfig toConfig(String env, Map<String, Object> value) {
    Map<String, Object> ui = section(value, "ui");
    Map<String, Object> api = section(value, "api");
    Map<String, Object> framework = section(value, "framework");
    return EnvironmentConfig.builder()
        .environment(env)
        .ui(
            EnvironmentConfig.UiConfig.builder()
                .baseUrl(required(ui, "baseUrl"))
                .username(required(ui, "username"))
                .password(required(ui, "password"))
                .lockedOutUsername(string(ui, "lockedOutUsername", "locked_out_user"))
                .build())
        .api(
            EnvironmentConfig.ApiConfig.builder()
                .baseUrl(required(api, "baseUrl"))
                .requestTimeoutMs(integer(api, "requestTimeoutMs", 10000))
                .build())
        .framework(
            EnvironmentConfig.FrameworkOptions.builder()
                .browser(
                    System.getProperty(
                        "framework.browser", string(framework, "browser", "chromium")))
                .headless(
                    Boolean.parseBoolean(
                        System.getProperty(
                            "framework.headless", string(framework, "headless", "true"))))
                .timeoutMs(integer(framework, "timeoutMs", 10000))
                .screenshotDirectory(string(framework, "screenshotDirectory", "target/screenshots"))
                .traceDirectory(string(framework, "traceDirectory", "target/traces"))
                .tracingEnabled(
                    Boolean.parseBoolean(
                        System.getProperty(
                            "framework.tracing", string(framework, "tracing", "true"))))
                .build())
        .build();
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> section(Map<String, Object> root, String name) {
    Object section = root.get(name);
    return section instanceof Map ? (Map<String, Object>) section : Map.of();
  }

  private static String required(Map<String, Object> map, String name) {
    String result = string(map, name, null);
    if (result == null || result.isBlank())
      throw new IllegalStateException("Missing required configuration: " + name);
    return result;
  }

  private static String string(Map<String, Object> map, String name, String fallback) {
    Object value = map.get(name);
    return value == null ? fallback : resolve(String.valueOf(value));
  }

  private static int integer(Map<String, Object> map, String name, int fallback) {
    String value = string(map, name, String.valueOf(fallback));
    return Integer.parseInt(value);
  }

  @SuppressWarnings("unchecked")
  private static Map<String, Object> read(Path file) {
    if (!Files.exists(file)) return new LinkedHashMap<>();
    try {
      Object raw = new Yaml().load(Files.newInputStream(file));
      return raw instanceof Map
          ? new LinkedHashMap<>((Map<String, Object>) raw)
          : new LinkedHashMap<>();
    } catch (IOException e) {
      throw new IllegalStateException("Cannot read configuration " + file, e);
    }
  }

  private static Map<String, Object> readRequired(Path file, String env) {
    if (!Files.exists(file))
      throw new IllegalArgumentException(
          "Unknown environment '" + env + "'. Expected config/environments/" + env + ".yml");
    return read(file);
  }

  @SuppressWarnings("unchecked")
  private static void merge(Map<String, Object> base, Map<String, Object> override) {
    override.forEach(
        (key, value) -> {
          if (value instanceof Map && base.get(key) instanceof Map)
            merge((Map<String, Object>) base.get(key), (Map<String, Object>) value);
          else base.put(key, value);
        });
  }

  private static String resolve(String raw) {
    Matcher matcher = VARIABLE.matcher(raw);
    StringBuffer result = new StringBuffer();
    while (matcher.find()) {
      String envValue = System.getenv(matcher.group(1));
      String replacement = envValue != null ? envValue : matcher.group(2);
      if (replacement == null)
        throw new IllegalStateException(
            "Missing required environment variable: " + matcher.group(1));
      matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
    }
    matcher.appendTail(result);
    return result.toString();
  }

  private static Path findProjectRoot() {
    Path current = Path.of(System.getProperty("user.dir")).toAbsolutePath();
    while (current != null) {
      if (Files.exists(current.resolve("config/application.yml"))) return current;
      current = current.getParent();
    }
    throw new IllegalStateException(
        "Could not find project config/application.yml from current directory");
  }
}

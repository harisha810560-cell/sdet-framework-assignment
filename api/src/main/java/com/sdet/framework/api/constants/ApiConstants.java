package com.sdet.framework.api.constants;

/** Fixed values shared by the API helpers, validators and suites. */
public final class ApiConstants {
  public static final String POST_SCHEMA = "schemas/post-schema.json";
  public static final String POST_LIST_SCHEMA = "schemas/post-list-schema.json";

  /** Beyond the seeded data set, used by the not-found cases. */
  public static final int INVALID_POST_ID = 999999;

  public static final int SEEDED_POST_COUNT = 100;

  /** Matches no seeded post, used by the empty-collection case. */
  public static final int UNUSED_USER_ID = 0;

  private ApiConstants() {}
}

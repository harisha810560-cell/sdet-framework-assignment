package com.sdet.framework.api.requestbuilder;

import com.sdet.framework.api.model.PostRequest;

/** Standard /posts payloads, so the suites carry no inline literals. */
public final class PostRequestFactory {
  public static final String MALFORMED_BODY = "{\"title\": \"unterminated";
  public static final String EMPTY_BODY = "{}";

  private PostRequestFactory() {}

  public static PostRequest defaultPost() {
    return PostRequest.builder()
        .title("SDET automation assignment")
        .body("Reusable builder-pattern API payload")
        .userId(1)
        .build();
  }

  public static PostRequest replacementPost() {
    return PostRequest.builder().title("Replaced title").body("Replaced body").userId(2).build();
  }

  public static PostRequest partialUpdate() {
    return PostRequest.builder().title("Updated title").body("Updated body").build();
  }
}

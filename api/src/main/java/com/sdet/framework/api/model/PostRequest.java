package com.sdet.framework.api.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

/** Request payload for /posts. Null fields are omitted so PATCH sends only what it changes. */
@Value
@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostRequest {
  String title;
  String body;
  Integer userId;
}

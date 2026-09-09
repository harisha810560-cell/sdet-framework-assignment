package com.sdet.framework.api.model;

import lombok.Builder;
import lombok.Value;

/** Immutable contract passed between the stages of an API flow. */
@Value
@Builder(toBuilder = true)
public class ApiTestContext {
  PostRequest postRequest;
  int existingPostId;
  int invalidPostId;
  int userId;

  /** Sent verbatim by the cases that need a body Jackson would never produce. */
  String rawBody;
}

package com.sdet.framework.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

/** Deserialized /posts document. Boxed types allow partial responses to omit fields. */
@Value
@Builder(toBuilder = true)
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostResponse {
  Integer id;
  String title;
  String body;
  Integer userId;
}

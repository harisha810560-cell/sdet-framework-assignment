package com.sdet.framework.api.helpers.posts;

import com.sdet.framework.api.endpoints.JsonPlaceholderEndpointManager;
import com.sdet.framework.api.endpoints.JsonPlaceholderEndpoints;
import com.sdet.framework.api.helpers.BaseApiHelper;
import com.sdet.framework.api.validators.PostValidator;
import com.sdet.framework.commons.base.ServiceHelper;
import com.sdet.framework.commons.utils.RestUtils;
import io.restassured.response.Response;
import java.util.Map;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/** POST a body that is not valid JSON, which must not create a resource. */
@Getter
@Slf4j
@SuperBuilder
public class CreatePostWithMalformedBodyHelper extends BaseApiHelper {
  private String endpoint;
  private Response response;

  @Override
  public ServiceHelper init() {
    endpoint = JsonPlaceholderEndpointManager.url(JsonPlaceholderEndpoints.POSTS);
    log.info("POST malformed-body endpoint: {}", endpoint);
    return this;
  }

  @Override
  public ServiceHelper process() {
    response = RestUtils.postRaw(endpoint, Map.of(), apiTestContext.getRawBody());
    return this;
  }

  @Override
  public ServiceHelper validate() {
    PostValidator.malformedBodyWasNotAccepted(response);
    return this;
  }
}

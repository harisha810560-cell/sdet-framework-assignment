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

/** GET /posts filtered by owner. */
@Getter
@Slf4j
@SuperBuilder
public class GetPostsByUserHelper extends BaseApiHelper {
  private String endpoint;
  private Response response;

  @Override
  public ServiceHelper init() {
    endpoint = JsonPlaceholderEndpointManager.url(JsonPlaceholderEndpoints.POSTS);
    log.info("GET filtered endpoint: {} for userId {}", endpoint, apiTestContext.getUserId());
    return this;
  }

  @Override
  public ServiceHelper process() {
    response = RestUtils.get(endpoint, Map.of(), Map.of("userId", apiTestContext.getUserId()));
    return this;
  }

  @Override
  public ServiceHelper validate() {
    PostValidator.hasPostsOwnedBy(response, apiTestContext.getUserId());
    return this;
  }
}

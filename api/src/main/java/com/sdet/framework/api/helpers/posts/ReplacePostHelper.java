package com.sdet.framework.api.helpers.posts;

import com.sdet.framework.api.endpoints.JsonPlaceholderEndpointManager;
import com.sdet.framework.api.endpoints.JsonPlaceholderEndpoints;
import com.sdet.framework.api.helpers.BaseApiHelper;
import com.sdet.framework.api.model.PostRequest;
import com.sdet.framework.api.validators.PostValidator;
import com.sdet.framework.commons.base.ServiceHelper;
import com.sdet.framework.commons.utils.RestUtils;
import io.restassured.response.Response;
import java.util.Map;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/** PUT a full replacement document over an existing post. */
@Getter
@Slf4j
@SuperBuilder
public class ReplacePostHelper extends BaseApiHelper {
  private String endpoint;
  private PostRequest request;
  private Response response;

  @Override
  public ServiceHelper init() {
    endpoint =
        JsonPlaceholderEndpointManager.url(
            JsonPlaceholderEndpoints.POST_BY_ID, apiTestContext.getExistingPostId());
    request = apiTestContext.getPostRequest();
    log.info("PUT endpoint: {}, payload: {}", endpoint, request);
    return this;
  }

  @Override
  public ServiceHelper process() {
    response = RestUtils.put(endpoint, Map.of(), request);
    return this;
  }

  @Override
  public ServiceHelper validate() {
    PostValidator.wasReplaced(response, apiTestContext.getExistingPostId(), request);
    return this;
  }
}

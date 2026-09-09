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

/** POST a valid but empty document. */
@Getter
@Slf4j
@SuperBuilder
public class CreatePostWithoutMandatoryFieldsHelper extends BaseApiHelper {
  private String endpoint;
  private Response response;

  @Override
  public ServiceHelper init() {
    endpoint = JsonPlaceholderEndpointManager.url(JsonPlaceholderEndpoints.POSTS);
    log.info("POST empty-document endpoint: {}", endpoint);
    return this;
  }

  @Override
  public ServiceHelper process() {
    response = RestUtils.postRaw(endpoint, Map.of(), apiTestContext.getRawBody());
    return this;
  }

  @Override
  public ServiceHelper validate() {
    PostValidator.wasAcceptedWithoutFieldValidation(response);
    return this;
  }
}

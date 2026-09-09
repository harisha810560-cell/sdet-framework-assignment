package com.sdet.framework.api.tests;

import com.sdet.framework.api.constants.ApiConstants;
import com.sdet.framework.api.data.ApiTestData;
import com.sdet.framework.api.model.ApiTestContext;
import com.sdet.framework.api.requestbuilder.PostRequestFactory;

/**
 * Shared baseline for the /posts suites. Every test builds its own contract rather than sharing a
 * field, which is what makes the suite safe to parallelise.
 */
public abstract class BasePostApiTest {

  protected ApiTestContext postContext() {
    return ApiTestContext.builder()
        .existingPostId(ApiTestData.EXISTING_POST_ID)
        .invalidPostId(ApiConstants.INVALID_POST_ID)
        .userId(ApiTestData.EXISTING_USER_ID)
        .postRequest(PostRequestFactory.defaultPost())
        .build();
  }
}

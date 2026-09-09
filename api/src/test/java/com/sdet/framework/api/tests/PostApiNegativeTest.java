package com.sdet.framework.api.tests;

import com.sdet.framework.api.constants.ApiConstants;
import com.sdet.framework.api.helpers.posts.CreatePostWithMalformedBodyHelper;
import com.sdet.framework.api.helpers.posts.CreatePostWithoutMandatoryFieldsHelper;
import com.sdet.framework.api.helpers.posts.GetPostNotFoundHelper;
import com.sdet.framework.api.helpers.posts.GetPostsForUnusedUserHelper;
import com.sdet.framework.api.helpers.posts.GetUnknownResourceHelper;
import com.sdet.framework.api.requestbuilder.PostRequestFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/** The /posts error paths and collection boundaries. */
public class PostApiNegativeTest extends BasePostApiTest {

  @DataProvider(name = "unknownPostIds")
  public Object[][] unknownPostIds() {
    // Just below the seeded range, just above it, and far beyond it.
    return new Object[][] {
      {0}, {ApiConstants.SEEDED_POST_COUNT + 1}, {ApiConstants.INVALID_POST_ID}
    };
  }

  @Test(
      dataProvider = "unknownPostIds",
      description = "GET with an id outside the seeded data returns 404",
      groups = {"api", "negative", "regression", "boundary"})
  public void unknownPostIdReturnsNotFound(int postId) {
    GetPostNotFoundHelper.builder()
        .apiTestContext(postContext().toBuilder().invalidPostId(postId).build())
        .build()
        .test();
  }

  @Test(
      description = "GET on a route the service does not expose returns 404",
      groups = {"api", "negative", "regression"})
  public void unknownResourceReturnsNotFound() {
    GetUnknownResourceHelper.builder().apiTestContext(postContext()).build().test();
  }

  @Test(
      description = "POST with a body that is not valid JSON does not create a post",
      groups = {"api", "negative", "regression"})
  public void malformedJsonBodyIsRefused() {
    CreatePostWithMalformedBodyHelper.builder()
        .apiTestContext(
            postContext().toBuilder().rawBody(PostRequestFactory.MALFORMED_BODY).build())
        .build()
        .test();
  }

  @Test(
      description = "POST with no mandatory fields records the service's validation behaviour",
      groups = {"api", "negative", "regression"})
  public void payloadWithoutMandatoryFieldsRecordsServiceBehaviour() {
    CreatePostWithoutMandatoryFieldsHelper.builder()
        .apiTestContext(postContext().toBuilder().rawBody(PostRequestFactory.EMPTY_BODY).build())
        .build()
        .test();
  }

  @Test(
      description = "A filter that matches no post returns an empty collection, not an error",
      groups = {"api", "boundary", "regression"})
  public void filterMatchingNoPostsReturnsEmptyCollection() {
    GetPostsForUnusedUserHelper.builder()
        .apiTestContext(postContext().toBuilder().userId(ApiConstants.UNUSED_USER_ID).build())
        .build()
        .test();
  }
}

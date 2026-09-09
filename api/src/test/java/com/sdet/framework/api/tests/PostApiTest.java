package com.sdet.framework.api.tests;

import com.sdet.framework.api.constants.ApiConstants;
import com.sdet.framework.api.helpers.posts.CreatePostHelper;
import com.sdet.framework.api.helpers.posts.DeletePostHelper;
import com.sdet.framework.api.helpers.posts.GetAllPostsHelper;
import com.sdet.framework.api.helpers.posts.GetPostByIdHelper;
import com.sdet.framework.api.helpers.posts.GetPostsByUserHelper;
import com.sdet.framework.api.helpers.posts.ReplacePostHelper;
import com.sdet.framework.api.helpers.posts.UpdatePostHelper;
import com.sdet.framework.api.requestbuilder.PostRequestFactory;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/** The /posts happy paths across all five HTTP verbs. */
public class PostApiTest extends BasePostApiTest {

  @Test(
      description = "GET returns an existing post matching the post schema",
      groups = {"api", "smoke", "regression"})
  public void getsAnExistingPost() {
    GetPostByIdHelper.builder().apiTestContext(postContext()).build().test();
  }

  @DataProvider(name = "seededPostIds")
  public Object[][] seededPostIds() {
    return new Object[][] {{1}, {50}, {ApiConstants.SEEDED_POST_COUNT}};
  }

  @Test(
      dataProvider = "seededPostIds",
      description = "GET returns each seeded post, including both ends of the seeded range",
      groups = {"api", "regression", "boundary"})
  public void getsEachSeededPost(int postId) {
    GetPostByIdHelper.builder()
        .apiTestContext(postContext().toBuilder().existingPostId(postId).build())
        .build()
        .test();
  }

  @Test(
      description = "GET returns the whole post collection",
      groups = {"api", "regression"})
  public void getsTheWholePostCollection() {
    GetAllPostsHelper.builder().apiTestContext(postContext()).build().test();
  }

  @Test(
      description = "GET filtered by userId returns only that user's posts",
      groups = {"api", "regression"})
  public void getsOnlyPostsOwnedByTheRequestedUser() {
    GetPostsByUserHelper.builder().apiTestContext(postContext()).build().test();
  }

  @Test(
      description = "POST creates a post and echoes the submitted payload",
      groups = {"api", "smoke", "regression"})
  public void createsPostFromBuilderPayload() {
    CreatePostHelper.builder().apiTestContext(postContext()).build().test();
  }

  @Test(
      description = "PUT replaces every field of an existing post",
      groups = {"api", "regression"})
  public void replacesEveryFieldOfAPost() {
    ReplacePostHelper.builder()
        .apiTestContext(
            postContext().toBuilder().postRequest(PostRequestFactory.replacementPost()).build())
        .build()
        .test();
  }

  @Test(
      description = "PATCH applies only the fields that were sent",
      groups = {"api", "regression"})
  public void patchesOnlyTheFieldsThatWereSent() {
    UpdatePostHelper.builder()
        .apiTestContext(
            postContext().toBuilder().postRequest(PostRequestFactory.partialUpdate()).build())
        .build()
        .test();
  }

  @Test(
      description = "DELETE removes a post and returns an empty document",
      groups = {"api", "regression"})
  public void deletesAPost() {
    DeletePostHelper.builder().apiTestContext(postContext()).build().test();
  }
}

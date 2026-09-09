package com.sdet.framework.api.validators;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.sdet.framework.api.constants.ApiConstants;
import com.sdet.framework.api.model.PostRequest;
import com.sdet.framework.api.model.PostResponse;
import com.sdet.framework.commons.utils.SpecFactory;
import io.restassured.response.Response;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/** Assertions for /posts: status code, JSON schema and the fields that carry meaning. */
@Slf4j
public final class PostValidator {
  private PostValidator() {}

  public static void hasPost(Response response, int id) {
    response
        .then()
        .spec(SpecFactory.expectJson(200))
        .body(matchesJsonSchemaInClasspath(ApiConstants.POST_SCHEMA));
    PostResponse post = response.as(PostResponse.class);
    assertEquals(post.getId(), Integer.valueOf(id), "post id");
    assertFalse(post.getTitle().isBlank(), "post title");
    assertFalse(post.getBody().isBlank(), "post body");
    assertTrue(post.getUserId() > 0, "post owner");
    log.info("Post {} returned with status 200", id);
  }

  public static void wasCreated(Response response, PostRequest request) {
    response
        .then()
        .spec(SpecFactory.expectJson(201))
        .body(matchesJsonSchemaInClasspath(ApiConstants.POST_SCHEMA));
    PostResponse created = response.as(PostResponse.class);
    assertNotNull(created.getId(), "generated post id");
    assertEquals(created.getTitle(), request.getTitle(), "post title");
    assertEquals(created.getBody(), request.getBody(), "post body");
    assertEquals(created.getUserId(), request.getUserId(), "post owner");
    log.info("Post {} created with status 201", created.getId());
  }

  public static void wasReplaced(Response response, int id, PostRequest request) {
    response
        .then()
        .spec(SpecFactory.expectJson(200))
        .body(matchesJsonSchemaInClasspath(ApiConstants.POST_SCHEMA));
    PostResponse replaced = response.as(PostResponse.class);
    assertEquals(replaced.getId(), Integer.valueOf(id), "post id");
    assertEquals(replaced.getTitle(), request.getTitle(), "post title");
    assertEquals(replaced.getBody(), request.getBody(), "post body");
    assertEquals(replaced.getUserId(), request.getUserId(), "post owner");
    log.info("Post {} replaced with status 200", id);
  }

  public static void wasUpdated(Response response, int id, PostRequest request) {
    response.then().spec(SpecFactory.expectJson(200));
    PostResponse updated = response.as(PostResponse.class);
    assertEquals(updated.getId(), Integer.valueOf(id), "post id");
    assertEquals(updated.getTitle(), request.getTitle(), "post title");
    assertEquals(updated.getBody(), request.getBody(), "post body");
    log.info("Post {} updated with status 200", id);
  }

  public static void wasDeleted(Response response) {
    response.then().spec(SpecFactory.expectJson(200));
    assertTrue(response.jsonPath().getMap("$").isEmpty(), "delete response body");
    log.info("Post deleted with status 200");
  }

  public static void wasNotFound(Response response) {
    response.then().spec(SpecFactory.expectStatus(404));
    log.info("Missing resource returned status 404");
  }

  public static void hasPostCollection(Response response, int expectedSize) {
    response
        .then()
        .spec(SpecFactory.expectJson(200))
        .body(matchesJsonSchemaInClasspath(ApiConstants.POST_LIST_SCHEMA));
    List<PostResponse> posts = response.jsonPath().getList("$", PostResponse.class);
    assertEquals(posts.size(), expectedSize, "post collection size");
    log.info("{} posts returned", posts.size());
  }

  public static void hasPostsOwnedBy(Response response, int userId) {
    response
        .then()
        .spec(SpecFactory.expectJson(200))
        .body(matchesJsonSchemaInClasspath(ApiConstants.POST_LIST_SCHEMA));
    List<PostResponse> posts = response.jsonPath().getList("$", PostResponse.class);
    assertFalse(posts.isEmpty(), "posts for an existing user");
    assertTrue(
        posts.stream().allMatch(post -> post.getUserId().equals(userId)), "post owner filter");
    log.info("{} posts returned for user {}", posts.size(), userId);
  }

  public static void hasNoPosts(Response response) {
    response.then().spec(SpecFactory.expectJson(200));
    assertTrue(response.jsonPath().getList("$").isEmpty(), "empty post collection");
    log.info("Empty collection returned with status 200");
  }

  /** JSONPlaceholder answers 500 where 400 is correct, so this asserts "refused", not a code. */
  public static void malformedBodyWasNotAccepted(Response response) {
    assertTrue(response.statusCode() >= 400, "malformed body refused");
    assertFalse(response.asString().contains("\"id\""), "no resource created");
    log.info("Malformed body refused with status {}", response.statusCode());
  }

  /** JSONPlaceholder applies no server-side validation; this pins that behaviour. */
  public static void wasAcceptedWithoutFieldValidation(Response response) {
    response.then().spec(SpecFactory.expectJson(201));
    assertNotNull(response.jsonPath().get("id"), "generated post id");
    log.info("Empty payload accepted with status 201");
  }
}

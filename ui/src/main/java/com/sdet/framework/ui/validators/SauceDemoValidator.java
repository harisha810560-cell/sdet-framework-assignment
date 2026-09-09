package com.sdet.framework.ui.validators;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import com.sdet.framework.ui.constants.UiConstants;
import com.sdet.framework.ui.models.Product;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/** Assertions for the SauceDemo journey, kept out of the page objects and helpers. */
@Slf4j
public final class SauceDemoValidator {
  private SauceDemoValidator() {}

  public static void loginWasSuccessful(boolean inventoryVisible, String currentUrl) {
    assertTrue(inventoryVisible, "inventory page visible after login");
    assertTrue(
        currentUrl.contains(UiConstants.INVENTORY_PATH), "inventory route, was " + currentUrl);
    log.info("Signed in and landed on {}", currentUrl);
  }

  public static void cartMatchesSelection(
      List<Product> cartProducts, Product expected, int cartBadgeCount, String requestedProduct) {
    assertEquals(expected.getName(), requestedProduct, "product selected on the inventory page");
    assertEquals(cartProducts.size(), 1, "cart item count");
    Product actual = cartProducts.get(0);
    assertEquals(actual.getName(), expected.getName(), "cart item name");
    assertEquals(actual.getPrice(), expected.getPrice(), "cart item price");
    assertEquals(cartBadgeCount, 1, "cart badge count");
    log.info("Cart holds {} at {}", actual.getName(), actual.getPrice());
  }

  public static void cartHoldsProducts(
      List<Product> cartProducts, List<Product> expected, int cartBadgeCount) {
    assertEquals(cartProducts.size(), expected.size(), "cart item count");
    assertEquals(cartBadgeCount, expected.size(), "cart badge count");
    assertEquals(
        cartProducts.stream().map(Product::getName).sorted().toList(),
        expected.stream().map(Product::getName).sorted().toList(),
        "cart item names");
    log.info("Cart holds {} products", cartProducts.size());
  }

  public static void cartIsEmpty(int cartItemCount, int cartBadgeCount) {
    assertEquals(cartItemCount, 0, "cart item count");
    assertEquals(cartBadgeCount, 0, "cart badge count");
    log.info("Cart is empty and no badge is rendered");
  }

  public static void orderWasCompleted(String header, String body, String currentUrl) {
    assertEquals(header, UiConstants.ORDER_CONFIRMATION_HEADER, "confirmation header");
    assertFalse(body.isBlank(), "confirmation body");
    assertTrue(
        currentUrl.contains(UiConstants.CHECKOUT_COMPLETE_PATH),
        "checkout-complete route, was " + currentUrl);
    log.info("Order confirmed with header '{}'", header);
  }

  public static void loginWasRejected(String actualError, String expectedError) {
    assertEquals(actualError, expectedError, "login error message");
    log.info("Login rejected with '{}'", actualError);
  }

  public static void checkoutWasRejected(String actualError, String expectedError) {
    assertEquals(actualError, expectedError, "checkout error message");
    log.info("Checkout rejected with '{}'", actualError);
  }
}

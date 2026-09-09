package com.sdet.framework.ui.constants;

/**
 * Single registry of SauceDemo test ids, route fragments and expected copy. Page objects are the
 * only consumers, so a markup change is a one-line edit here. The values are test ids rather than
 * CSS because {@code DriverFactory} points Playwright's {@code getByTestId} at {@code data-test}.
 */
public final class UiConstants {

  // Login screen
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";
  public static final String LOGIN_BUTTON = "login-button";
  public static final String ERROR_MESSAGE = "error";

  // Inventory screen
  public static final String INVENTORY_CONTAINER = "inventory-container";
  public static final String INVENTORY_ITEM = "inventory-item";
  public static final String INVENTORY_ITEM_NAME = "inventory-item-name";
  public static final String INVENTORY_ITEM_PRICE = "inventory-item-price";
  public static final String CART_LINK = "shopping-cart-link";
  public static final String CART_BADGE = "shopping-cart-badge";

  /** Each button's test id carries the product slug, so this one stays a scoped CSS prefix. */
  public static final String ADD_TO_CART_CSS = "button[data-test^='add-to-cart']";

  // Cart screen
  public static final String CHECKOUT_BUTTON = "checkout";

  // Checkout screens
  public static final String FIRST_NAME = "firstName";
  public static final String LAST_NAME = "lastName";
  public static final String POSTAL_CODE = "postalCode";
  public static final String CONTINUE_BUTTON = "continue";
  public static final String FINISH_BUTTON = "finish";
  public static final String CONFIRMATION_HEADER = "complete-header";
  public static final String CONFIRMATION_TEXT = "complete-text";

  // Route fragments used for post-navigation assertions
  public static final String INVENTORY_PATH = "/inventory.html";
  public static final String CHECKOUT_COMPLETE_PATH = "/checkout-complete.html";

  // Expected application copy
  public static final String INVALID_CREDENTIALS_MESSAGE =
      "Epic sadface: Username and password do not match any user in this service";
  public static final String LOCKED_OUT_MESSAGE =
      "Epic sadface: Sorry, this user has been locked out.";
  public static final String USERNAME_REQUIRED_MESSAGE = "Epic sadface: Username is required";
  public static final String PASSWORD_REQUIRED_MESSAGE = "Epic sadface: Password is required";
  public static final String FIRST_NAME_REQUIRED_MESSAGE = "Error: First Name is required";
  public static final String ORDER_CONFIRMATION_HEADER = "Thank you for your order!";

  private UiConstants() {}
}

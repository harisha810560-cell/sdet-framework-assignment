package com.sdet.framework.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.sdet.framework.ui.constants.UiConstants;

public class LoginPage extends BasePage {
  private final Locator username;
  private final Locator password;
  private final Locator loginButton;
  private final Locator error;

  public LoginPage(Page page) {
    super(page);
    username = page.getByTestId(UiConstants.USERNAME);
    password = page.getByTestId(UiConstants.PASSWORD);
    loginButton = page.getByTestId(UiConstants.LOGIN_BUTTON);
    error = page.getByTestId(UiConstants.ERROR_MESSAGE);
  }

  public LoginPage open(String baseUrl) {
    page.navigate(baseUrl);
    waitUntilVisible(loginButton);
    return this;
  }

  /** Submits without assuming the outcome, so rejected logins share this entry point. */
  public LoginPage submitCredentials(String user, String secret) {
    username.fill(user);
    password.fill(secret);
    loginButton.click();
    return this;
  }

  public InventoryPage signIn(String user, String secret) {
    submitCredentials(user, secret);
    return new InventoryPage(page).waitUntilLoaded();
  }

  public String errorMessage() {
    return waitUntilVisible(error).textContent().trim();
  }
}

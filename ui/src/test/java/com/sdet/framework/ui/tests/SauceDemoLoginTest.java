package com.sdet.framework.ui.tests;

import com.sdet.framework.ui.constants.UiConstants;
import com.sdet.framework.ui.data.SauceDemoTestData;
import com.sdet.framework.ui.helpers.flows.RejectedLoginHelper;
import com.sdet.framework.ui.models.UiTestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/** Login stage: the happy path plus every way the form must refuse a sign-in. */
public class SauceDemoLoginTest extends BaseSauceDemoTest {

  @Test(
      description = "Valid credentials sign the shopper in and load the storefront",
      groups = {"ui", "smoke", "regression"})
  public void validCredentialsSignTheShopperIn() {
    signIn();
  }

  @DataProvider(name = "rejectedLogins")
  public Object[][] rejectedLogins() {
    return new Object[][] {
      {
        SauceDemoTestData.UNREGISTERED_USERNAME,
        SauceDemoTestData.UNREGISTERED_PASSWORD,
        UiConstants.INVALID_CREDENTIALS_MESSAGE
      },
      {"", CONFIG.getUi().getPassword(), UiConstants.USERNAME_REQUIRED_MESSAGE},
      {CONFIG.getUi().getUsername(), "", UiConstants.PASSWORD_REQUIRED_MESSAGE},
      {
        CONFIG.getUi().getLockedOutUsername(),
        CONFIG.getUi().getPassword(),
        UiConstants.LOCKED_OUT_MESSAGE
      }
    };
  }

  @Test(
      dataProvider = "rejectedLogins",
      description = "Login is refused with a message that names the reason",
      groups = {"ui", "negative", "regression"})
  public void loginIsRejected(String username, String password, String expectedError) {
    UiTestContext context =
        shopperContext().toBuilder().username(username).password(password).build();
    RejectedLoginHelper.builder()
        .page(page())
        .uiTestContext(context)
        .expectedError(expectedError)
        .build()
        .test();
  }
}

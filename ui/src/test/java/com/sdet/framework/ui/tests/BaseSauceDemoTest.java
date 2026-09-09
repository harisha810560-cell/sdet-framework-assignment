package com.sdet.framework.ui.tests;

import com.sdet.framework.commons.ui.base.BaseUiTest;
import com.sdet.framework.ui.data.SauceDemoTestData;
import com.sdet.framework.ui.helpers.saucedemo.LoginHelper;
import com.sdet.framework.ui.models.UiTestContext;

/** Shared flow contract and sign-in precondition for the SauceDemo suites. */
public abstract class BaseSauceDemoTest extends BaseUiTest {

  protected UiTestContext shopperContext() {
    return UiTestContext.builder()
        .baseUrl(CONFIG.getUi().getBaseUrl())
        .username(CONFIG.getUi().getUsername())
        .password(CONFIG.getUi().getPassword())
        .productName(SauceDemoTestData.TARGET_PRODUCT)
        .checkoutData(SauceDemoTestData.validShippingDetails())
        .build();
  }

  protected UiTestContext signIn() {
    LoginHelper loginHelper =
        LoginHelper.builder().page(page()).uiTestContext(shopperContext()).build();
    loginHelper.test();
    return loginHelper.getUiTestContext();
  }
}

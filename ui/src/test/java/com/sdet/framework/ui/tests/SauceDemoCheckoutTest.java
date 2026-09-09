package com.sdet.framework.ui.tests;

import com.sdet.framework.ui.constants.UiConstants;
import com.sdet.framework.ui.data.SauceDemoTestData;
import com.sdet.framework.ui.helpers.flows.RejectedCheckoutHelper;
import com.sdet.framework.ui.helpers.saucedemo.AddItemToCartHelper;
import com.sdet.framework.ui.helpers.saucedemo.CheckoutHelper;
import com.sdet.framework.ui.models.UiTestContext;
import org.testng.annotations.Test;

/** Checkout stage, from a cart that already holds a product. */
public class SauceDemoCheckoutTest extends BaseSauceDemoTest {

  @Test(
      description = "Complete shipping details produce an order confirmation",
      groups = {"ui", "regression"})
  public void completeShippingDetailsConfirmTheOrder() {
    CheckoutHelper.builder().page(page()).uiTestContext(cartWithOneProduct()).build().test();
  }

  @Test(
      description = "Shipping details without a first name are refused",
      groups = {"ui", "negative", "regression"})
  public void shippingDetailsWithoutFirstNameAreRefused() {
    UiTestContext context =
        cartWithOneProduct().toBuilder()
            .checkoutData(SauceDemoTestData.shippingDetailsMissingFirstName())
            .build();
    RejectedCheckoutHelper.builder()
        .page(page())
        .uiTestContext(context)
        .expectedError(UiConstants.FIRST_NAME_REQUIRED_MESSAGE)
        .build()
        .test();
  }

  private UiTestContext cartWithOneProduct() {
    AddItemToCartHelper addItemToCartHelper =
        AddItemToCartHelper.builder().page(page()).uiTestContext(signIn()).build();
    addItemToCartHelper.test();
    return addItemToCartHelper.getUiTestContext();
  }
}

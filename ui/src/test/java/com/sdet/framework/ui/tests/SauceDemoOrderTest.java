package com.sdet.framework.ui.tests;

import com.sdet.framework.ui.helpers.saucedemo.AddItemToCartHelper;
import com.sdet.framework.ui.helpers.saucedemo.CheckoutHelper;
import com.sdet.framework.ui.models.UiTestContext;
import org.testng.annotations.Test;

/**
 * End-to-end purchase journey. Each stage asserts its own outcome in the helper's validate phase.
 * The stages are also covered in isolation by the login, cart and checkout suites.
 */
public class SauceDemoOrderTest extends BaseSauceDemoTest {

  @Test(
      description = "Shopper signs in, adds a product and completes checkout",
      groups = {"ui", "smoke", "regression"})
  public void shopperCompletesOrderEndToEnd() {
    UiTestContext context = signIn();

    AddItemToCartHelper addItemToCartHelper =
        AddItemToCartHelper.builder().page(page()).uiTestContext(context).build();
    addItemToCartHelper.test();
    context = addItemToCartHelper.getUiTestContext();

    CheckoutHelper checkoutHelper =
        CheckoutHelper.builder().page(page()).uiTestContext(context).build();
    checkoutHelper.test();
  }
}

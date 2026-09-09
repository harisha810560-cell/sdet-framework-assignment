package com.sdet.framework.ui.tests;

import com.sdet.framework.ui.data.SauceDemoTestData;
import com.sdet.framework.ui.helpers.flows.EmptyCartHelper;
import com.sdet.framework.ui.helpers.saucedemo.AddItemToCartHelper;
import com.sdet.framework.ui.helpers.saucedemo.AddMultipleItemsToCartHelper;
import com.sdet.framework.ui.models.UiTestContext;
import org.testng.annotations.Test;

/** Cart stage, including the no-item and multi-item boundaries. */
public class SauceDemoCartTest extends BaseSauceDemoTest {

  @Test(
      description = "Adding a product puts that exact product and price in the cart",
      groups = {"ui", "regression"})
  public void addingProductPutsItInTheCart() {
    UiTestContext context = signIn();
    AddItemToCartHelper.builder().page(page()).uiTestContext(context).build().test();
  }

  @Test(
      description = "Cart holds every product added in one session",
      groups = {"ui", "regression", "boundary"})
  public void cartHoldsEveryProductAdded() {
    UiTestContext context = signIn();
    AddMultipleItemsToCartHelper.builder()
        .page(page())
        .uiTestContext(context)
        .productCount(SauceDemoTestData.MULTI_ITEM_PRODUCT_COUNT)
        .build()
        .test();
  }

  @Test(
      description = "A fresh session starts with an empty cart and no badge",
      groups = {"ui", "regression", "boundary"})
  public void freshSessionStartsWithAnEmptyCart() {
    UiTestContext context = signIn();
    EmptyCartHelper.builder().page(page()).uiTestContext(context).build().test();
  }
}

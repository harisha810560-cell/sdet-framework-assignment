package com.sdet.framework.ui.helpers.saucedemo;

import com.sdet.framework.commons.base.ServiceHelper;
import com.sdet.framework.ui.helpers.BaseUiHelper;
import com.sdet.framework.ui.pages.CartPage;
import com.sdet.framework.ui.pages.CheckoutPage;
import com.sdet.framework.ui.validators.SauceDemoValidator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@SuperBuilder
public class CheckoutHelper extends BaseUiHelper {
  private CartPage cartPage;
  private CheckoutPage checkoutPage;

  @Override
  public ServiceHelper init() {
    cartPage = new CartPage(page).waitUntilLoaded();
    log.info("Cart page ready with {} items", cartPage.itemCount());
    return this;
  }

  @Override
  public ServiceHelper process() {
    checkoutPage =
        cartPage
            .checkout()
            .fillShippingDetails(uiTestContext.getCheckoutData())
            .continueToOverview()
            .finishOrder();
    log.info("Checkout completed");
    return this;
  }

  @Override
  public ServiceHelper validate() {
    SauceDemoValidator.orderWasCompleted(
        checkoutPage.confirmationHeader(),
        checkoutPage.confirmationText(),
        checkoutPage.currentUrl());
    return this;
  }
}

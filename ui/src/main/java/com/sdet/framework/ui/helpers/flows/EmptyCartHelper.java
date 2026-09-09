package com.sdet.framework.ui.helpers.flows;

import com.sdet.framework.commons.base.ServiceHelper;
import com.sdet.framework.ui.helpers.BaseUiHelper;
import com.sdet.framework.ui.pages.CartPage;
import com.sdet.framework.ui.pages.InventoryPage;
import com.sdet.framework.ui.validators.SauceDemoValidator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

/** Opens the cart without adding anything. */
@Getter
@Slf4j
@SuperBuilder
public class EmptyCartHelper extends BaseUiHelper {
  private InventoryPage inventoryPage;
  private CartPage cartPage;
  private int cartBadgeCount;

  @Override
  public ServiceHelper init() {
    inventoryPage = new InventoryPage(page).waitUntilLoaded();
    return this;
  }

  @Override
  public ServiceHelper process() {
    cartBadgeCount = inventoryPage.cartBadgeCount();
    cartPage = inventoryPage.openCart();
    log.info("Opened the cart without adding a product");
    return this;
  }

  @Override
  public ServiceHelper validate() {
    SauceDemoValidator.cartIsEmpty(cartPage.itemCount(), cartBadgeCount);
    return this;
  }
}

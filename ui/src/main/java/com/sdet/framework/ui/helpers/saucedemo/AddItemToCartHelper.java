package com.sdet.framework.ui.helpers.saucedemo;

import com.sdet.framework.commons.base.ServiceHelper;
import com.sdet.framework.ui.helpers.BaseUiHelper;
import com.sdet.framework.ui.models.Product;
import com.sdet.framework.ui.pages.CartPage;
import com.sdet.framework.ui.pages.InventoryPage;
import com.sdet.framework.ui.validators.SauceDemoValidator;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@SuperBuilder
public class AddItemToCartHelper extends BaseUiHelper {
  private InventoryPage inventoryPage;
  private CartPage cartPage;
  private int cartBadgeCount;

  @Override
  public ServiceHelper init() {
    inventoryPage = new InventoryPage(page).waitUntilLoaded();
    log.info("Inventory page ready with {} products", inventoryPage.productCount());
    return this;
  }

  @Override
  public ServiceHelper process() {
    Product selected = inventoryPage.addFirstProductToCart();
    cartBadgeCount = inventoryPage.cartBadgeCount();
    uiTestContext = uiTestContext.toBuilder().selectedProduct(selected).build();
    cartPage = inventoryPage.openCart();
    log.info("Added {} ({}) to the cart", selected.getName(), selected.getPrice());
    return this;
  }

  @Override
  public ServiceHelper validate() {
    SauceDemoValidator.cartMatchesSelection(
        cartPage.products(), uiTestContext.getSelectedProduct(), cartBadgeCount);
    return this;
  }
}

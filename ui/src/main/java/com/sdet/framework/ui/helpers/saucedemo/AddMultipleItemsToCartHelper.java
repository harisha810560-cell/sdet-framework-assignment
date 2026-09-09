package com.sdet.framework.ui.helpers.saucedemo;

import com.sdet.framework.commons.base.ServiceHelper;
import com.sdet.framework.ui.helpers.BaseUiHelper;
import com.sdet.framework.ui.models.Product;
import com.sdet.framework.ui.pages.CartPage;
import com.sdet.framework.ui.pages.InventoryPage;
import com.sdet.framework.ui.validators.SauceDemoValidator;
import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@SuperBuilder
public class AddMultipleItemsToCartHelper extends BaseUiHelper {
  private final int productCount;
  private InventoryPage inventoryPage;
  private CartPage cartPage;
  private int cartBadgeCount;

  @Override
  public ServiceHelper init() {
    inventoryPage = new InventoryPage(page).waitUntilLoaded();
    if (productCount > inventoryPage.productCount()) {
      throw new IllegalStateException(
          "Requested "
              + productCount
              + " products, storefront has "
              + inventoryPage.productCount());
    }
    return this;
  }

  @Override
  public ServiceHelper process() {
    List<Product> selected = inventoryPage.addProductsToCart(productCount);
    cartBadgeCount = inventoryPage.cartBadgeCount();
    uiTestContext = uiTestContext.toBuilder().selectedProducts(selected).build();
    cartPage = inventoryPage.openCart();
    log.info("Added {} products to the cart", selected.size());
    return this;
  }

  @Override
  public ServiceHelper validate() {
    SauceDemoValidator.cartHoldsProducts(
        cartPage.products(), uiTestContext.getSelectedProducts(), cartBadgeCount);
    return this;
  }
}

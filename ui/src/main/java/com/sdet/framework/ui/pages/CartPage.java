package com.sdet.framework.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.sdet.framework.ui.constants.UiConstants;
import com.sdet.framework.ui.models.Product;
import java.util.List;

public class CartPage extends BasePage {
  private final Locator items;
  private final Locator checkoutButton;

  public CartPage(Page page) {
    super(page);
    items = page.getByTestId(UiConstants.INVENTORY_ITEM);
    checkoutButton = page.getByTestId(UiConstants.CHECKOUT_BUTTON);
  }

  public CartPage waitUntilLoaded() {
    waitUntilVisible(checkoutButton);
    return this;
  }

  public int itemCount() {
    return items.count();
  }

  public List<Product> products() {
    return items.all().stream()
        .map(
            item ->
                Product.builder()
                    .name(item.getByTestId(UiConstants.INVENTORY_ITEM_NAME).textContent().trim())
                    .price(item.getByTestId(UiConstants.INVENTORY_ITEM_PRICE).textContent().trim())
                    .build())
        .toList();
  }

  public CheckoutPage checkout() {
    checkoutButton.click();
    return new CheckoutPage(page).waitUntilDetailsLoaded();
  }
}

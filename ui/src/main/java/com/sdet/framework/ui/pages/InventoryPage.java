package com.sdet.framework.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.sdet.framework.ui.constants.UiConstants;
import com.sdet.framework.ui.models.Product;
import java.util.List;
import java.util.stream.IntStream;

public class InventoryPage extends BasePage {
  private final Locator container;
  private final Locator items;

  public InventoryPage(Page page) {
    super(page);
    container = page.getByTestId(UiConstants.INVENTORY_CONTAINER);
    items = page.getByTestId(UiConstants.INVENTORY_ITEM);
  }

  public InventoryPage waitUntilLoaded() {
    waitUntilVisible(container);
    return this;
  }

  public boolean isLoaded() {
    return container.isVisible();
  }

  public int productCount() {
    return items.count();
  }

  /** Adds the product at the given position and returns it as displayed. */
  public Product addProductToCart(int index) {
    Locator item = items.nth(index);
    Product product =
        Product.builder()
            .name(item.getByTestId(UiConstants.INVENTORY_ITEM_NAME).textContent().trim())
            .price(item.getByTestId(UiConstants.INVENTORY_ITEM_PRICE).textContent().trim())
            .build();
    item.locator(UiConstants.ADD_TO_CART_CSS).click();
    return product;
  }

  public Product addFirstProductToCart() {
    return addProductToCart(0);
  }

  public List<Product> addProductsToCart(int count) {
    return IntStream.range(0, count).mapToObj(this::addProductToCart).toList();
  }
}

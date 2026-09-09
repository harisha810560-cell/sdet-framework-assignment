package com.sdet.framework.ui.models;

import lombok.Builder;
import lombok.Value;

/** A product as displayed by the storefront. */
@Value
@Builder(toBuilder = true)
public class Product {
  String name;
  String price;
}

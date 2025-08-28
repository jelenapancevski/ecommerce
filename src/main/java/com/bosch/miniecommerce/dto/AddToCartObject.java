package com.bosch.miniecommerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class AddToCartObject {
    @NotNull(message = "Product id is required.")
    private Long productId;

    @NotNull(message = "Quantity is required.")
    @Positive(message = "Quantity must pe positive.")
    private int quantity;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

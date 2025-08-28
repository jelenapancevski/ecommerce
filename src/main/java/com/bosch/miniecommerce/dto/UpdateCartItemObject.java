package com.bosch.miniecommerce.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UpdateCartItemObject {
    @NotNull(message = "Quantity is required.")
    @Positive(message = "Quantity must pe positive.")
    private int quantity;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

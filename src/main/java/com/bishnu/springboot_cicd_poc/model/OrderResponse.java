package com.bishnu.springboot_cicd_poc.model;
import java.math.BigDecimal;

public class OrderResponse {

    private Long id;
    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalAmount;

    public OrderResponse() {
    }

    public OrderResponse(Long id,
                         String productName,
                         Integer quantity,
                         BigDecimal price,
                         BigDecimal totalAmount) {

        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = totalAmount;
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}

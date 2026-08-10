package com.bishnu.springboot_cicd_poc.service;


import com.bishnu.springboot_cicd_poc.exception.OrderNotFoundException;
import com.bishnu.springboot_cicd_poc.model.OrderRequest;
import com.bishnu.springboot_cicd_poc.model.OrderResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private final OrderService orderService = new OrderService();

    @Test
    void shouldCreateOrderSuccessfully() {

        OrderRequest request = new OrderRequest();
        request.setProductName("Laptop");
        request.setQuantity(2);
        request.setPrice(new BigDecimal("50000"));

        OrderResponse response = orderService.createOrder(request);

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Laptop", response.getProductName());
        assertEquals(2, response.getQuantity());
        assertEquals(new BigDecimal("50000"), response.getPrice());
        assertEquals(new BigDecimal("100000"), response.getTotalAmount());
    }

    @Test
    void shouldRetrieveExistingOrder() {

        OrderRequest request = new OrderRequest();
        request.setProductName("Phone");
        request.setQuantity(2);
        request.setPrice(new BigDecimal("20000"));

        OrderResponse created = orderService.createOrder(request);

        OrderResponse fetched = orderService.getOrder(created.getId());

        assertEquals(created.getId(), fetched.getId());
        assertEquals("Phone", fetched.getProductName());
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {

        OrderNotFoundException exception = assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(999L));

        assertEquals("Order not found with id: 999", exception.getMessage());
    }
}
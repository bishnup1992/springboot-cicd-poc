package com.bishnu.springboot_cicd_poc.controller;


import com.bishnu.springboot_cicd_poc.model.OrderRequest;
import com.bishnu.springboot_cicd_poc.model.OrderResponse;
import com.bishnu.springboot_cicd_poc.service.OrderService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {

        OrderResponse response =
                new OrderResponse(
                        1L,
                        "Laptop",
                        2,
                        new BigDecimal("50000"),
                        new BigDecimal("100000")
                );

        when(orderService.createOrder(any(OrderRequest.class)))
                .thenReturn(response);

        OrderRequest request = new OrderRequest();
        request.setProductName("Laptop");
        request.setQuantity(2);
        request.setPrice(new BigDecimal("50000"));

        mockMvc.perform(
                        post("/api/orders")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productName").value("Laptop"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.totalAmount").value(100000));
    }

    @Test
    void shouldRejectInvalidOrder() throws Exception {

        OrderRequest request = new OrderRequest();
        request.setProductName("");
        request.setQuantity(0);
        request.setPrice(new BigDecimal("-100"));

        mockMvc.perform(
                        post("/api/orders")
                                .contentType("application/json")
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.productName").exists())
                .andExpect(jsonPath("$.quantity").exists())
                .andExpect(jsonPath("$.price").exists());
    }
}
package com.bishnu.springboot_cicd_poc.service;

import com.bishnu.springboot_cicd_poc.exception.OrderNotFoundException;
import com.bishnu.springboot_cicd_poc.model.OrderRequest;
import com.bishnu.springboot_cicd_poc.model.OrderResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class OrderService {
    private final Map<Long, OrderResponse> orders = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    public OrderResponse createOrder(OrderRequest request) {

        Long id = idGenerator.incrementAndGet();

        BigDecimal totalAmount = request.getPrice().multiply(BigDecimal.valueOf(request.getQuantity()));

        OrderResponse response = new OrderResponse(id, request.getProductName(), request.getQuantity(), request.getPrice(), totalAmount);

        orders.put(id, response);

        return response;
    }

    public OrderResponse getOrder(Long id) {

        OrderResponse order = orders.get(id);

        if (order == null) {
            throw new OrderNotFoundException(id);
        }

        return order;
    }
}

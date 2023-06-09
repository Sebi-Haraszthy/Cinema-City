package com.Movies.Cinema.City.controller;

import com.Movies.Cinema.City.DTO.OrderDTO;
import com.Movies.Cinema.City.model.Order;
import com.Movies.Cinema.City.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {
    private OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/add")
    public Order buyTicket(@RequestBody OrderDTO orderDTO) throws Throwable {
        return orderService.buyTicket(orderDTO);
    }
}
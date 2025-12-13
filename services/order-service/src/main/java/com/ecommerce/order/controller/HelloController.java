package com.ecommerce.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello from Order Service! 🚀";
    }

    @GetMapping("/status")
    public String status() {
        return "Order Service is running successfully!";
    }
}

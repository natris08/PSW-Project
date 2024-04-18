package com.example.ecommerceapplication.support.dto;

import lombok.Data;
import com.example.ecommerceapplication.entities.Address;
import com.example.ecommerceapplication.entities.Customer;
import com.example.ecommerceapplication.entities.Order;
import com.example.ecommerceapplication.entities.OrderItem;

import java.util.Set;
@Data
public class Purchase {

    private Customer customer;
    private Address shippingAddress;
    private Address billingAddress;
    private Order order;
    private Set<OrderItem> orderItems;

}

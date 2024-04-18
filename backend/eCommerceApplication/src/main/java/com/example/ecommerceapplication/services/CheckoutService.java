package com.example.ecommerceapplication.services;

import com.example.ecommerceapplication.entities.Customer;
import com.example.ecommerceapplication.entities.Order;
import com.example.ecommerceapplication.entities.OrderItem;
import com.example.ecommerceapplication.entities.Product;
import com.example.ecommerceapplication.repositories.CustomerRepository;
import com.example.ecommerceapplication.repositories.ProductRepository;
import com.example.ecommerceapplication.support.dto.Purchase;
import com.example.ecommerceapplication.support.dto.PurchaseResponse;
import com.example.ecommerceapplication.support.exceptions.QuantityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class CheckoutService {

    @Autowired
    private ProductRepository productRepository;
    private CustomerRepository customerRepository;

    @Autowired
    public CheckoutService(ProductRepository productRepository, CustomerRepository customerRepository) {
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public PurchaseResponse placeOrder(Purchase purchase) {

        //Ricaviamo le informazioni dal dto
        Order order = purchase.getOrder();

        //Generiamo un numero di tracking
        String orderTrackingNumber = generateOrderTrackingNumber();
        order.setOrderTrackingNumber(orderTrackingNumber);

        //Aggiungiamo gli orderItems
        Set<OrderItem> orderItems = purchase.getOrderItems();

        //---CHECK QUANTITY---
        for(OrderItem item : orderItems) {
            Optional<Product> op = productRepository.findById(item.getProductId());
            Product product = op.get();
            int quantity = product.getUnitsInStock() - item.getQuantity();
            if(quantity < 0) throw new QuantityException("ERROR: Quantity unavailable!");
            product.setUnitsInStock(quantity);
            productRepository.save(product);
        }//---CHECK QUANTITY

        orderItems.forEach(item -> order.add(item));

        //Aggiungiamo gli indirizzi di spedizione e fatturazione
        order.setBillingAddress(purchase.getBillingAddress());
        order.setShippingAddress(purchase.getShippingAddress());

        //Aggiungiamo il cliente
        Customer customer = purchase.getCustomer();

        //Check per verificare che il cliente esista
        String email = customer.getEmail();

        Customer customerFromDB = customerRepository.findByEmail(email);

        if (customerFromDB != null) {
            //Cliente trovato!
            customer = customerFromDB;
        }

        customer.add(order);

        //Salviamolo nel database
        customerRepository.save(customer);

        //Restituiamo una risposta
        return new PurchaseResponse(orderTrackingNumber);
    }

    private String generateOrderTrackingNumber() {
        return UUID.randomUUID().toString();
    }

}
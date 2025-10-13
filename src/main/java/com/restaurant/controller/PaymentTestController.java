package com.restaurant.controller;

import com.restaurant.model.Order;
import com.restaurant.model.Reservation;
import com.restaurant.model.User;
import com.restaurant.model.RestaurantTable;
import com.restaurant.payment.PaymentServiceManager;
import com.restaurant.payment.PaymentResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Test controller to demonstrate the Adapter Pattern implementation
 * for the payment processing system.
 */
@RestController
@RequestMapping("/api/payment-test")
public class PaymentTestController {

    @Autowired
    private PaymentServiceManager paymentServiceManager;

    /**
     * Tests payment processing with a specific processor.
     * 
     * @param processor the payment processor to use
     * @return response with test results
     */
    @PostMapping("/process/{processor}")
    public ResponseEntity<Map<String, Object>> testPaymentProcessing(@PathVariable String processor) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Create test data
            Order testOrder = createTestOrder();
            double amount = 50.00;
            String paymentMethod = "credit_card";
            
            // Process payment using specified processor
            PaymentResult result = paymentServiceManager.processPayment(processor, testOrder, amount, paymentMethod);
            
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("transactionId", result.getTransactionId());
            response.put("processor", processor);
            response.put("amount", amount);
            response.put("paymentMethod", paymentMethod);
            
            if (!result.isSuccess()) {
                response.put("errorCode", result.getErrorCode());
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error testing payment processing: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Tests payment processing with the default processor.
     * 
     * @return response with test results
     */
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> testDefaultPaymentProcessing() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Create test data
            Order testOrder = createTestOrder();
            double amount = 75.00;
            String paymentMethod = "credit_card";
            
            // Process payment using default processor
            PaymentResult result = paymentServiceManager.processPayment(testOrder, amount, paymentMethod);
            
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("transactionId", result.getTransactionId());
            response.put("processor", paymentServiceManager.getDefaultProcessor().getProcessorName());
            response.put("amount", amount);
            response.put("paymentMethod", paymentMethod);
            
            if (!result.isSuccess()) {
                response.put("errorCode", result.getErrorCode());
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error testing default payment processing: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Tests reservation payment processing.
     * 
     * @param processor the payment processor to use
     * @return response with test results
     */
    @PostMapping("/reservation/{processor}")
    public ResponseEntity<Map<String, Object>> testReservationPayment(@PathVariable String processor) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Create test data
            Reservation testReservation = createTestReservation();
            double amount = 25.00; // Deposit amount
            String paymentMethod = "paypal";
            
            // Process reservation payment
            PaymentResult result = paymentServiceManager.processReservationPayment(processor, testReservation, amount, paymentMethod);
            
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("transactionId", result.getTransactionId());
            response.put("processor", processor);
            response.put("amount", amount);
            response.put("paymentMethod", paymentMethod);
            
            if (!result.isSuccess()) {
                response.put("errorCode", result.getErrorCode());
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error testing reservation payment: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Tests payment refund.
     * 
     * @param processor the payment processor to use
     * @return response with test results
     */
    @PostMapping("/refund/{processor}")
    public ResponseEntity<Map<String, Object>> testPaymentRefund(@PathVariable String processor) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String transactionId = "test_txn_" + System.currentTimeMillis();
            double amount = 25.00;
            
            // Process refund
            PaymentResult result = paymentServiceManager.refundPayment(processor, transactionId, amount);
            
            response.put("success", result.isSuccess());
            response.put("message", result.getMessage());
            response.put("transactionId", result.getTransactionId());
            response.put("processor", processor);
            response.put("refundAmount", amount);
            
            if (!result.isSuccess()) {
                response.put("errorCode", result.getErrorCode());
            }
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error testing payment refund: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Tests payment method validation.
     * 
     * @param processor the payment processor to use
     * @return response with test results
     */
    @PostMapping("/validate/{processor}")
    public ResponseEntity<Map<String, Object>> testPaymentValidation(@PathVariable String processor) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String paymentMethod = "credit_card";
            String cardNumber = "4111111111111111"; // Test card number
            String expiryDate = "12/25";
            
            // Validate payment method
            boolean isValid = paymentServiceManager.validatePaymentMethod(processor, paymentMethod, cardNumber, expiryDate);
            
            response.put("success", true);
            response.put("isValid", isValid);
            response.put("processor", processor);
            response.put("paymentMethod", paymentMethod);
            response.put("message", isValid ? "Payment method is valid" : "Payment method is invalid");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error testing payment validation: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Gets the status of all payment processors.
     * 
     * @return response with processor status
     */
    @GetMapping("/processors/status")
    public ResponseEntity<Map<String, Object>> getProcessorStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Boolean> status = paymentServiceManager.getProcessorStatus();
            String[] supportedMethods = paymentServiceManager.getAllSupportedPaymentMethods();
            
            response.put("success", true);
            response.put("processors", status);
            response.put("supportedMethods", supportedMethods);
            response.put("defaultProcessor", paymentServiceManager.getDefaultProcessor().getProcessorName());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error getting processor status: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Changes the default payment processor.
     * 
     * @param processor the new default processor
     * @return response with change results
     */
    @PostMapping("/processors/default/{processor}")
    public ResponseEntity<Map<String, Object>> setDefaultProcessor(@PathVariable String processor) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            paymentServiceManager.setDefaultProcessor(processor);
            response.put("success", true);
            response.put("message", "Default processor changed to: " + processor);
            response.put("newDefaultProcessor", paymentServiceManager.getDefaultProcessor().getProcessorName());
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error changing default processor: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Tests all available payment processors.
     * 
     * @return response with test results for all processors
     */
    @PostMapping("/test-all-processors")
    public ResponseEntity<Map<String, Object>> testAllProcessors() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> results = new HashMap<>();
        
        try {
            // Create test data
            Order testOrder = createTestOrder();
            double amount = 100.00;
            String paymentMethod = "credit_card";
            
            // Test each available processor
            String[] processors = {"stripe", "paypal"};
            
            for (String processor : processors) {
                try {
                    PaymentResult result = paymentServiceManager.processPayment(processor, testOrder, amount, paymentMethod);
                    results.put(processor, Map.of(
                        "success", result.isSuccess(),
                        "message", result.getMessage(),
                        "transactionId", result.getTransactionId() != null ? result.getTransactionId() : "N/A"
                    ));
                } catch (Exception e) {
                    results.put(processor, Map.of(
                        "success", false,
                        "message", "Error: " + e.getMessage()
                    ));
                }
            }
            
            response.put("success", true);
            response.put("message", "All processors tested");
            response.put("results", results);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error testing all processors: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
        }
        
        return ResponseEntity.ok(response);
    }

    // Helper methods to create test data
    
    private Order createTestOrder() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setTotalAmount(BigDecimal.valueOf(50.00));
        order.setCreatedAt(LocalDateTime.now());
        
        return order;
    }
    
    private Reservation createTestReservation() {
        User user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");
        
        RestaurantTable table = new RestaurantTable();
        table.setId(1L);
        table.setName("Table 1");
        table.setCapacity(4);
        
        Reservation reservation = new Reservation();
        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setTable(table);
        reservation.setReservationDateTime(LocalDateTime.now().plusDays(1));
        reservation.setNumberOfGuests(2);
        
        return reservation;
    }
}

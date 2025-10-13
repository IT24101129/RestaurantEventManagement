package com.restaurant.payment.thirdparty.impl;

import com.restaurant.payment.thirdparty.PayPalPaymentService;
import com.restaurant.payment.thirdparty.PayPalPaymentResponse;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of PayPal payment service.
 * In a real application, this would integrate with the actual PayPal API.
 */
@Service
public class PayPalPaymentServiceImpl implements PayPalPaymentService {
    
    @Override
    public PayPalPaymentResponse processPayment(double amount, String currency, String paymentMethod) {
        // Mock implementation - simulate API call
        try {
            // Simulate processing time
            Thread.sleep(1500);
            
            // Mock successful response
            PayPalPaymentResponse response = new PayPalPaymentResponse();
            response.setSuccess(true);
            response.setTransactionId("TXN_" + System.currentTimeMillis());
            response.setOrderId("ORDER_" + System.currentTimeMillis());
            response.setStatus("COMPLETED");
            response.setAmount(amount);
            response.setCurrency(currency);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return createErrorResponse("PAYPAL_TIMEOUT", "Payment processing timeout");
        } catch (Exception e) {
            return createErrorResponse("PAYPAL_ERROR", "Payment processing failed: " + e.getMessage());
        }
    }
    
    @Override
    public PayPalPaymentResponse refundPayment(String transactionId, double amount) {
        try {
            // Simulate processing time
            Thread.sleep(800);
            
            PayPalPaymentResponse response = new PayPalPaymentResponse();
            response.setSuccess(true);
            response.setTransactionId("REF_" + System.currentTimeMillis());
            response.setStatus("COMPLETED");
            response.setAmount(amount);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return createErrorResponse("PAYPAL_REFUND_TIMEOUT", "Refund processing timeout");
        } catch (Exception e) {
            return createErrorResponse("PAYPAL_REFUND_ERROR", "Refund processing failed: " + e.getMessage());
        }
    }
    
    @Override
    public PayPalPaymentResponse createOrder(double amount, String currency) {
        try {
            // Simulate processing time
            Thread.sleep(600);
            
            PayPalPaymentResponse response = new PayPalPaymentResponse();
            response.setSuccess(true);
            response.setOrderId("ORDER_" + System.currentTimeMillis());
            response.setStatus("CREATED");
            response.setAmount(amount);
            response.setCurrency(currency);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return createErrorResponse("PAYPAL_ORDER_TIMEOUT", "Order creation timeout");
        } catch (Exception e) {
            return createErrorResponse("PAYPAL_ORDER_ERROR", "Order creation failed: " + e.getMessage());
        }
    }
    
    @Override
    public PayPalPaymentResponse captureOrder(String orderId) {
        try {
            // Simulate processing time
            Thread.sleep(700);
            
            PayPalPaymentResponse response = new PayPalPaymentResponse();
            response.setSuccess(true);
            response.setOrderId(orderId);
            response.setTransactionId("TXN_" + System.currentTimeMillis());
            response.setStatus("COMPLETED");
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return createErrorResponse("PAYPAL_CAPTURE_TIMEOUT", "Order capture timeout");
        } catch (Exception e) {
            return createErrorResponse("PAYPAL_CAPTURE_ERROR", "Order capture failed: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isServiceAvailable() {
        // Mock service availability check
        return true;
    }
    
    private PayPalPaymentResponse createErrorResponse(String errorCode, String errorMessage) {
        PayPalPaymentResponse response = new PayPalPaymentResponse();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        response.setStatus("FAILED");
        return response;
    }
}

package com.restaurant.payment.adapter;

import com.restaurant.model.Order;
import com.restaurant.model.Reservation;
import com.restaurant.payment.PaymentProcessor;
import com.restaurant.payment.PaymentResult;
import com.restaurant.payment.thirdparty.PayPalPaymentService;
import com.restaurant.payment.thirdparty.PayPalPaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Adapter for PayPal payment service.
 * This class adapts the PayPal payment service interface to our PaymentProcessor interface.
 * This demonstrates how the Adapter Pattern allows us to integrate different payment providers.
 */
@Component
public class PayPalPaymentAdapter implements PaymentProcessor {
    
    @Autowired
    private PayPalPaymentService payPalPaymentService;
    
    private static final String PROCESSOR_NAME = "PayPal";
    private static final String[] SUPPORTED_METHODS = {"paypal", "paypal_credit", "paypal_debit"};
    
    @Override
    public PaymentResult processOrderPayment(Order order, double amount, String paymentMethod) {
        try {
            // Call PayPal service
            PayPalPaymentResponse response = payPalPaymentService.processPayment(
                amount, 
                "USD", 
                paymentMethod
            );
            
            // Convert PayPal response to our PaymentResult
            return convertPayPalResponse(response, amount, paymentMethod);
            
        } catch (Exception e) {
            return PaymentResult.failure("PAYPAL_ERROR", 
                "Failed to process payment with PayPal: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentResult processReservationPayment(Reservation reservation, double amount, String paymentMethod) {
        try {
            PayPalPaymentResponse response = payPalPaymentService.processPayment(
                amount, 
                "USD", 
                paymentMethod
            );
            
            return convertPayPalResponse(response, amount, paymentMethod);
            
        } catch (Exception e) {
            return PaymentResult.failure("PAYPAL_ERROR", 
                "Failed to process reservation payment with PayPal: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentResult refundPayment(String transactionId, double amount) {
        try {
            PayPalPaymentResponse response = payPalPaymentService.refundPayment(transactionId, amount);
            
            return convertPayPalResponse(response, amount, "refund");
            
        } catch (Exception e) {
            return PaymentResult.failure("PAYPAL_REFUND_ERROR", 
                "Failed to refund payment with PayPal: " + e.getMessage());
        }
    }
    
    @Override
    public boolean validatePaymentMethod(String paymentMethod, String cardNumber, String expiryDate) {
        // PayPal handles validation internally
        // We just need to check if it's a supported PayPal method
        return isPayPalMethod(paymentMethod);
    }
    
    @Override
    public String getProcessorName() {
        return PROCESSOR_NAME;
    }
    
    @Override
    public boolean isAvailable() {
        return payPalPaymentService != null && payPalPaymentService.isServiceAvailable();
    }
    
    @Override
    public String[] getSupportedPaymentMethods() {
        return SUPPORTED_METHODS.clone();
    }
    
    /**
     * Converts PayPal payment response to our PaymentResult format.
     * This is where the adaptation happens - converting between different interfaces.
     */
    private PaymentResult convertPayPalResponse(PayPalPaymentResponse payPalResponse, double amount, String paymentMethod) {
        PaymentResult result = new PaymentResult();
        
        result.setSuccess(payPalResponse.isSuccess());
        result.setTransactionId(payPalResponse.getTransactionId());
        result.setAmount(amount);
        result.setCurrency("USD");
        result.setPaymentMethod(paymentMethod);
        
        if (payPalResponse.isSuccess()) {
            result.setMessage("Payment processed successfully via PayPal");
        } else {
            result.setMessage(payPalResponse.getErrorMessage());
            result.setErrorCode(payPalResponse.getErrorCode());
        }
        
        return result;
    }
    
    /**
     * Checks if the payment method is a PayPal method.
     */
    private boolean isPayPalMethod(String paymentMethod) {
        for (String method : SUPPORTED_METHODS) {
            if (method.equalsIgnoreCase(paymentMethod)) {
                return true;
            }
        }
        return false;
    }
}

package com.restaurant.payment.thirdparty;

/**
 * Third-party PayPal payment service interface.
 * This represents an external service with its own interface that we need to adapt.
 */
public interface PayPalPaymentService {
    
    /**
     * Processes a payment using PayPal.
     * 
     * @param amount the amount to charge
     * @param currency the currency code
     * @param paymentMethod the PayPal payment method
     * @return PayPalPaymentResponse containing the result
     */
    PayPalPaymentResponse processPayment(double amount, String currency, String paymentMethod);
    
    /**
     * Refunds a payment using PayPal.
     * 
     * @param transactionId the PayPal transaction ID
     * @param amount the amount to refund
     * @return PayPalPaymentResponse containing the result
     */
    PayPalPaymentResponse refundPayment(String transactionId, double amount);
    
    /**
     * Creates a PayPal order.
     * 
     * @param amount the amount
     * @param currency the currency code
     * @return PayPalPaymentResponse containing the order details
     */
    PayPalPaymentResponse createOrder(double amount, String currency);
    
    /**
     * Captures a PayPal order.
     * 
     * @param orderId the PayPal order ID
     * @return PayPalPaymentResponse containing the result
     */
    PayPalPaymentResponse captureOrder(String orderId);
    
    /**
     * Checks if PayPal service is available.
     * 
     * @return true if available, false otherwise
     */
    boolean isServiceAvailable();
}

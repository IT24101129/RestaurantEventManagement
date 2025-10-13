package com.restaurant.payment;

import com.restaurant.model.Order;
import com.restaurant.model.Reservation;

/**
 * Target interface for payment processing in the restaurant management system.
 * This interface defines the standard contract that all payment processors must implement.
 * The Adapter Pattern will be used to adapt different payment providers to this interface.
 */
public interface PaymentProcessor {
    
    /**
     * Processes a payment for an order.
     * 
     * @param order the order to process payment for
     * @param amount the amount to charge
     * @param paymentMethod the payment method (credit_card, paypal, etc.)
     * @return PaymentResult containing the result of the payment processing
     */
    PaymentResult processOrderPayment(Order order, double amount, String paymentMethod);
    
    /**
     * Processes a payment for a reservation deposit.
     * 
     * @param reservation the reservation to process payment for
     * @param amount the deposit amount to charge
     * @param paymentMethod the payment method
     * @return PaymentResult containing the result of the payment processing
     */
    PaymentResult processReservationPayment(Reservation reservation, double amount, String paymentMethod);
    
    /**
     * Refunds a payment.
     * 
     * @param transactionId the transaction ID to refund
     * @param amount the amount to refund
     * @return PaymentResult containing the result of the refund
     */
    PaymentResult refundPayment(String transactionId, double amount);
    
    /**
     * Validates a payment method.
     * 
     * @param paymentMethod the payment method to validate
     * @param cardNumber the card number (if applicable)
     * @param expiryDate the expiry date (if applicable)
     * @return boolean indicating if the payment method is valid
     */
    boolean validatePaymentMethod(String paymentMethod, String cardNumber, String expiryDate);
    
    /**
     * Gets the payment processor name.
     * 
     * @return the name of the payment processor
     */
    String getProcessorName();
    
    /**
     * Checks if the payment processor is available.
     * 
     * @return true if the processor is available, false otherwise
     */
    boolean isAvailable();
    
    /**
     * Gets supported payment methods.
     * 
     * @return array of supported payment methods
     */
    String[] getSupportedPaymentMethods();
}

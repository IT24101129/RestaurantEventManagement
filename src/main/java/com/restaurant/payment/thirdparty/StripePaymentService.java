package com.restaurant.payment.thirdparty;

/**
 * Third-party Stripe payment service interface.
 * This represents an external service with its own interface that we need to adapt.
 */
public interface StripePaymentService {
    
    /**
     * Charges a payment using Stripe.
     * 
     * @param amountInCents the amount in cents
     * @param currency the currency code (e.g., "USD")
     * @param paymentMethodId the Stripe payment method ID
     * @return StripePaymentResponse containing the result
     */
    StripePaymentResponse chargePayment(int amountInCents, String currency, String paymentMethodId);
    
    /**
     * Refunds a payment using Stripe.
     * 
     * @param chargeId the Stripe charge ID
     * @param amountInCents the amount to refund in cents
     * @return StripePaymentResponse containing the result
     */
    StripePaymentResponse refundPayment(String chargeId, int amountInCents);
    
    /**
     * Creates a payment intent using Stripe.
     * 
     * @param amountInCents the amount in cents
     * @param currency the currency code
     * @return StripePaymentResponse containing the payment intent
     */
    StripePaymentResponse createPaymentIntent(int amountInCents, String currency);
    
    /**
     * Validates a card using Stripe.
     * 
     * @param cardNumber the card number
     * @param expiryMonth the expiry month
     * @param expiryYear the expiry year
     * @param cvc the CVC code
     * @return boolean indicating if the card is valid
     */
    boolean validateCard(String cardNumber, int expiryMonth, int expiryYear, String cvc);
    
    /**
     * Checks if Stripe service is available.
     * 
     * @return true if available, false otherwise
     */
    boolean isServiceAvailable();
}

package com.restaurant.payment.adapter;

import com.restaurant.model.Order;
import com.restaurant.model.Reservation;
import com.restaurant.payment.PaymentProcessor;
import com.restaurant.payment.PaymentResult;
import com.restaurant.payment.thirdparty.StripePaymentService;
import com.restaurant.payment.thirdparty.StripePaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Adapter for Stripe payment service.
 * This class adapts the Stripe payment service interface to our PaymentProcessor interface.
 * This is the core of the Adapter Pattern implementation.
 */
@Component
public class StripePaymentAdapter implements PaymentProcessor {
    
    @Autowired
    private StripePaymentService stripePaymentService;
    
    private static final String PROCESSOR_NAME = "Stripe";
    private static final String[] SUPPORTED_METHODS = {"credit_card", "debit_card", "stripe_payment"};
    
    @Override
    public PaymentResult processOrderPayment(Order order, double amount, String paymentMethod) {
        try {
            // Convert amount to cents (Stripe uses cents)
            int amountInCents = (int) (amount * 100);
            
            // For demonstration, we'll use a mock payment method ID
            // In real implementation, this would be obtained from the frontend
            String paymentMethodId = generatePaymentMethodId(paymentMethod);
            
            // Call Stripe service
            StripePaymentResponse response = stripePaymentService.chargePayment(
                amountInCents, 
                "USD", 
                paymentMethodId
            );
            
            // Convert Stripe response to our PaymentResult
            return convertStripeResponse(response, amount, paymentMethod);
            
        } catch (Exception e) {
            return PaymentResult.failure("STRIPE_ERROR", 
                "Failed to process payment with Stripe: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentResult processReservationPayment(Reservation reservation, double amount, String paymentMethod) {
        try {
            // Convert amount to cents
            int amountInCents = (int) (amount * 100);
            
            String paymentMethodId = generatePaymentMethodId(paymentMethod);
            
            StripePaymentResponse response = stripePaymentService.chargePayment(
                amountInCents, 
                "USD", 
                paymentMethodId
            );
            
            return convertStripeResponse(response, amount, paymentMethod);
            
        } catch (Exception e) {
            return PaymentResult.failure("STRIPE_ERROR", 
                "Failed to process reservation payment with Stripe: " + e.getMessage());
        }
    }
    
    @Override
    public PaymentResult refundPayment(String transactionId, double amount) {
        try {
            int amountInCents = (int) (amount * 100);
            
            StripePaymentResponse response = stripePaymentService.refundPayment(transactionId, amountInCents);
            
            return convertStripeResponse(response, amount, "refund");
            
        } catch (Exception e) {
            return PaymentResult.failure("STRIPE_REFUND_ERROR", 
                "Failed to refund payment with Stripe: " + e.getMessage());
        }
    }
    
    @Override
    public boolean validatePaymentMethod(String paymentMethod, String cardNumber, String expiryDate) {
        try {
            // Parse expiry date (assuming format MM/YY)
            String[] parts = expiryDate.split("/");
            if (parts.length != 2) {
                return false;
            }
            
            int month = Integer.parseInt(parts[0]);
            int year = 2000 + Integer.parseInt(parts[1]); // Convert YY to YYYY
            
            // For demonstration, we'll use a mock CVC
            String cvc = "123";
            
            return stripePaymentService.validateCard(cardNumber, month, year, cvc);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public String getProcessorName() {
        return PROCESSOR_NAME;
    }
    
    @Override
    public boolean isAvailable() {
        return stripePaymentService != null && stripePaymentService.isServiceAvailable();
    }
    
    @Override
    public String[] getSupportedPaymentMethods() {
        return SUPPORTED_METHODS.clone();
    }
    
    /**
     * Converts Stripe payment response to our PaymentResult format.
     * This is where the adaptation happens - converting between different interfaces.
     */
    private PaymentResult convertStripeResponse(StripePaymentResponse stripeResponse, double amount, String paymentMethod) {
        PaymentResult result = new PaymentResult();
        
        result.setSuccess(stripeResponse.isSuccess());
        result.setTransactionId(stripeResponse.getChargeId());
        result.setAmount(amount);
        result.setCurrency("USD");
        result.setPaymentMethod(paymentMethod);
        
        if (stripeResponse.isSuccess()) {
            result.setMessage("Payment processed successfully via Stripe");
        } else {
            result.setMessage(stripeResponse.getErrorMessage());
            result.setErrorCode(stripeResponse.getErrorCode());
        }
        
        return result;
    }
    
    /**
     * Generates a mock payment method ID for demonstration.
     * In real implementation, this would be obtained from Stripe's frontend integration.
     */
    private String generatePaymentMethodId(String paymentMethod) {
        // This is a mock implementation
        // In real scenario, payment method ID would come from Stripe Elements or similar
        return "pm_" + paymentMethod.toLowerCase() + "_" + System.currentTimeMillis();
    }
}

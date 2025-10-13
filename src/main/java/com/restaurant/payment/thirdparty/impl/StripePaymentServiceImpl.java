package com.restaurant.payment.thirdparty.impl;

import com.restaurant.payment.thirdparty.StripePaymentService;
import com.restaurant.payment.thirdparty.StripePaymentResponse;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of Stripe payment service.
 * In a real application, this would integrate with the actual Stripe API.
 */
@Service
public class StripePaymentServiceImpl implements StripePaymentService {
    
    @Override
    public StripePaymentResponse chargePayment(int amountInCents, String currency, String paymentMethodId) {
        // Mock implementation - simulate API call
        try {
            // Simulate processing time
            Thread.sleep(1000);
            
            // Mock successful response
            StripePaymentResponse response = new StripePaymentResponse();
            response.setSuccess(true);
            response.setChargeId("ch_" + System.currentTimeMillis());
            response.setStatus("succeeded");
            response.setAmountInCents(amountInCents);
            response.setCurrency(currency);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return createErrorResponse("STRIPE_TIMEOUT", "Payment processing timeout");
        } catch (Exception e) {
            return createErrorResponse("STRIPE_ERROR", "Payment processing failed: " + e.getMessage());
        }
    }
    
    @Override
    public StripePaymentResponse refundPayment(String chargeId, int amountInCents) {
        try {
            // Simulate processing time
            Thread.sleep(500);
            
            StripePaymentResponse response = new StripePaymentResponse();
            response.setSuccess(true);
            response.setChargeId("re_" + System.currentTimeMillis());
            response.setStatus("succeeded");
            response.setAmountInCents(amountInCents);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return createErrorResponse("STRIPE_REFUND_TIMEOUT", "Refund processing timeout");
        } catch (Exception e) {
            return createErrorResponse("STRIPE_REFUND_ERROR", "Refund processing failed: " + e.getMessage());
        }
    }
    
    @Override
    public StripePaymentResponse createPaymentIntent(int amountInCents, String currency) {
        try {
            // Simulate processing time
            Thread.sleep(500);
            
            StripePaymentResponse response = new StripePaymentResponse();
            response.setSuccess(true);
            response.setChargeId("pi_" + System.currentTimeMillis());
            response.setStatus("requires_payment_method");
            response.setAmountInCents(amountInCents);
            response.setCurrency(currency);
            
            return response;
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return createErrorResponse("STRIPE_INTENT_TIMEOUT", "Payment intent creation timeout");
        } catch (Exception e) {
            return createErrorResponse("STRIPE_INTENT_ERROR", "Payment intent creation failed: " + e.getMessage());
        }
    }
    
    @Override
    public boolean validateCard(String cardNumber, int expiryMonth, int expiryYear, String cvc) {
        // Mock validation logic
        if (cardNumber == null || cardNumber.length() < 13) {
            return false;
        }
        
        if (expiryMonth < 1 || expiryMonth > 12) {
            return false;
        }
        
        if (expiryYear < 2024) {
            return false;
        }
        
        if (cvc == null || cvc.length() < 3) {
            return false;
        }
        
        // Simulate validation processing
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        return true;
    }
    
    @Override
    public boolean isServiceAvailable() {
        // Mock service availability check
        return true;
    }
    
    private StripePaymentResponse createErrorResponse(String errorCode, String errorMessage) {
        StripePaymentResponse response = new StripePaymentResponse();
        response.setSuccess(false);
        response.setErrorCode(errorCode);
        response.setErrorMessage(errorMessage);
        response.setStatus("failed");
        return response;
    }
}

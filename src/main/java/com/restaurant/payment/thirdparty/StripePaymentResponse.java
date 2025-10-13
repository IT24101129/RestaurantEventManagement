package com.restaurant.payment.thirdparty;

/**
 * Response from Stripe payment service.
 */
public class StripePaymentResponse {
    
    private boolean success;
    private String chargeId;
    private String errorMessage;
    private String errorCode;
    private int amountInCents;
    private String currency;
    private String status;
    
    // Constructors
    public StripePaymentResponse() {}
    
    public StripePaymentResponse(boolean success, String chargeId, String status) {
        this.success = success;
        this.chargeId = chargeId;
        this.status = status;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getChargeId() {
        return chargeId;
    }
    
    public void setChargeId(String chargeId) {
        this.chargeId = chargeId;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public int getAmountInCents() {
        return amountInCents;
    }
    
    public void setAmountInCents(int amountInCents) {
        this.amountInCents = amountInCents;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}

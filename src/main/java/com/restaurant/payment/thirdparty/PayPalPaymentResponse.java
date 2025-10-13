package com.restaurant.payment.thirdparty;

/**
 * Response from PayPal payment service.
 */
public class PayPalPaymentResponse {
    
    private boolean success;
    private String transactionId;
    private String orderId;
    private String errorMessage;
    private String errorCode;
    private double amount;
    private String currency;
    private String status;
    
    // Constructors
    public PayPalPaymentResponse() {}
    
    public PayPalPaymentResponse(boolean success, String transactionId, String status) {
        this.success = success;
        this.transactionId = transactionId;
        this.status = status;
    }
    
    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
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
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
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

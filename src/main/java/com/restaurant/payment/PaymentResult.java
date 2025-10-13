package com.restaurant.payment;

import java.time.LocalDateTime;

/**
 * Represents the result of a payment processing operation.
 * This class encapsulates all the information returned by payment processors.
 */
public class PaymentResult {
    
    private boolean success;
    private String transactionId;
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
    private double amount;
    private String currency;
    private String paymentMethod;
    
    // Constructors
    public PaymentResult() {
        this.timestamp = LocalDateTime.now();
    }
    
    public PaymentResult(boolean success, String message) {
        this();
        this.success = success;
        this.message = message;
    }
    
    public PaymentResult(boolean success, String transactionId, String message) {
        this(success, message);
        this.transactionId = transactionId;
    }
    
    // Builder pattern for easy construction
    public static PaymentResult success(String transactionId, String message) {
        return new PaymentResult(true, transactionId, message);
    }
    
    public static PaymentResult failure(String message) {
        return new PaymentResult(false, message);
    }
    
    public static PaymentResult failure(String errorCode, String message) {
        PaymentResult result = new PaymentResult(false, message);
        result.setErrorCode(errorCode);
        return result;
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
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
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
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    @Override
    public String toString() {
        return "PaymentResult{" +
                "success=" + success +
                ", transactionId='" + transactionId + '\'' +
                ", message='" + message + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", timestamp=" + timestamp +
                ", amount=" + amount +
                ", currency='" + currency + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}

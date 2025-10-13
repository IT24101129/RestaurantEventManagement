package com.restaurant.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

/**
 * Payment Service Manager that manages different payment processors.
 * This class demonstrates how the Adapter Pattern allows us to easily switch
 * between different payment providers without changing the client code.
 */
@Service
public class PaymentServiceManager {
    
    private final Map<String, PaymentProcessor> paymentProcessors = new HashMap<>();
    private PaymentProcessor defaultProcessor;
    
    @Value("${payment.default.processor:stripe}")
    private String defaultProcessorName;
    
    @Autowired
    public PaymentServiceManager(List<PaymentProcessor> processors) {
        // Initialize all available payment processors
        for (PaymentProcessor processor : processors) {
            paymentProcessors.put(processor.getProcessorName().toLowerCase(), processor);
        }
    }
    
    @PostConstruct
    void initDefaultProcessor() {
        setDefaultProcessor(defaultProcessorName);
    }
    
    /**
     * Processes a payment using the specified processor.
     * 
     * @param processorName the name of the processor to use
     * @param order the order to process payment for
     * @param amount the amount to charge
     * @param paymentMethod the payment method
     * @return PaymentResult containing the result
     */
    public PaymentResult processPayment(String processorName, com.restaurant.model.Order order, 
                                      double amount, String paymentMethod) {
        PaymentProcessor processor = getProcessor(processorName);
        if (processor == null) {
            return PaymentResult.failure("PROCESSOR_NOT_FOUND", 
                "Payment processor '" + processorName + "' not found");
        }
        
        if (!processor.isAvailable()) {
            return PaymentResult.failure("PROCESSOR_UNAVAILABLE", 
                "Payment processor '" + processorName + "' is not available");
        }
        
        return processor.processOrderPayment(order, amount, paymentMethod);
    }
    
    /**
     * Processes a payment using the default processor.
     * 
     * @param order the order to process payment for
     * @param amount the amount to charge
     * @param paymentMethod the payment method
     * @return PaymentResult containing the result
     */
    public PaymentResult processPayment(com.restaurant.model.Order order, double amount, String paymentMethod) {
        if (defaultProcessor == null) {
            return PaymentResult.failure("NO_DEFAULT_PROCESSOR", 
                "No default payment processor configured");
        }
        
        return defaultProcessor.processOrderPayment(order, amount, paymentMethod);
    }
    
    /**
     * Processes a reservation payment using the specified processor.
     * 
     * @param processorName the name of the processor to use
     * @param reservation the reservation to process payment for
     * @param amount the amount to charge
     * @param paymentMethod the payment method
     * @return PaymentResult containing the result
     */
    public PaymentResult processReservationPayment(String processorName, com.restaurant.model.Reservation reservation, 
                                                  double amount, String paymentMethod) {
        PaymentProcessor processor = getProcessor(processorName);
        if (processor == null) {
            return PaymentResult.failure("PROCESSOR_NOT_FOUND", 
                "Payment processor '" + processorName + "' not found");
        }
        
        if (!processor.isAvailable()) {
            return PaymentResult.failure("PROCESSOR_UNAVAILABLE", 
                "Payment processor '" + processorName + "' is not available");
        }
        
        return processor.processReservationPayment(reservation, amount, paymentMethod);
    }
    
    /**
     * Processes a reservation payment using the default processor.
     * 
     * @param reservation the reservation to process payment for
     * @param amount the amount to charge
     * @param paymentMethod the payment method
     * @return PaymentResult containing the result
     */
    public PaymentResult processReservationPayment(com.restaurant.model.Reservation reservation, 
                                                  double amount, String paymentMethod) {
        if (defaultProcessor == null) {
            return PaymentResult.failure("NO_DEFAULT_PROCESSOR", 
                "No default payment processor configured");
        }
        
        return defaultProcessor.processReservationPayment(reservation, amount, paymentMethod);
    }
    
    /**
     * Refunds a payment using the specified processor.
     * 
     * @param processorName the name of the processor to use
     * @param transactionId the transaction ID to refund
     * @param amount the amount to refund
     * @return PaymentResult containing the result
     */
    public PaymentResult refundPayment(String processorName, String transactionId, double amount) {
        PaymentProcessor processor = getProcessor(processorName);
        if (processor == null) {
            return PaymentResult.failure("PROCESSOR_NOT_FOUND", 
                "Payment processor '" + processorName + "' not found");
        }
        
        if (!processor.isAvailable()) {
            return PaymentResult.failure("PROCESSOR_UNAVAILABLE", 
                "Payment processor '" + processorName + "' is not available");
        }
        
        return processor.refundPayment(transactionId, amount);
    }
    
    /**
     * Refunds a payment using the default processor.
     * 
     * @param transactionId the transaction ID to refund
     * @param amount the amount to refund
     * @return PaymentResult containing the result
     */
    public PaymentResult refundPayment(String transactionId, double amount) {
        if (defaultProcessor == null) {
            return PaymentResult.failure("NO_DEFAULT_PROCESSOR", 
                "No default payment processor configured");
        }
        
        return defaultProcessor.refundPayment(transactionId, amount);
    }
    
    /**
     * Validates a payment method using the specified processor.
     * 
     * @param processorName the name of the processor to use
     * @param paymentMethod the payment method
     * @param cardNumber the card number (if applicable)
     * @param expiryDate the expiry date (if applicable)
     * @return boolean indicating if the payment method is valid
     */
    public boolean validatePaymentMethod(String processorName, String paymentMethod, 
                                       String cardNumber, String expiryDate) {
        PaymentProcessor processor = getProcessor(processorName);
        if (processor == null || !processor.isAvailable()) {
            return false;
        }
        
        return processor.validatePaymentMethod(paymentMethod, cardNumber, expiryDate);
    }
    
    /**
     * Gets a payment processor by name.
     * 
     * @param processorName the name of the processor
     * @return the payment processor, or null if not found
     */
    public PaymentProcessor getProcessor(String processorName) {
        if (processorName == null) {
            return null;
        }
        return paymentProcessors.get(processorName.toLowerCase());
    }
    
    /**
     * Gets all available payment processors.
     * 
     * @return map of processor names and their instances
     */
    public Map<String, PaymentProcessor> getAllProcessors() {
        return new HashMap<>(paymentProcessors);
    }
    
    /**
     * Gets the default payment processor.
     * 
     * @return the default processor
     */
    public PaymentProcessor getDefaultProcessor() {
        return defaultProcessor;
    }
    
    /**
     * Sets the default payment processor.
     * 
     * @param processorName the name of the processor to set as default
     */
    public void setDefaultProcessor(String processorName) {
        PaymentProcessor processor = getProcessor(processorName);
        if (processor != null && processor.isAvailable()) {
            this.defaultProcessor = processor;
        } else {
            // Fallback to first available processor
            Optional<PaymentProcessor> firstAvailable = paymentProcessors.values().stream()
                .filter(PaymentProcessor::isAvailable)
                .findFirst();
            
            if (firstAvailable.isPresent()) {
                this.defaultProcessor = firstAvailable.get();
            }
        }
    }
    
    /**
     * Gets the status of all payment processors.
     * 
     * @return map of processor names and their availability status
     */
    public Map<String, Boolean> getProcessorStatus() {
        Map<String, Boolean> status = new HashMap<>();
        for (Map.Entry<String, PaymentProcessor> entry : paymentProcessors.entrySet()) {
            status.put(entry.getKey(), entry.getValue().isAvailable());
        }
        return status;
    }
    
    /**
     * Gets all supported payment methods across all processors.
     * 
     * @return array of all supported payment methods
     */
    public String[] getAllSupportedPaymentMethods() {
        return paymentProcessors.values().stream()
            .filter(PaymentProcessor::isAvailable)
            .flatMap(processor -> java.util.Arrays.stream(processor.getSupportedPaymentMethods()))
            .distinct()
            .toArray(String[]::new);
    }
}

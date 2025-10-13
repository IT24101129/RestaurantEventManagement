// Payment JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializePaymentForm();
    initializePaymentMethodHandlers();
    initializeFormValidation();
    initializeCardFormatting();
});

function initializePaymentForm() {
    console.log('Payment form initialized');
    
    // Set minimum date for expiry
    const currentDate = new Date();
    const currentYear = currentDate.getFullYear() % 100;
    const currentMonth = currentDate.getMonth() + 1;
    
    // Add event listeners for real-time validation
    const form = document.getElementById('paymentForm');
    if (form) {
        form.addEventListener('submit', function(e) {
            if (!validatePaymentForm()) {
                e.preventDefault();
                return false;
            }
            
            // Show loading state
            const submitBtn = document.getElementById('payButton');
            showLoading(submitBtn);
        });
    }
}

function initializePaymentMethodHandlers() {
    const paymentMethodSelect = document.getElementById('paymentMethod');
    const creditCardDetails = document.getElementById('creditCardDetails');
    const paypalDetails = document.getElementById('paypalDetails');
    
    if (paymentMethodSelect) {
        paymentMethodSelect.addEventListener('change', function() {
            const selectedMethod = this.value;
            
            // Hide all payment method details
            if (creditCardDetails) creditCardDetails.style.display = 'none';
            if (paypalDetails) paypalDetails.style.display = 'none';
            
            // Show relevant payment method details
            switch (selectedMethod) {
                case 'Credit Card':
                case 'Debit Card':
                    if (creditCardDetails) creditCardDetails.style.display = 'block';
                    break;
                case 'PayPal':
                    if (paypalDetails) paypalDetails.style.display = 'block';
                    break;
            }
        });
    }
}

function initializeFormValidation() {
    // Real-time validation for card number
    const cardNumberInput = document.getElementById('cardNumber');
    if (cardNumberInput) {
        cardNumberInput.addEventListener('input', function() {
            validateCardNumber(this);
        });
    }
    
    // Real-time validation for expiry date
    const expiryDateInput = document.getElementById('expiryDate');
    if (expiryDateInput) {
        expiryDateInput.addEventListener('input', function() {
            validateExpiryDate(this);
        });
    }
    
    // Real-time validation for CVV
    const cvvInput = document.getElementById('cvv');
    if (cvvInput) {
        cvvInput.addEventListener('input', function() {
            validateCVV(this);
        });
    }
}

function initializeCardFormatting() {
    // Format card number with spaces
    const cardNumberInput = document.getElementById('cardNumber');
    if (cardNumberInput) {
        cardNumberInput.addEventListener('input', function() {
            let value = this.value.replace(/\s/g, '').replace(/[^0-9]/gi, '');
            let formattedValue = value.match(/.{1,4}/g)?.join(' ') || value;
            if (formattedValue !== this.value) {
                this.value = formattedValue;
            }
        });
    }
    
    // Format expiry date
    const expiryDateInput = document.getElementById('expiryDate');
    if (expiryDateInput) {
        expiryDateInput.addEventListener('input', function() {
            let value = this.value.replace(/\D/g, '');
            if (value.length >= 2) {
                value = value.substring(0, 2) + '/' + value.substring(2, 4);
            }
            this.value = value;
        });
    }
    
    // Format CVV (numbers only)
    const cvvInput = document.getElementById('cvv');
    if (cvvInput) {
        cvvInput.addEventListener('input', function() {
            this.value = this.value.replace(/\D/g, '');
        });
    }
}

function validatePaymentForm() {
    const paymentMethod = document.getElementById('paymentMethod').value;
    let isValid = true;
    
    // Validate payment method selection
    if (!paymentMethod) {
        showFieldError('paymentMethod', 'Please select a payment method');
        isValid = false;
    } else {
        hideFieldError('paymentMethod');
    }
    
    // Validate credit card details if selected
    if (paymentMethod === 'Credit Card' || paymentMethod === 'Debit Card') {
        if (!validateCardNumber(document.getElementById('cardNumber'))) isValid = false;
        if (!validateExpiryDate(document.getElementById('expiryDate'))) isValid = false;
        if (!validateCVV(document.getElementById('cvv'))) isValid = false;
        if (!validateCardholderName(document.getElementById('cardholderName'))) isValid = false;
    }
    
    // Validate terms acceptance
    const termsCheckbox = document.getElementById('paymentTerms');
    if (!termsCheckbox.checked) {
        showFieldError('paymentTerms', 'You must accept the terms and conditions');
        isValid = false;
    } else {
        hideFieldError('paymentTerms');
    }
    
    return isValid;
}

function validateCardNumber(input) {
    const value = input.value.replace(/\s/g, '');
    const isValid = /^[0-9]{13,19}$/.test(value) && luhnCheck(value);
    
    if (value && !isValid) {
        showFieldError(input, 'Please enter a valid card number');
        return false;
    } else {
        hideFieldError(input);
        return true;
    }
}

function validateExpiryDate(input) {
    const value = input.value;
    const isValid = /^(0[1-9]|1[0-2])\/\d{2}$/.test(value);
    
    if (value && !isValid) {
        showFieldError(input, 'Please enter a valid expiry date (MM/YY)');
        return false;
    } else if (value && isValid) {
        // Check if card is expired
        const [month, year] = value.split('/');
        const currentDate = new Date();
        const currentYear = currentDate.getFullYear() % 100;
        const currentMonth = currentDate.getMonth() + 1;
        
        if (parseInt(year) < currentYear || 
            (parseInt(year) === currentYear && parseInt(month) < currentMonth)) {
            showFieldError(input, 'Card has expired');
            return false;
        }
    }
    
    if (value && isValid) {
        hideFieldError(input);
    }
    return true;
}

function validateCVV(input) {
    const value = input.value;
    const isValid = /^[0-9]{3,4}$/.test(value);
    
    if (value && !isValid) {
        showFieldError(input, 'Please enter a valid CVV (3-4 digits)');
        return false;
    } else {
        hideFieldError(input);
        return true;
    }
}

function validateCardholderName(input) {
    const value = input.value.trim();
    const isValid = value.length >= 2 && /^[a-zA-Z\s]+$/.test(value);
    
    if (value && !isValid) {
        showFieldError(input, 'Please enter a valid cardholder name');
        return false;
    } else {
        hideFieldError(input);
        return true;
    }
}

function luhnCheck(cardNumber) {
    let sum = 0;
    let isEven = false;
    
    for (let i = cardNumber.length - 1; i >= 0; i--) {
        let digit = parseInt(cardNumber.charAt(i));
        
        if (isEven) {
            digit *= 2;
            if (digit > 9) {
                digit -= 9;
            }
        }
        
        sum += digit;
        isEven = !isEven;
    }
    
    return sum % 10 === 0;
}

function showFieldError(input, message) {
    hideFieldError(input);
    
    const errorDiv = document.createElement('div');
    errorDiv.className = 'invalid-feedback';
    errorDiv.textContent = message;
    errorDiv.id = input.id + '-error';
    
    input.classList.add('is-invalid');
    input.parentNode.appendChild(errorDiv);
}

function hideFieldError(input) {
    const errorDiv = document.getElementById(input.id + '-error');
    if (errorDiv) {
        errorDiv.remove();
    }
    input.classList.remove('is-invalid');
    input.classList.add('is-valid');
}

function showLoading(element) {
    if (element) {
        element.classList.add('loading');
        element.disabled = true;
        const originalText = element.innerHTML;
        element.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Processing Payment...';
        
        // Store original text for restoration
        element.dataset.originalText = originalText;
    }
}

function hideLoading(element) {
    if (element) {
        element.classList.remove('loading');
        element.disabled = false;
        if (element.dataset.originalText) {
            element.innerHTML = element.dataset.originalText;
        }
    }
}

// Simulate payment processing
function processPayment() {
    const submitBtn = document.getElementById('payButton');
    showLoading(submitBtn);
    
    // Simulate API call
    setTimeout(() => {
        hideLoading(submitBtn);
        // In a real implementation, this would redirect to success page
        window.location.href = '/reservations/confirmation/' + getReservationId();
    }, 2000);
}

function getReservationId() {
    const path = window.location.pathname;
    const matches = path.match(/\/reservations\/payment\/(\d+)/);
    return matches ? matches[1] : null;
}

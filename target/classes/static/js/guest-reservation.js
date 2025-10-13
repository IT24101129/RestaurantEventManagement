// Guest Reservation JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeGuestReservation();
    initializeGroupOptions();
    initializePaymentOptions();
    initializeAlternativeTimeSelection();
    initializeFormValidation();
});

function initializeGuestReservation() {
    console.log('Guest reservation form initialized');
    
    // Set minimum date to today
    const dateInput = document.getElementById('reservationDate');
    if (dateInput) {
        const today = new Date().toISOString().split('T')[0];
        dateInput.setAttribute('min', today);
        
        // Set maximum date to 30 days from today
        const maxDate = new Date();
        maxDate.setDate(maxDate.getDate() + 30);
        dateInput.setAttribute('max', maxDate.toISOString().split('T')[0]);
    }
    
    // Load pre-selected date and time from session storage
    loadPreselectedValues();
}

function loadPreselectedValues() {
    const selectedDate = sessionStorage.getItem('selectedDate');
    const selectedTime = sessionStorage.getItem('selectedTime');
    
    if (selectedDate) {
        const dateInput = document.getElementById('reservationDate');
        if (dateInput) {
            dateInput.value = selectedDate;
        }
    }
    
    if (selectedTime) {
        const timeSelect = document.getElementById('reservationTime');
        if (timeSelect) {
            timeSelect.value = selectedTime;
        }
    }
    
    // Clear session storage after loading
    sessionStorage.removeItem('selectedDate');
    sessionStorage.removeItem('selectedTime');
}

function initializeGroupOptions() {
    const guestsSelect = document.getElementById('numberOfGuests');
    const groupOptions = document.getElementById('groupOptions');
    
    if (guestsSelect && groupOptions) {
        guestsSelect.addEventListener('change', function() {
            const numberOfGuests = parseInt(this.value);
            
            if (numberOfGuests >= 8) {
                groupOptions.style.display = 'block';
                // Mark as group reservation
                const groupCheckbox = document.querySelector('input[name="isGroupReservation"]');
                if (groupCheckbox) {
                    groupCheckbox.checked = true;
                }
            } else {
                groupOptions.style.display = 'none';
            }
        });
    }
}

function initializePaymentOptions() {
    const paymentCheckbox = document.getElementById('requiresPayment');
    const paymentMethodDiv = document.getElementById('paymentMethodDiv');
    
    if (paymentCheckbox && paymentMethodDiv) {
        paymentCheckbox.addEventListener('change', function() {
            if (this.checked) {
                paymentMethodDiv.style.display = 'block';
            } else {
                paymentMethodDiv.style.display = 'none';
            }
        });
    }
}

function initializeAlternativeTimeSelection() {
    // Add click handlers for alternative time suggestions
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('alternative-time')) {
            const time = e.target.textContent.trim();
            const timeSelect = document.getElementById('reservationTime');
            
            if (timeSelect) {
                // Find matching option
                const options = timeSelect.querySelectorAll('option');
                for (let option of options) {
                    if (formatTime(option.value) === time) {
                        timeSelect.value = option.value;
                        break;
                    }
                }
            }
            
            // Remove the alert
            const alert = e.target.closest('.alert');
            if (alert) {
                alert.remove();
            }
        }
    });
}

function initializeFormValidation() {
    const form = document.getElementById('guestReservationForm');
    if (form) {
        form.addEventListener('submit', function(e) {
            if (!validateGuestForm()) {
                e.preventDefault();
                return false;
            }
            
            // Show loading state
            const submitBtn = form.querySelector('button[type="submit"]');
            showLoading(submitBtn);
        });
    }
}

function validateGuestForm() {
    const form = document.getElementById('guestReservationForm');
    const requiredFields = form.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;
    
    requiredFields.forEach(field => {
        if (!field.value.trim()) {
            field.classList.add('is-invalid');
            isValid = false;
        } else {
            field.classList.remove('is-invalid');
            field.classList.add('is-valid');
        }
    });
    
    // Validate email format
    const emailField = document.getElementById('guestEmail');
    if (emailField && emailField.value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(emailField.value)) {
            emailField.classList.add('is-invalid');
            isValid = false;
        } else {
            emailField.classList.remove('is-invalid');
            emailField.classList.add('is-valid');
        }
    }
    
    // Validate phone format
    const phoneField = document.getElementById('guestPhone');
    if (phoneField && phoneField.value) {
        const phoneRegex = /^[0-9]{10}$/;
        if (!phoneRegex.test(phoneField.value)) {
            phoneField.classList.add('is-invalid');
            isValid = false;
        } else {
            phoneField.classList.remove('is-invalid');
            phoneField.classList.add('is-valid');
        }
    }
    
    // Validate date is not in the past
    const dateField = document.getElementById('reservationDate');
    if (dateField && dateField.value) {
        const selectedDate = new Date(dateField.value);
        const today = new Date();
        today.setHours(0, 0, 0, 0);
        
        if (selectedDate < today) {
            dateField.classList.add('is-invalid');
            showFieldError(dateField, 'Reservation date cannot be in the past');
            isValid = false;
        } else {
            dateField.classList.remove('is-invalid');
            dateField.classList.add('is-valid');
            hideFieldError(dateField);
        }
    }
    
    // Validate terms checkbox
    const termsCheckbox = document.getElementById('terms');
    if (termsCheckbox && !termsCheckbox.checked) {
        termsCheckbox.classList.add('is-invalid');
        isValid = false;
    } else if (termsCheckbox) {
        termsCheckbox.classList.remove('is-invalid');
        termsCheckbox.classList.add('is-valid');
    }
    
    return isValid;
}

function showFieldError(field, message) {
    hideFieldError(field);
    
    const errorDiv = document.createElement('div');
    errorDiv.className = 'invalid-feedback';
    errorDiv.textContent = message;
    errorDiv.id = field.id + '-error';
    
    field.parentNode.appendChild(errorDiv);
}

function hideFieldError(field) {
    const errorDiv = document.getElementById(field.id + '-error');
    if (errorDiv) {
        errorDiv.remove();
    }
}

function formatTime(time) {
    const [hours, minutes] = time.split(':');
    const hour = parseInt(hours);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour % 12 || 12;
    return `${displayHour}:${minutes} ${ampm}`;
}

function showLoading(element) {
    if (element) {
        element.classList.add('loading');
        element.disabled = true;
        const originalText = element.innerHTML;
        element.innerHTML = '<i class="fas fa-spinner fa-spin me-1"></i>Processing...';
        
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

// Real-time availability checking
async function checkAvailability() {
    const dateInput = document.getElementById('reservationDate');
    const timeSelect = document.getElementById('reservationTime');
    const guestsSelect = document.getElementById('numberOfGuests');
    
    if (!dateInput.value || !timeSelect.value || !guestsSelect.value) {
        return;
    }
    
    try {
        const response = await fetch(`/reservations/check-availability?date=${dateInput.value}&time=${timeSelect.value}&guests=${guestsSelect.value}`);
        const availability = await response.json();
        
        if (!availability.isAvailable) {
            showAlert('Selected time slot is no longer available. Please choose an alternative time.', 'warning');
        }
    } catch (error) {
        console.error('Error checking availability:', error);
    }
}

// Add event listeners for real-time checking
document.addEventListener('DOMContentLoaded', function() {
    const dateInput = document.getElementById('reservationDate');
    const timeSelect = document.getElementById('reservationTime');
    const guestsSelect = document.getElementById('numberOfGuests');
    
    if (dateInput) dateInput.addEventListener('change', checkAvailability);
    if (timeSelect) timeSelect.addEventListener('change', checkAvailability);
    if (guestsSelect) guestsSelect.addEventListener('change', checkAvailability);
});

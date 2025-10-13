// Member Reservation JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeMemberReservation();
    initializeLoyaltyPointsPreview();
    initializeFormValidation();
    initializeRealTimeAvailability();
});

function initializeMemberReservation() {
    console.log('Member reservation form initialized');
    
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

function initializeLoyaltyPointsPreview() {
    const guestsSelect = document.getElementById('numberOfGuests');
    const pointsPreview = document.getElementById('pointsPreview');
    const totalPointsPreview = document.getElementById('totalPointsPreview');
    
    if (guestsSelect && pointsPreview && totalPointsPreview) {
        guestsSelect.addEventListener('change', function() {
            updateLoyaltyPointsPreview();
        });
        
        // Initial calculation
        updateLoyaltyPointsPreview();
    }
}

function updateLoyaltyPointsPreview() {
    const guestsSelect = document.getElementById('numberOfGuests');
    const pointsPreview = document.getElementById('pointsPreview');
    const totalPointsPreview = document.getElementById('totalPointsPreview');
    
    if (!guestsSelect || !pointsPreview || !totalPointsPreview) return;
    
    const numberOfGuests = parseInt(guestsSelect.value) || 0;
    const pointsForReservation = calculatePointsForReservation(numberOfGuests);
    const currentPoints = getCurrentLoyaltyPoints();
    const totalPoints = currentPoints + pointsForReservation;
    
    pointsPreview.textContent = `${pointsForReservation} points`;
    totalPointsPreview.textContent = `${totalPoints} points`;
}

function calculatePointsForReservation(numberOfGuests) {
    if (numberOfGuests <= 0) return 0;
    
    // Base points: 10 per guest, bonus for larger parties
    let basePoints = numberOfGuests * 10;
    if (numberOfGuests >= 8) basePoints += 50; // Group bonus
    if (numberOfGuests >= 15) basePoints += 100; // Large group bonus
    
    return basePoints;
}

function getCurrentLoyaltyPoints() {
    const totalPointsElement = document.getElementById('totalPointsPreview');
    if (totalPointsElement) {
        const text = totalPointsElement.textContent;
        const match = text.match(/(\d+)/);
        return match ? parseInt(match[1]) : 0;
    }
    return 0;
}

function initializeFormValidation() {
    const form = document.getElementById('memberReservationForm');
    if (form) {
        form.addEventListener('submit', function(e) {
            if (!validateMemberForm()) {
                e.preventDefault();
                return false;
            }
            
            // Show loading state
            const submitBtn = form.querySelector('button[type="submit"]');
            showLoading(submitBtn);
        });
    }
}

function validateMemberForm() {
    const form = document.getElementById('memberReservationForm');
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
    
    return isValid;
}

function initializeRealTimeAvailability() {
    const dateInput = document.getElementById('reservationDate');
    const timeSelect = document.getElementById('reservationTime');
    const guestsSelect = document.getElementById('numberOfGuests');
    
    if (dateInput) dateInput.addEventListener('change', checkAvailability);
    if (timeSelect) timeSelect.addEventListener('change', checkAvailability);
    if (guestsSelect) guestsSelect.addEventListener('change', checkAvailability);
}

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
        
        displayAvailabilityResult(availability);
    } catch (error) {
        console.error('Error checking availability:', error);
    }
}

function displayAvailabilityResult(availability) {
    // Remove existing availability messages
    const existingAlerts = document.querySelectorAll('.availability-alert');
    existingAlerts.forEach(alert => alert.remove());
    
    if (!availability.isAvailable) {
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-warning availability-alert';
        alertDiv.innerHTML = `
            <i class="fas fa-exclamation-triangle me-2"></i>
            <strong>Time slot unavailable!</strong> 
            ${availability.suggestion || 'Please select a different time.'}
        `;
        
        if (availability.alternatives && availability.alternatives.length > 0) {
            alertDiv.innerHTML += `
                <div class="mt-2">
                    <strong>Alternative times:</strong>
                    <div class="d-flex flex-wrap gap-2 mt-2">
                        ${availability.alternatives.map(time => 
                            `<span class="badge bg-primary alternative-time" style="cursor: pointer;">${formatTime(time)}</span>`
                        ).join('')}
                    </div>
                </div>
            `;
        }
        
        // Insert after the form or in a designated area
        const form = document.querySelector('form');
        if (form) {
            form.insertBefore(alertDiv, form.firstChild);
        }
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

// Initialize alternative time selection
document.addEventListener('DOMContentLoaded', function() {
    initializeAlternativeTimeSelection();
});

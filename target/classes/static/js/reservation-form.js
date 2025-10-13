// Reservation Form JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeReservationForm();
    initializeDateValidation();
    initializeTableAvailability();
});

function initializeReservationForm() {
    const form = document.querySelector('form[th\\:action="@{/reservations}"]');
    if (form) {
        form.addEventListener('submit', function(e) {
            if (!validateReservationForm()) {
                e.preventDefault();
                return false;
            }
        });
    }
}

function initializeDateValidation() {
    const dateInput = document.getElementById('reservationDate');
    if (dateInput) {
        // Set minimum date to today
        const today = new Date().toISOString().split('T')[0];
        dateInput.setAttribute('min', today);
        
        // Set maximum date to 30 days from today
        const maxDate = new Date();
        maxDate.setDate(maxDate.getDate() + 30);
        dateInput.setAttribute('max', maxDate.toISOString().split('T')[0]);
        
        dateInput.addEventListener('change', function() {
            validateReservationDate();
        });
    }
}

function initializeTableAvailability() {
    const dateInput = document.getElementById('reservationDate');
    const timeSelect = document.getElementById('reservationTime');
    const guestsSelect = document.getElementById('numberOfGuests');
    
    if (dateInput && timeSelect && guestsSelect) {
        [dateInput, timeSelect, guestsSelect].forEach(element => {
            element.addEventListener('change', function() {
                checkTableAvailability();
            });
        });
    }
}

function validateReservationForm() {
    const dateInput = document.getElementById('reservationDate');
    const timeSelect = document.getElementById('reservationTime');
    const guestsSelect = document.getElementById('numberOfGuests');
    
    let isValid = true;
    
    if (!dateInput.value) {
        dateInput.classList.add('is-invalid');
        isValid = false;
    } else {
        dateInput.classList.remove('is-invalid');
        dateInput.classList.add('is-valid');
    }
    
    if (!timeSelect.value) {
        timeSelect.classList.add('is-invalid');
        isValid = false;
    } else {
        timeSelect.classList.remove('is-invalid');
        timeSelect.classList.add('is-valid');
    }
    
    if (!guestsSelect.value) {
        guestsSelect.classList.add('is-invalid');
        isValid = false;
    } else {
        guestsSelect.classList.remove('is-invalid');
        guestsSelect.classList.add('is-valid');
    }
    
    return isValid;
}

function validateReservationDate() {
    const dateInput = document.getElementById('reservationDate');
    const selectedDate = new Date(dateInput.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    if (selectedDate < today) {
        dateInput.classList.add('is-invalid');
        showFieldError(dateInput, 'Reservation date cannot be in the past');
        return false;
    } else {
        dateInput.classList.remove('is-invalid');
        dateInput.classList.add('is-valid');
        hideFieldError(dateInput);
        return true;
    }
}

async function checkTableAvailability() {
    const date = document.getElementById('reservationDate').value;
    const time = document.getElementById('reservationTime').value;
    const guests = document.getElementById('numberOfGuests').value;
    
    if (!date || !time || !guests) {
        return;
    }
    
    try {
        const response = await fetch(`/reservations/availability?date=${date}&guests=${guests}`);
        const availableTimes = await response.json();
        
        updateAvailableTables(availableTimes);
    } catch (error) {
        console.error('Error checking availability:', error);
    }
}

function updateAvailableTables(availableTimes) {
    const availableTablesList = document.getElementById('availableTablesList');
    if (availableTablesList) {
        // This would be populated with real data from the server
        availableTablesList.innerHTML = `
            <div class="table-item mb-2 p-2 border rounded">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <strong>Table 1</strong>
                        <small class="text-muted d-block">Capacity: 4</small>
                    </div>
                    <span class="badge bg-success">Available</span>
                </div>
            </div>
        `;
    }
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

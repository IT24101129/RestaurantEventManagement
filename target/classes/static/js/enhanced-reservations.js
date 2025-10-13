// Enhanced Reservations JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeEnhancedReservations();
    loadAvailabilityCalendar();
    initializeAvailabilityChecks();
});

function initializeEnhancedReservations() {
    console.log('Enhanced reservations initialized');
    
    // Add click handlers for reservation options
    const reservationOptions = document.querySelectorAll('.reservation-option');
    reservationOptions.forEach(option => {
        option.addEventListener('click', function() {
            const link = this.querySelector('a');
            if (link) {
                window.location.href = link.href;
            }
        });
    });
}

async function loadAvailabilityCalendar() {
    try {
        const today = new Date().toISOString().split('T')[0];
        const response = await fetch(`/reservations/time-slots?date=${today}&guests=4`);
        const timeSlots = await response.json();
        
        displayAvailabilityCalendar(timeSlots);
    } catch (error) {
        console.error('Error loading availability calendar:', error);
        showAvailabilityError();
    }
}

function displayAvailabilityCalendar(timeSlots) {
    const calendarContainer = document.getElementById('availabilityCalendar');
    if (!calendarContainer) return;
    
    const today = new Date();
    const next7Days = [];
    
    for (let i = 0; i < 7; i++) {
        const date = new Date(today);
        date.setDate(today.getDate() + i);
        next7Days.push(date);
    }
    
    const timeSlotsList = ['17:00', '17:30', '18:00', '18:30', '19:00', '19:30', '20:00', '20:30', '21:00', '21:30'];
    
    let calendarHTML = `
        <div class="table-responsive">
            <table class="table table-bordered availability-table">
                <thead>
                    <tr>
                        <th>Date</th>
                        ${timeSlotsList.map(time => `<th>${formatTime(time)}</th>`).join('')}
                    </tr>
                </thead>
                <tbody>
    `;
    
    next7Days.forEach(date => {
        const dateStr = date.toISOString().split('T')[0];
        const dayName = date.toLocaleDateString('en-US', { weekday: 'short' });
        const dayNumber = date.getDate();
        
        calendarHTML += `
            <tr>
                <td class="date-cell">
                    <div class="fw-bold">${dayName}</div>
                    <div class="text-muted">${dayNumber}</div>
                </td>
        `;
        
        timeSlotsList.forEach(time => {
            const isAvailable = timeSlots.includes(time);
            const availabilityClass = isAvailable ? 'available' : 'unavailable';
            const availabilityText = isAvailable ? '✓' : '✗';
            
            calendarHTML += `
                <td class="time-cell ${availabilityClass}" data-date="${dateStr}" data-time="${time}">
                    ${availabilityText}
                </td>
            `;
        });
        
        calendarHTML += '</tr>';
    });
    
    calendarHTML += `
                </tbody>
            </table>
        </div>
    `;
    
    calendarContainer.innerHTML = calendarHTML;
    
    // Add click handlers for available time slots
    const availableCells = calendarContainer.querySelectorAll('.time-cell.available');
    availableCells.forEach(cell => {
        cell.addEventListener('click', function() {
            const date = this.dataset.date;
            const time = this.dataset.time;
            handleTimeSlotClick(date, time);
        });
    });
}

function showAvailabilityError() {
    const calendarContainer = document.getElementById('availabilityCalendar');
    if (calendarContainer) {
        calendarContainer.innerHTML = `
            <div class="text-center py-4">
                <i class="fas fa-exclamation-triangle fa-3x text-warning mb-3"></i>
                <p class="text-muted">Unable to load availability. Please try again later.</p>
                <button class="btn btn-outline-primary" onclick="loadAvailabilityCalendar()">
                    <i class="fas fa-refresh me-1"></i>Retry
                </button>
            </div>
        `;
    }
}

function formatTime(time) {
    const [hours, minutes] = time.split(':');
    const hour = parseInt(hours);
    const ampm = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour % 12 || 12;
    return `${displayHour}:${minutes} ${ampm}`;
}

function handleTimeSlotClick(date, time) {
    // Redirect to appropriate booking form
    const isLoggedIn = document.body.dataset.isLoggedIn === 'true';
    const url = isLoggedIn ? '/reservations/new' : '/reservations/guest';
    
    // Store selected date and time in session storage
    sessionStorage.setItem('selectedDate', date);
    sessionStorage.setItem('selectedTime', time);
    
    window.location.href = url;
}

function initializeAvailabilityChecks() {
    // Add real-time availability checking
    const dateInput = document.getElementById('reservationDate');
    const timeSelect = document.getElementById('reservationTime');
    const guestsSelect = document.getElementById('numberOfGuests');
    
    if (dateInput && timeSelect && guestsSelect) {
        [dateInput, timeSelect, guestsSelect].forEach(element => {
            element.addEventListener('change', function() {
                checkRealTimeAvailability();
            });
        });
    }
}

async function checkRealTimeAvailability() {
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

// Utility functions
function showLoading(element) {
    if (element) {
        element.classList.add('loading');
        element.disabled = true;
    }
}

function hideLoading(element) {
    if (element) {
        element.classList.remove('loading');
        element.disabled = false;
    }
}

function showAlert(message, type = 'info') {
    const alertContainer = document.querySelector('.alert-container') || createAlertContainer();
    const alert = document.createElement('div');
    alert.className = `alert alert-${type} alert-dismissible fade show`;
    alert.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : type === 'danger' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    alertContainer.appendChild(alert);
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
        alert.remove();
    }, 5000);
}

function createAlertContainer() {
    const container = document.createElement('div');
    container.className = 'alert-container position-fixed top-0 end-0 p-3';
    container.style.zIndex = '9999';
    document.body.appendChild(container);
    return container;
}

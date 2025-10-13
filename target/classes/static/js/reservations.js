// Reservations Management JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeReservations();
    initializeFilters();
    initializeActions();
});

function initializeReservations() {
    console.log('Reservations page initialized');
}

function initializeFilters() {
    const statusFilter = document.getElementById('statusFilter');
    const dateFilter = document.getElementById('dateFilter');
    const searchInput = document.getElementById('searchInput');
    
    if (statusFilter) {
        statusFilter.addEventListener('change', filterReservations);
    }
    
    if (dateFilter) {
        dateFilter.addEventListener('change', filterReservations);
    }
    
    if (searchInput) {
        searchInput.addEventListener('input', debounce(filterReservations, 300));
    }
}

function initializeActions() {
    // Initialize any action buttons
}

function filterReservations() {
    const status = document.getElementById('statusFilter').value;
    const date = document.getElementById('dateFilter').value;
    const search = document.getElementById('searchInput').value.toLowerCase();
    
    const rows = document.querySelectorAll('#reservationsTable tbody tr');
    
    rows.forEach(row => {
        const statusCell = row.querySelector('td:nth-child(6) .badge');
        const dateCell = row.querySelector('td:nth-child(3)');
        const customerCell = row.querySelector('td:nth-child(2)');
        
        let show = true;
        
        if (status && statusCell && !statusCell.textContent.includes(status)) {
            show = false;
        }
        
        if (date && dateCell && !dateCell.textContent.includes(date)) {
            show = false;
        }
        
        if (search && customerCell && !customerCell.textContent.toLowerCase().includes(search)) {
            show = false;
        }
        
        row.style.display = show ? '' : 'none';
    });
}

function confirmReservation(id) {
    showConfirmModal(
        'Confirm Reservation',
        'Are you sure you want to confirm this reservation?',
        () => {
            fetch(`/reservations/${id}/confirm`, { method: 'POST' })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    } else {
                        alert('Error confirming reservation');
                    }
                });
        }
    );
}

function cancelReservation(id) {
    showConfirmModal(
        'Cancel Reservation',
        'Are you sure you want to cancel this reservation?',
        () => {
            fetch(`/reservations/${id}/cancel`, { method: 'POST' })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    } else {
                        alert('Error cancelling reservation');
                    }
                });
        }
    );
}

function showConfirmModal(title, message, onConfirm) {
    const modal = document.getElementById('confirmModal');
    const titleElement = modal.querySelector('.modal-title');
    const messageElement = modal.querySelector('#confirmMessage');
    const confirmButton = modal.querySelector('#confirmButton');
    
    titleElement.textContent = title;
    messageElement.textContent = message;
    
    confirmButton.onclick = () => {
        onConfirm();
        bootstrap.Modal.getInstance(modal).hide();
    };
    
    new bootstrap.Modal(modal).show();
}

function exportReservations() {
    // Implement export functionality
    console.log('Exporting reservations...');
}

function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

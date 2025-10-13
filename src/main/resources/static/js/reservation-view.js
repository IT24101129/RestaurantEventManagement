// Reservation View JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeReservationView();
});

function initializeReservationView() {
    console.log('Reservation view initialized');
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
        'Are you sure you want to cancel this reservation? This action cannot be undone.',
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

function completeReservation(id) {
    showConfirmModal(
        'Complete Reservation',
        'Mark this reservation as completed?',
        () => {
            fetch(`/reservations/${id}/complete`, { method: 'POST' })
                .then(response => {
                    if (response.ok) {
                        location.reload();
                    } else {
                        alert('Error completing reservation');
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

function printReservation() {
    window.print();
}

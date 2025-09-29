// Bookings Page JavaScript
const API_BASE = '/api/banquet-hall';
let allBookings = [];

document.addEventListener('DOMContentLoaded', function() {
    console.log('Bookings page loaded');
    loadAllBookings();
});

function loadAllBookings() {
    showLoading(true);

    fetch(`${API_BASE}/bookings`)
        .then(response => {
            if (!response.ok) throw new Error('Failed to load bookings');
            return response.json();
        })
        .then(bookings => {
            console.log('Loaded all bookings:', bookings);
            allBookings = bookings;
            displayBookings(bookings);
            updateStats(bookings);
            showLoading(false);
        })
        .catch(error => {
            console.error('Error loading bookings:', error);
            showError('Failed to load bookings');
            showLoading(false);
        });
}

function displayBookings(bookings) {
    const tableBody = document.getElementById('bookingsTable');
    const noBookingsMessage = document.getElementById('noBookingsMessage');

    tableBody.innerHTML = '';

    if (bookings.length === 0) {
        tableBody.style.display = 'none';
        noBookingsMessage.style.display = 'block';
        return;
    }

    tableBody.style.display = '';
    noBookingsMessage.style.display = 'none';

    bookings.forEach(booking => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td><strong>#${booking.id || 'N/A'}</strong></td>
            <td>
                <strong>${booking.eventName || 'Unnamed Event'}</strong>
                ${booking.specialRequirements ? '<br><small class="text-muted">📝 ' + booking.specialRequirements + '</small>' : ''}
            </td>
            <td>${booking.clientName || 'Unknown Client'}</td>
            <td>
                ${booking.clientEmail ? booking.clientEmail + '<br>' : ''}
                ${booking.clientPhone ? '<small>' + booking.clientPhone + '</small>' : ''}
            </td>
            <td>${formatDateTime(booking.startTime)}</td>
            <td class="text-center">${booking.numberOfGuests || 0}</td>
            <td class="text-center">Hall ${booking.banquetHallId || 1}</td>
            <td>${getStatusBadge(booking.status)}</td>
            <td>
                <div class="btn-group-vertical">
                    ${booking.status === 'PENDING' ? `
                        <button class="btn btn-success btn-sm" onclick="approveBooking(${booking.id})">✅ Approve</button>
                        <button class="btn btn-danger btn-sm" onclick="rejectBooking(${booking.id})">❌ Reject</button>
                    ` : ''}
                    <button class="btn btn-info btn-sm" onclick="viewBookingDetails(${booking.id})">👁️ View</button>
                    <button class="btn btn-warning btn-sm" onclick="editBooking(${booking.id})">✏️ Edit</button>
                </div>
            </td>
        `;
        tableBody.appendChild(row);
    });
}

function filterBookings() {
    const statusFilter = document.getElementById('statusFilter').value;
    const searchFilter = document.getElementById('searchFilter').value.toLowerCase();

    let filtered = allBookings;

    // Status filter
    if (statusFilter !== 'ALL') {
        filtered = filtered.filter(booking => booking.status === statusFilter);
    }

    // Search filter
    if (searchFilter) {
        filtered = filtered.filter(booking =>
            (booking.eventName && booking.eventName.toLowerCase().includes(searchFilter)) ||
            (booking.clientName && booking.clientName.toLowerCase().includes(searchFilter)) ||
            (booking.clientEmail && booking.clientEmail.toLowerCase().includes(searchFilter))
        );
    }

    displayBookings(filtered);
    updateStats(filtered);
}

function updateStats(bookings) {
    const total = bookings.length;
    const pending = bookings.filter(b => b.status === 'PENDING').length;
    const approved = bookings.filter(b => b.status === 'APPROVED').length;
    const rejected = bookings.filter(b => b.status === 'REJECTED').length;

    document.getElementById('totalBookings').textContent = total;
    document.getElementById('pendingBookings').textContent = pending;
    document.getElementById('approvedBookings').textContent = approved;
    document.getElementById('rejectedBookings').textContent = rejected;
}

function approveBooking(bookingId) {
    if (confirm('Approve this booking?')) {
        fetch(`${API_BASE}/bookings/${bookingId}/approve`, { method: 'PUT' })
            .then(response => {
                if (!response.ok) throw new Error('Approval failed');
                return response.json();
            })
            .then(data => {
                alert('✅ Booking approved!');
                loadAllBookings();
            })
            .catch(error => {
                alert('❌ Error: ' + error.message);
            });
    }
}

function rejectBooking(bookingId) {
    if (confirm('Reject this booking?')) {
        fetch(`${API_BASE}/bookings/${bookingId}/reject`, { method: 'PUT' })
            .then(response => {
                if (!response.ok) throw new Error('Rejection failed');
                return response.json();
            })
            .then(data => {
                alert('✅ Booking rejected!');
                loadAllBookings();
            })
            .catch(error => {
                alert('❌ Error: ' + error.message);
            });
    }
}

function viewBookingDetails(bookingId) {
    fetch(`${API_BASE}/bookings/${bookingId}`)
        .then(response => response.json())
        .then(booking => {
            const details = `
📅 EVENT: ${booking.eventName || 'N/A'}
👤 CLIENT: ${booking.clientName || 'N/A'}
📞 PHONE: ${booking.clientPhone || 'N/A'}
📧 EMAIL: ${booking.clientEmail || 'N/A'}
🕐 DATE: ${formatDateTime(booking.startTime)}
⏰ DURATION: ${calculateDuration(booking.startTime, booking.endTime)}
👥 GUESTS: ${booking.numberOfGuests || 0}
🏛️ HALL ID: ${booking.banquetHallId || 'N/A'}
📝 REQUIREMENTS: ${booking.specialRequirements || 'None'}
✅ STATUS: ${booking.status || 'N/A'}
        `;
            alert(details);
        })
        .catch(error => {
            alert('Error loading details');
        });
}

function editBooking(bookingId) {
    alert('Edit feature coming soon! Booking ID: ' + bookingId);
}

function exportBookings() {
    alert('Export feature coming soon!');
}

function showLoading(show) {
    document.getElementById('loadingMessage').style.display = show ? 'block' : 'none';
}

function showError(message) {
    const tableBody = document.getElementById('bookingsTable');
    tableBody.innerHTML = `<tr><td colspan="9" class="text-center text-danger">${message}</td></tr>`;
}

// Utility functions
function formatDateTime(dateTimeString) {
    if (!dateTimeString) return 'N/A';
    try {
        const date = new Date(dateTimeString);
        return date.toLocaleString();
    } catch (e) {
        return 'Invalid Date';
    }
}

function calculateDuration(start, end) {
    if (!start || !end) return 'N/A';
    try {
        const startDate = new Date(start);
        const endDate = new Date(end);
        const diffHours = (endDate - startDate) / (1000 * 60 * 60);
        return Math.round(diffHours) + ' hours';
    } catch (e) {
        return 'N/A';
    }
}

function getStatusBadge(status) {
    const badges = {
        'PENDING': '<span class="badge bg-warning">PENDING</span>',
        'APPROVED': '<span class="badge bg-success">APPROVED</span>',
        'REJECTED': '<span class="badge bg-danger">REJECTED</span>'
    };
    return badges[status] || '<span class="badge bg-secondary">UNKNOWN</span>';
}
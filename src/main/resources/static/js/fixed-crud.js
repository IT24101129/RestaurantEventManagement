// Fixed CRUD Operations JavaScript
const API_BASE = '/api/banquet-hall';

// Load all data when page loads
document.addEventListener('DOMContentLoaded', function() {
    console.log('Dashboard loaded - starting data load...');
    loadAllData();
    setupFormListeners();
});

function setupFormListeners() {
    // CREATE: New booking form
    document.getElementById('createBookingForm').addEventListener('submit', function(e) {
        e.preventDefault();
        createNewBooking();
    });
}

function loadAllData() {
    loadAllBookings();
    loadPendingBookings();
}

// CREATE: Add new booking - FIXED
function createNewBooking() {
    const eventName = document.getElementById('eventName').value;
    const clientName = document.getElementById('clientName').value;

    if (!eventName || !clientName) {
        alert('Please fill Event Name and Client Name');
        return;
    }

    const bookingData = {
        eventName: eventName,
        startTime: document.getElementById('startTime').value + ':00',
        endTime: document.getElementById('endTime').value + ':00',
        clientName: clientName,
        clientEmail: document.getElementById('clientEmail').value || 'test@email.com',
        clientPhone: document.getElementById('clientPhone').value || '123-456-7890',
        numberOfGuests: parseInt(document.getElementById('numberOfGuests').value) || 50,
        specialRequirements: document.getElementById('specialRequirements').value || 'No special requirements',
        banquetHallId: parseInt(document.getElementById('banquetHallId').value) || 1,
        status: 'PENDING'
    };

    console.log('Sending booking data:', bookingData);

    fetch(`${API_BASE}/bookings`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(bookingData)
    })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    console.error('Server error:', text);
                    throw new Error(text)
                });
            }
            return response.json();
        })
        .then(data => {
            console.log('Booking created successfully:', data);
            alert('✅ Booking created successfully!');
            document.getElementById('createBookingForm').reset();
            loadAllData(); // Reload all data
        })
        .catch(error => {
            console.error('Error creating booking:', error);
            alert('❌ Error creating booking: ' + error.message);
        });
}

// READ: Load all bookings - FIXED
function loadAllBookings() {
    document.getElementById('allLoading').style.display = 'block';

    fetch(`${API_BASE}/bookings`)
        .then(response => {
            if (!response.ok) throw new Error('Failed to load bookings');
            return response.json();
        })
        .then(bookings => {
            console.log('Loaded bookings:', bookings);
            const tableBody = document.getElementById('allBookingsTable');
            tableBody.innerHTML = '';

            if (bookings.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="7" class="text-center">No bookings found</td></tr>';
            } else {
                bookings.forEach(booking => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                    <td>${booking.id || 'N/A'}</td>
                    <td><strong>${booking.eventName || 'Unnamed Event'}</strong></td>
                    <td>${booking.clientName || 'Unknown Client'}</td>
                    <td>${formatDateTime(booking.startTime)}</td>
                    <td>${booking.numberOfGuests || 0}</td>
                    <td>${getStatusBadge(booking.status)}</td>
                    <td>
                        ${(booking.status === 'PENDING') ? `
                            <button class="btn btn-success btn-sm" onclick="approveBooking(${booking.id})">Approve</button>
                            <button class="btn btn-danger btn-sm" onclick="rejectBooking(${booking.id})">Reject</button>
                        ` : ''}
                        <button class="btn btn-info btn-sm" onclick="viewBookingDetails(${booking.id})">View</button>
                    </td>
                `;
                    tableBody.appendChild(row);
                });
            }

            document.getElementById('allLoading').style.display = 'none';
            document.getElementById('totalCount').textContent = bookings.length;
            updateStats(bookings);
        })
        .catch(error => {
            console.error('Error loading bookings:', error);
            document.getElementById('allBookingsTable').innerHTML =
                '<tr><td colspan="7" class="text-center text-danger">Error loading bookings</td></tr>';
            document.getElementById('allLoading').style.display = 'none';
        });
}

// READ: Load pending bookings - FIXED
function loadPendingBookings() {
    document.getElementById('pendingLoading').style.display = 'block';

    fetch(`${API_BASE}/bookings/pending`)
        .then(response => {
            if (!response.ok) throw new Error('Failed to load pending bookings');
            return response.json();
        })
        .then(bookings => {
            console.log('Loaded pending bookings:', bookings);
            const tableBody = document.getElementById('pendingBookingsTable');
            tableBody.innerHTML = '';

            if (bookings.length === 0) {
                tableBody.innerHTML = '<tr><td colspan="7" class="text-center">No pending bookings</td></tr>';
            } else {
                bookings.forEach(booking => {
                    const row = document.createElement('tr');
                    row.innerHTML = `
                    <td>${booking.id || 'N/A'}</td>
                    <td><strong>${booking.eventName || 'Unnamed Event'}</strong></td>
                    <td>${booking.clientName || 'Unknown Client'}</td>
                    <td>${formatDateTime(booking.startTime)}</td>
                    <td>${booking.numberOfGuests || 0}</td>
                    <td>Hall ${booking.banquetHallId || 1}</td>
                    <td>
                        <button class="btn btn-success btn-sm" onclick="approveBooking(${booking.id})">✅ Approve</button>
                        <button class="btn btn-danger btn-sm" onclick="rejectBooking(${booking.id})">❌ Reject</button>
                        <button class="btn btn-info btn-sm" onclick="viewBookingDetails(${booking.id})">👁️ View</button>
                    </td>
                `;
                    tableBody.appendChild(row);
                });
            }

            document.getElementById('pendingLoading').style.display = 'none';
            document.getElementById('pendingCount').textContent = bookings.length;
        })
        .catch(error => {
            console.error('Error loading pending bookings:', error);
            document.getElementById('pendingBookingsTable').innerHTML =
                '<tr><td colspan="7" class="text-center text-danger">Error loading pending bookings</td></tr>';
            document.getElementById('pendingLoading').style.display = 'none';
        });
}

// UPDATE: Approve booking - FIXED
function approveBooking(bookingId) {
    if (confirm('Are you sure you want to approve this booking?')) {
        fetch(`${API_BASE}/bookings/${bookingId}/approve`, {
            method: 'PUT'
        })
            .then(response => {
                if (!response.ok) throw new Error('Approval failed');
                return response.json();
            })
            .then(data => {
                alert('✅ Booking approved successfully!');
                loadAllData();
            })
            .catch(error => {
                alert('❌ Error approving booking: ' + error.message);
            });
    }
}

// UPDATE: Reject booking - FIXED
function rejectBooking(bookingId) {
    if (confirm('Are you sure you want to reject this booking?')) {
        fetch(`${API_BASE}/bookings/${bookingId}/reject`, {
            method: 'PUT'
        })
            .then(response => {
                if (!response.ok) throw new Error('Rejection failed');
                return response.json();
            })
            .then(data => {
                alert('✅ Booking rejected successfully!');
                loadAllData();
            })
            .catch(error => {
                alert('❌ Error rejecting booking: ' + error.message);
            });
    }
}

// VIEW: Booking details - FIXED
function viewBookingDetails(bookingId) {
    fetch(`${API_BASE}/bookings/${bookingId}`)
        .then(response => response.json())
        .then(booking => {
            const details = `
📅 Event: ${booking.eventName || 'N/A'}
👤 Client: ${booking.clientName || 'N/A'}
📞 Phone: ${booking.clientPhone || 'N/A'}
📧 Email: ${booking.clientEmail || 'N/A'}
🕐 Date: ${formatDateTime(booking.startTime)}
👥 Guests: ${booking.numberOfGuests || 0}
🏛️ Hall ID: ${booking.banquetHallId || 'N/A'}
📝 Requirements: ${booking.specialRequirements || 'None'}
✅ Status: ${booking.status || 'N/A'}
        `;
            alert(details);
        })
        .catch(error => {
            alert('Error loading booking details');
        });
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

function getStatusBadge(status) {
    const badges = {
        'PENDING': '<span class="badge bg-warning">PENDING</span>',
        'APPROVED': '<span class="badge bg-success">APPROVED</span>',
        'REJECTED': '<span class="badge bg-danger">REJECTED</span>'
    };
    return badges[status] || '<span class="badge bg-secondary">UNKNOWN</span>';
}

function updateStats(bookings) {
    const approved = bookings.filter(b => b.status === 'APPROVED').length;
    document.getElementById('approvedCount').textContent = approved;
}

function testConnection() {
    fetch(`${API_BASE}/bookings`)
        .then(response => {
            if (response.ok) {
                alert('✅ Backend connection successful!');
            } else {
                alert('❌ Backend connection failed');
            }
        })
        .catch(error => {
            alert('❌ Cannot connect to backend');
        });
}
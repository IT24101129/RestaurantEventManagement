// Dashboard JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeDashboard();
    loadDashboardData();
    initializeCharts();
});

function initializeDashboard() {
    // Initialize any dashboard-specific functionality
    console.log('Dashboard initialized');
}

async function loadDashboardData() {
    try {
        // Load today's reservations
        const todayReservations = await fetch('/api/dashboard/today-reservations').then(r => r.json());
        document.getElementById('todayReservations').textContent = todayReservations.count || 0;
        
        // Load pending orders
        const pendingOrders = await fetch('/api/dashboard/pending-orders').then(r => r.json());
        document.getElementById('pendingOrders').textContent = pendingOrders.count || 0;
        
        // Load available tables
        const availableTables = await fetch('/api/dashboard/available-tables').then(r => r.json());
        document.getElementById('availableTables').textContent = availableTables.count || 0;
        
        // Load total revenue
        const totalRevenue = await fetch('/api/dashboard/total-revenue').then(r => r.json());
        document.getElementById('totalRevenue').textContent = `$${totalRevenue.amount || 0}`;
        
        // Load recent reservations
        loadRecentReservations();
        
    } catch (error) {
        console.error('Error loading dashboard data:', error);
    }
}

async function loadRecentReservations() {
    try {
        const response = await fetch('/api/dashboard/recent-reservations');
        const reservations = await response.json();
        
        const tbody = document.querySelector('#recentReservations tbody');
        if (tbody) {
            tbody.innerHTML = reservations.map(reservation => `
                <tr>
                    <td>${reservation.customerName}</td>
                    <td>${formatDateTime(reservation.reservationDateTime)}</td>
                    <td><span class="badge bg-secondary">${reservation.numberOfGuests}</span></td>
                    <td><span class="badge bg-${getStatusColor(reservation.status)}">${reservation.status}</span></td>
                    <td>
                        <a href="/reservations/${reservation.id}" class="btn btn-sm btn-outline-primary">
                            <i class="fas fa-eye"></i>
                        </a>
                    </td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Error loading recent reservations:', error);
    }
}

function getStatusColor(status) {
    const colors = {
        'PENDING': 'warning',
        'CONFIRMED': 'success',
        'CANCELLED': 'danger',
        'COMPLETED': 'primary',
        'NO_SHOW': 'secondary'
    };
    return colors[status] || 'secondary';
}

function formatDateTime(dateTimeString) {
    const date = new Date(dateTimeString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
}

function initializeCharts() {
    // Initialize any charts or visualizations
    // This would integrate with Chart.js or similar library
}

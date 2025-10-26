// Custom JavaScript for Banquet Management System

// Initialize tooltips
document.addEventListener('DOMContentLoaded', function() {
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

// Auto-dismiss alerts
setTimeout(function() {
    var alerts = document.querySelectorAll('.alert');
    alerts.forEach(function(alert) {
        var bsAlert = new bootstrap.Alert(alert);
        bsAlert.close();
    });
}, 5000);

// Order status update confirmation
function confirmStatusUpdate(orderId, newStatus) {
    if (confirm('Are you sure you want to change order #' + orderId + ' status to ' + newStatus + '?')) {
        return true;
    }
    return false;
}
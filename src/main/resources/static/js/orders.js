// Orders Management JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeOrders();
    initializeFilters();
});

function initializeOrders() {
    console.log('Orders page initialized');
}

function initializeFilters() {
    const statusFilter = document.getElementById('statusFilter');
    const typeFilter = document.getElementById('typeFilter');
    const searchInput = document.getElementById('searchInput');
    
    if (statusFilter) {
        statusFilter.addEventListener('change', filterOrders);
    }
    
    if (typeFilter) {
        typeFilter.addEventListener('change', filterOrders);
    }
    
    if (searchInput) {
        searchInput.addEventListener('input', debounce(filterOrders, 300));
    }
}

function filterOrders() {
    const status = document.getElementById('statusFilter').value;
    const type = document.getElementById('typeFilter').value;
    const search = document.getElementById('searchInput').value.toLowerCase();
    
    const rows = document.querySelectorAll('#ordersTable tbody tr');
    
    rows.forEach(row => {
        const statusCell = row.querySelector('td:nth-child(6) .badge');
        const typeCell = row.querySelector('td:nth-child(3) .badge');
        const customerCell = row.querySelector('td:nth-child(2)');
        const orderIdCell = row.querySelector('td:nth-child(1)');
        
        let show = true;
        
        if (status && statusCell && !statusCell.textContent.includes(status)) {
            show = false;
        }
        
        if (type && typeCell && !typeCell.textContent.includes(type)) {
            show = false;
        }
        
        if (search && !customerCell.textContent.toLowerCase().includes(search) && 
            !orderIdCell.textContent.toLowerCase().includes(search)) {
            show = false;
        }
        
        row.style.display = show ? '' : 'none';
    });
}

function exportOrders() {
    console.log('Exporting orders...');
    // Implement export functionality
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

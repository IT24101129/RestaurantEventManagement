// Menu Page JavaScript

let orderItems = [];

document.addEventListener('DOMContentLoaded', function() {
    initializeMenu();
    initializeCategoryFilter();
    initializeOrderModal();
});

function initializeMenu() {
    console.log('Menu page initialized');
}

function initializeCategoryFilter() {
    const categoryButtons = document.querySelectorAll('[data-category]');
    const categories = document.querySelectorAll('.menu-category');
    
    categoryButtons.forEach(button => {
        button.addEventListener('click', function() {
            const category = this.getAttribute('data-category');
            
            // Update button states
            categoryButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');
            
            // Show/hide categories
            categories.forEach(cat => {
                if (category === 'all' || cat.getAttribute('data-category') === category) {
                    cat.classList.add('active');
                } else {
                    cat.classList.remove('active');
                }
            });
        });
    });
}

function initializeOrderModal() {
    const orderModal = document.getElementById('orderModal');
    if (orderModal) {
        // Initialize modal functionality
    }
}

function addToOrder(itemName, price, prepTime) {
    const existingItem = orderItems.find(item => item.name === itemName);
    
    if (existingItem) {
        existingItem.quantity += 1;
    } else {
        orderItems.push({
            name: itemName,
            price: parseFloat(price.replace('$', '')),
            prepTime: prepTime,
            quantity: 1
        });
    }
    
    updateOrderModal();
    showOrderModal();
}

function updateOrderModal() {
    const orderItemsContainer = document.getElementById('orderItems');
    const orderTotal = document.getElementById('orderTotal');
    
    if (orderItemsContainer) {
        orderItemsContainer.innerHTML = orderItems.map(item => `
            <div class="d-flex justify-content-between align-items-center mb-2">
                <div>
                    <strong>${item.name}</strong>
                    <small class="text-muted d-block">$${item.price.toFixed(2)} each</small>
                </div>
                <div class="d-flex align-items-center">
                    <button class="btn btn-sm btn-outline-secondary me-2" onclick="updateQuantity('${item.name}', -1)">-</button>
                    <span class="me-2">${item.quantity}</span>
                    <button class="btn btn-sm btn-outline-secondary me-2" onclick="updateQuantity('${item.name}', 1)">+</button>
                    <button class="btn btn-sm btn-outline-danger" onclick="removeItem('${item.name}')">
                        <i class="fas fa-trash"></i>
                    </button>
                </div>
            </div>
        `).join('');
    }
    
    if (orderTotal) {
        const total = orderItems.reduce((sum, item) => sum + (item.price * item.quantity), 0);
        orderTotal.textContent = `$${total.toFixed(2)}`;
    }
}

function updateQuantity(itemName, change) {
    const item = orderItems.find(item => item.name === itemName);
    if (item) {
        item.quantity += change;
        if (item.quantity <= 0) {
            removeItem(itemName);
        } else {
            updateOrderModal();
        }
    }
}

function removeItem(itemName) {
    orderItems = orderItems.filter(item => item.name !== itemName);
    updateOrderModal();
    
    if (orderItems.length === 0) {
        hideOrderModal();
    }
}

function showOrderModal() {
    const modal = new bootstrap.Modal(document.getElementById('orderModal'));
    modal.show();
}

function hideOrderModal() {
    const modal = bootstrap.Modal.getInstance(document.getElementById('orderModal'));
    if (modal) {
        modal.hide();
    }
}

function proceedToCheckout() {
    if (orderItems.length === 0) {
        alert('Your order is empty!');
        return;
    }
    
    // Redirect to checkout or order page
    window.location.href = '/orders/new?items=' + encodeURIComponent(JSON.stringify(orderItems));
}

// Add click handlers to all "Add to Order" buttons
document.addEventListener('click', function(e) {
    if (e.target.closest('.menu-item-actions .btn')) {
        const button = e.target.closest('.menu-item-actions .btn');
        const card = button.closest('.menu-item-card');
        
        if (card) {
            const name = card.querySelector('.menu-item-name').textContent;
            const price = card.querySelector('.price').textContent;
            const prepTime = card.querySelector('.prep-time').textContent;
            
            addToOrder(name, price, prepTime);
        }
    }
});

// Login Page JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeLoginForm();
    initializePasswordToggle();
    initializeDemoAccounts();
});

function initializeLoginForm() {
    const form = document.querySelector('form[th\\:action="@{/login}"]');
    if (form) {
        form.addEventListener('submit', function(e) {
            const submitBtn = form.querySelector('button[type="submit"]');
            showLoading(submitBtn);
        });
    }
}

function initializePasswordToggle() {
    const toggleBtn = document.getElementById('togglePassword');
    const passwordInput = document.getElementById('password');
    
    if (toggleBtn && passwordInput) {
        toggleBtn.addEventListener('click', function() {
            const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
            passwordInput.setAttribute('type', type);
            
            const icon = toggleBtn.querySelector('i');
            icon.classList.toggle('fa-eye');
            icon.classList.toggle('fa-eye-slash');
        });
    }
}

function initializeDemoAccounts() {
    const demoAccounts = document.querySelectorAll('.demo-account');
    const emailInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    
    demoAccounts.forEach(account => {
        account.addEventListener('click', function() {
            const email = this.querySelector('small.fw-bold').textContent;
            if (emailInput) {
                emailInput.value = email;
            }
            if (passwordInput) {
                passwordInput.value = 'demo123';
            }
        });
    });
}

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

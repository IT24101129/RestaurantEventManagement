// Registration Page JavaScript

document.addEventListener('DOMContentLoaded', function() {
    initializeRegistrationForm();
    initializePasswordToggle();
    initializePasswordValidation();
    initializeFormValidation();
});

function initializeRegistrationForm() {
    const form = document.querySelector('form[th\\:action="@{/register}"]');
    if (form) {
        form.addEventListener('submit', function(e) {
            if (!validateForm(form)) {
                e.preventDefault();
                return false;
            }
            
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

function initializePasswordValidation() {
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const passwordMatchDiv = document.getElementById('passwordMatch');
    
    if (passwordInput && confirmPasswordInput && passwordMatchDiv) {
        confirmPasswordInput.addEventListener('input', function() {
            validatePasswordMatch();
        });
        
        passwordInput.addEventListener('input', function() {
            validatePasswordStrength();
            validatePasswordMatch();
        });
    }
}

function validatePasswordStrength() {
    const passwordInput = document.getElementById('password');
    const password = passwordInput.value;
    
    let strength = 0;
    if (password.length >= 8) strength++;
    if (/[a-z]/.test(password)) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^A-Za-z0-9]/.test(password)) strength++;
    
    const strengthText = ['Very Weak', 'Weak', 'Fair', 'Good', 'Strong'][strength - 1] || 'Very Weak';
    const strengthClass = ['danger', 'danger', 'warning', 'info', 'success'][strength - 1] || 'danger';
    
    // Update password strength indicator if it exists
    const strengthIndicator = document.querySelector('.password-strength');
    if (strengthIndicator) {
        const bar = strengthIndicator.querySelector('.password-strength-bar');
        if (bar) {
            bar.className = `password-strength-bar password-strength-${strengthClass}`;
            bar.style.width = `${(strength / 5) * 100}%`;
        }
    }
}

function validatePasswordMatch() {
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const passwordMatchDiv = document.getElementById('passwordMatch');
    
    if (passwordInput && confirmPasswordInput && passwordMatchDiv) {
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;
        
        if (confirmPassword.length > 0) {
            if (password === confirmPassword) {
                passwordMatchDiv.innerHTML = '<i class="fas fa-check text-success me-1"></i>Passwords match';
                passwordMatchDiv.className = 'form-text text-success';
                confirmPasswordInput.classList.remove('is-invalid');
                confirmPasswordInput.classList.add('is-valid');
            } else {
                passwordMatchDiv.innerHTML = '<i class="fas fa-times text-danger me-1"></i>Passwords do not match';
                passwordMatchDiv.className = 'form-text text-danger';
                confirmPasswordInput.classList.remove('is-valid');
                confirmPasswordInput.classList.add('is-invalid');
            }
        } else {
            passwordMatchDiv.innerHTML = '';
            confirmPasswordInput.classList.remove('is-valid', 'is-invalid');
        }
    }
}

function validateForm(form) {
    const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!input.value.trim()) {
            input.classList.add('is-invalid');
            isValid = false;
        } else {
            input.classList.remove('is-invalid');
            input.classList.add('is-valid');
        }
    });
    
    // Validate password match
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    
    if (passwordInput && confirmPasswordInput) {
        if (passwordInput.value !== confirmPasswordInput.value) {
            confirmPasswordInput.classList.add('is-invalid');
            isValid = false;
        }
    }
    
    // Validate terms checkbox
    const termsCheckbox = document.getElementById('terms');
    if (termsCheckbox && !termsCheckbox.checked) {
        termsCheckbox.classList.add('is-invalid');
        isValid = false;
    }
    
    return isValid;
}

function initializeFormValidation() {
    const form = document.querySelector('form[th\\:action="@{/register}"]');
    if (form) {
        const inputs = form.querySelectorAll('input, select, textarea');
        
        inputs.forEach(input => {
            input.addEventListener('blur', function() {
                validateField(this);
            });
            
            input.addEventListener('input', function() {
                if (this.classList.contains('is-invalid')) {
                    validateField(this);
                }
            });
        });
    }
}

function validateField(field) {
    const value = field.value.trim();
    const isRequired = field.hasAttribute('required');
    
    if (isRequired && !value) {
        field.classList.add('is-invalid');
        field.classList.remove('is-valid');
        return false;
    }
    
    // Email validation
    if (field.type === 'email' && value) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(value)) {
            field.classList.add('is-invalid');
            field.classList.remove('is-valid');
            return false;
        }
    }
    
    // Phone validation
    if (field.type === 'tel' && value) {
        const phoneRegex = /^[0-9]{10}$/;
        if (!phoneRegex.test(value)) {
            field.classList.add('is-invalid');
            field.classList.remove('is-valid');
            return false;
        }
    }
    
    field.classList.remove('is-invalid');
    field.classList.add('is-valid');
    return true;
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

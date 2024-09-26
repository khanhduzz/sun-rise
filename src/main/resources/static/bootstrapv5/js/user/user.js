document.addEventListener("DOMContentLoaded", function () {
    // Elements
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const passwordError = document.getElementById('passwordError');
    const createUserForm = document.getElementById('createUserForm');
    const editUserForm = document.getElementById('editUserForm');
    const emailInput = document.getElementById('email');
    const phoneInput = document.getElementById('phone');
    const emailError = document.getElementById('emailError');
    const phoneError = document.getElementById('phoneError');
    const submitBtn = document.getElementById('submitBtn');
    const savePasswordBtn = document.getElementById('savePasswordBtn');
    const inputs = document.querySelectorAll('input, textarea, select');
    const requiredInputs = document.querySelectorAll('input[required]');
    let initialData = {};

    // Lưu giá trị ban đầu của các trường nhập liệu
    document.querySelectorAll('input, select').forEach(input => {
        initialData[input.id] = input.value;
    });

    // Functions
    function checkPasswordMatch() {
        if (password && confirmPassword) {
            if (password.value !== confirmPassword.value) {
                confirmPassword.classList.add('is-invalid');
//                passwordError.style.display = 'block';
                passwordError.classList.remove('hidden');
                return false;
            } else {
                confirmPassword.classList.remove('is-invalid');
//                passwordError.style.display = 'none';
                passwordError.classList.add('hidden');
                return true;
            }
        }
    }

    function checkValidation(input, errorElement) {
        if (!input.checkValidity()) {
            input.classList.add('is-invalid');
            errorElement.style.display = 'block';
        } else {
            input.classList.remove('is-invalid');
            errorElement.style.display = 'none';
        }
    }

    function checkIfChanged() {
        let isChanged = false;
        document.querySelectorAll('input, select').forEach(input => {
            if (input.value !== initialData[input.id]) {
                isChanged = true;
            }
        });
        if (submitBtn) {
            submitBtn.disabled = !isChanged;
        }
    }

    function togglePasswordVisibility(fieldId, eyeButton) {
        const field = document.getElementById(fieldId);
        field.type = (field.type === "password") ? "text" : "password";
        eyeButton.innerHTML = (field.type === "password")
            ? '<i class="fas fa-eye"></i>'
            : '<i class="fas fa-eye-slash"></i>';
    }

    // Event listeners
    if (createUserForm) {
        createUserForm.addEventListener('reset', () => {
            inputs.forEach(input => input.classList.remove('valid-input', 'invalid-input'));
        });

        createUserForm.addEventListener('submit', function (event) {
            if (!checkPasswordMatch()) {
                event.preventDefault();
            }
        });

        confirmPassword.addEventListener('input', checkPasswordMatch);
    }

    if (editUserForm) {
        editUserForm.addEventListener('reset', () => {
            document.querySelectorAll('input, select').forEach(input => {
                input.value = initialData[input.id];
                input.classList.remove('is-invalid');
            });
            submitBtn.setAttribute('disabled', true);
        });

        editUserForm.addEventListener('submit', function (event) {
            let isChanged = false;
            document.querySelectorAll('input, select').forEach(input => {
                if (input.value !== initialData[input.id]) {
                    isChanged = true;
                }
            });
            if (!isChanged) {
                event.preventDefault();
                alert('Không có gì thay đổi!');
            }
        });
    }

    if (savePasswordBtn) {
        savePasswordBtn.addEventListener('click', function () {
            const newPassword = document.getElementById('newPassword').value;
            const confirmPassword = document.getElementById('confirmNewPassword').value;

            if (newPassword !== confirmPassword) {
                document.getElementById('newPasswordError').style.display = 'block';
                return;
            }
            document.getElementById('changePasswordForm').submit();
        });
    }

    // Common validation logic for email and phone
    [emailInput, phoneInput].forEach(input => {
        if (input) {
            input.addEventListener('input', () => {
                checkValidation(input, input.id === 'email' ? emailError : phoneError);
                checkIfChanged();
            });
        }
    });

    // Required inputs validation
    requiredInputs.forEach(input => {
        input.addEventListener('invalid', function () {
            if (this.type === 'email') {
                if (this.value === "") {
                    this.setCustomValidity('Xin vui lòng điền đầy đủ thông tin');
                } else if (!this.value.includes('@')) {
                    this.setCustomValidity('Vui lòng bao gồm ký tự \'@\' trong địa chỉ email.');
                } else if (!this.value.includes('@.')) {
                    this.setCustomValidity('Vui lòng hoàn thiện email theo định dạng: example@example.com');
                } else if (!this.value.includes('.com')) {
                    this.setCustomValidity('Vui lòng hoàn thiện email theo định dạng: example@example.com');
                }
            } else if (this.id === 'phone') {
                const phonePattern = /^\d+$/;
                if (this.value === "") {
                    this.setCustomValidity('Xin vui lòng điền đầy đủ thông tin');
                } else if (!phonePattern.test(this.value)) {
                    this.setCustomValidity('Số điện thoại chỉ được chứa các chữ số.');
                } else if (this.value.length > 10) {
                    this.setCustomValidity('Số điện thoại không được quá 10 chữ số.');
                } else {
                    this.setCustomValidity('');
                }
            } else if (this.value === "") {
                this.setCustomValidity('Xin vui lòng điền đầy đủ thông tin');
            }
        });

        input.addEventListener('input', function () {
            this.setCustomValidity('');
        });
    });

    // DOM loaded handlers
    let message = new URLSearchParams(window.location.search).get('message');
    if (message === 'SuccessPassword') {
        $('#successPasswordModal').modal('show');
    }
    if (message === 'SuccessInfo') {
        $('#successChangeInfoModal').modal('show');
    }
    if ($("#popup-failed-change-password").length) {
        $('#popup-failed-change-password').modal('show');
    }
});

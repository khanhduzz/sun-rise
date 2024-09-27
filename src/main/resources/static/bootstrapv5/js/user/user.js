document.addEventListener("DOMContentLoaded", function () {

    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const passwordError = document.getElementById('passwordError');
    const createUserForm = document.getElementById('createUserForm');
    const editUserForm = document.getElementById('editUserForm');
    const emailInput = document.getElementById('email');
    const phoneInput = document.getElementById('phone');
    const submitBtn = document.getElementById('submitBtn');
    const savePasswordBtn = document.getElementById('savePasswordBtn');
    const inputs = document.querySelectorAll('input, textarea, select');
    const requiredInputs = document.querySelectorAll('input[required]');
    let initialData = {};

    document.querySelectorAll('input, select').forEach(input => {
        initialData[input.id] = input.value;
    });

    function checkPasswordMatch() {
        if (password && confirmPassword) {
            if (password.value !== confirmPassword.value) {
                confirmPassword.classList.add('is-invalid');
                passwordError.classList.remove('hidden');
                return false;
            } else {
                confirmPassword.classList.remove('is-invalid');
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

            document.querySelectorAll('.invalid-feedback, .text-danger').forEach(error => {
                error.style.display = 'none';
            });
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

    [emailInput, phoneInput].forEach(input => {
        if (input) {
            input.addEventListener('input', () => {
                checkValidation(input, input.id === 'email' ? emailError : phoneError);
                checkIfChanged();
            });
        }
    });

    requiredInputs.forEach(input => {
        input.addEventListener('invalid', function () {
            // Phần xử lý cho email (type="email")
            if (this.id === 'email') {
                if (this.value === "") {
                    this.setCustomValidity('Xin vui lòng nhập địa chỉ email.');
                } else if (!this.value.includes('@')) {
                    this.setCustomValidity('Địa chỉ email phải bao gồm ký tự \'@\'.');
                } else {
                    this.setCustomValidity('');
                }
            }

            // Phần xử lý cho số điện thoại (id="phone")
            else if (this.id === 'phone') {
                const phonePattern = /^\d+$/;
                if (this.value === "") {
                    this.setCustomValidity('Xin vui lòng nhập số điện thoại.');
                } else if (!phonePattern.test(this.value)) {
                    this.setCustomValidity('Số điện thoại chỉ được chứa các chữ số.');
                } else if (this.value.length > 10) {
                    this.setCustomValidity('Số điện thoại không được quá 10 chữ số.');
                } else {
                    this.setCustomValidity('');
                }
            }

            // Phần xử lý cho các trường khác
            else if (this.required && this.value === "") {
                this.setCustomValidity('Xin vui lòng điền đầy đủ thông tin.');
            } else {
                this.setCustomValidity('');
            }
        });

        input.addEventListener('input', function () {
            this.setCustomValidity('');
        });
    });


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

document.addEventListener("DOMContentLoaded", function() {
    var roleInput = document.getElementById('role');
    var roleValue = roleInput.value;

    changeVietnamese(roleValue);
});

document.addEventListener("DOMContentLoaded", function() {
    var roleInput = document.getElementById('role');
    var roleValue = roleInput.value;

    var vietnameseRole = convertRoleToVietnamese(roleValue);

    roleInput.value = vietnameseRole;
});

function convertRoleToVietnamese(role) {
    if (role === 'USER') {
        return 'Người dùng';
    } else if (role === 'ADMIN') {
        return 'Quản trị viên';
    } else {
        return 'Vai trò không xác định';
    }
}

document.addEventListener('DOMContentLoaded', function() {
    changeVietnamese();
});

function togglePasswordVisibility(fieldId, eyeButton) {
    var field = document.getElementById(fieldId);
    field.type = (field.type === "password") ? "text" : "password";
    eyeButton.innerHTML = (field.type === "password")
        ? '<i class="fas fa-eye"></i>'
        : '<i class="fas fa-eye-slash"></i>';
}
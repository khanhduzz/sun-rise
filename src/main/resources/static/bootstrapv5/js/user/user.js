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


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
                passwordError.style.display = 'block';
                return false;
            } else {
                confirmPassword.classList.remove('is-invalid');
                passwordError.style.display = 'none';
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
            // Phần xử lý cho email (type="email")
            if (this.id === 'email') {
                if (this.value === "") {
                    this.setCustomValidity('Xin vui lòng nhập địa chỉ email.');
                } else if (!this.value.includes('@')) {
                    this.setCustomValidity('Địa chỉ email phải bao gồm ký tự \'@\'.');
                } else if (!this.value.match(/^[^@]+@[^@]+\.[^@]+$/)) {
                    this.setCustomValidity('Vui lòng hoàn thiện địa chỉ email theo định dạng: example@example.com');
                } else {
                    this.setCustomValidity(''); // Xóa thông báo lỗi khi giá trị hợp lệ
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
                    this.setCustomValidity(''); // Xóa thông báo lỗi khi giá trị hợp lệ
                }
            }

            // Phần xử lý cho các trường khác
            else if (this.required && this.value === "") {
                this.setCustomValidity('Xin vui lòng điền đầy đủ thông tin.');
            } else {
                this.setCustomValidity(''); // Xóa thông báo lỗi khi giá trị hợp lệ
            }
        });

        input.addEventListener('input', function () {
            this.setCustomValidity(''); // Xóa thông báo khi người dùng bắt đầu nhập liệu
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

function changeVietnamese() {
    // Lấy phần tử với id "changeVietnamese"
    var element = document.getElementById('changeVietnamese');

    // Kiểm tra nếu phần tử tồn tại
    if (element) {
        var role = 'ROLE_ADMIN'; // Hoặc lấy giá trị role từ đâu đó
        var displayRole = '';

        // Kiểm tra giá trị role
        if (role === 'ROLE_ADMIN') {
            displayRole = 'Quản trị viên';
        } else if (role === 'ROLE_USER') {
            displayRole = 'Người dùng';
        }

        // Cập nhật nội dung của phần tử với id "changeVietnamese"
        element.textContent = displayRole;
    }
}

// Gọi hàm sau khi DOM đã tải xong
document.addEventListener('DOMContentLoaded', function() {
    changeVietnamese(); // Gọi hàm khi trang đã sẵn sàng
});
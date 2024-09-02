const MESSAGE = {
    name: ["Không được để trống", "Không chứa kí tự đặc biệt", "Viết hoa chữ cái đầu"],
    email: ["Không được để trống", "Đúng định dạng(vd: name@example.com)"],
    phone: ["Không được để trống", "Có 10 chữ số", "Đúng định dạng(vd: 0123456789)"],
    password: ["Không được để trống", "Có ít nhất 1 kí tự đặc biệt", "Có ít nhất 1 kí tự in hoa"],
    rePassword: ["Không được để trống", "Trùng với mật khẩu"]
};

let validRegister = {};

// Hàm chung cho việc xác thực các trường dữ liệu
function validateField(value, validations, messages, elementId) {
    validRegister[elementId] = true;
    let message = `<ul class="custom-list"></ul>`;

    validations.forEach(([test, errorIndex]) => {
        if (!test(value)) {
            message += `<li class="inValid">${messages[errorIndex]}</li>`;
            validRegister[elementId] = false;
        } else {
            message += `<li class="valid">${messages[errorIndex]}</li>`;
        }
    });

    document.getElementById(elementId).innerHTML = message;
    updateBorderColor(elementId);
}

function updateBorderColor(elementId) {
    const field = document.getElementById(elementId.replace('message', ''));
    field.style.borderColor = validRegister[elementId] ? "green" : "red";
}

// Hàm xác thực tên
function validName() {
    const value = document.activeElement.value.trim();
    const validations = [
        [v => v !== "", 0],
        [v => !/[!@#\$%\^\&*\)\(+=._-]+/g.test(v), 1],
        [v => /^[A-Z][a-z]*( [A-Z][a-z]*)*$/.test(v.replace(/[@#!]/g, '')), 2]
    ];
    const elementId = document.activeElement.id === "lastname" ? "messageL" : "messageF";
    validateField(value, validations, MESSAGE.name, elementId);
}

// Hàm xác thực email
function validEmail() {
    const value = document.activeElement.value.trim();
    const validations = [
        [v => v !== "", 0],
        [v => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(v), 1]
    ];
    validateField(value, validations, MESSAGE.email, "messageE");
}

// Hàm xác thực số điện thoại
function validPhone() {
    const value = document.activeElement.value.trim();
    const validations = [
        [v => v !== "", 0],
        [v => v.length === 10, 1],
        [v => /^0\d{9}$/.test(v), 2]
    ];
    validateField(value, validations, MESSAGE.phone, "messageP");
}

// Hàm xác thực mật khẩu
function validPassword() {
    const value = document.activeElement.value.trim();
    const validations = [
        [v => v !== "", 0],
        [v => /[!@#\$%\^\&*\)\(+=._-]+/.test(v), 1],
        [v => /[A-Z]/.test(v), 2]
    ];
    validateField(value, validations, MESSAGE.password, "messagePW");
}

// Hàm xác thực xác nhận mật khẩu
function validRePassword() {
    const value = document.activeElement.value.trim();
    const password = document.getElementById("passwordRegister").value.trim();
    const validations = [
        [v => v !== "", 0],
        [v => v === password, 1]
    ];
    validateField(value, validations, MESSAGE.rePassword, "messageR");
}

// Hàm đóng các phần tử collapse
function closeCollapse() {
    document.querySelectorAll('.collapse').forEach(collapseElement => {
        new bootstrap.Collapse(collapseElement, { toggle: false }).hide();
    });
}

// Thêm sự kiện cho các input để đóng collapse khi có focus
document.querySelectorAll('input[data-bs-target]').forEach(input => {
    input.addEventListener('focus', closeCollapse);
});

// Hàm kiểm tra thông tin đã điền đầy đủ
function checkFillOut() {
    const popup = new bootstrap.Modal(document.getElementById('popup'));
    if (!Object.values(validRegister).every(value => value === true)) {
        document.activeElement.type = "button";
        popup.show();
        document.getElementById("erroeMsg").textContent = "Vui lòng điền lại thông tin!";
    } else {
        document.activeElement.type = "submit";
        popup.hide();
    }
}

// Hàm kiểm tra thông tin đăng nhập
function checkLogin() {
    const popup = new bootstrap.Modal(document.getElementById('popup'));
    const username = document.getElementById("username");
    const password = document.getElementById("passwordLogin");
    if (!username.value.trim() || !password.value.trim()) {
        document.activeElement.type = "button";
        document.getElementById("erroeMsg").textContent = "Vui lòng điền lại thông tin!";
        popup.show();
    } else {
        document.activeElement.type = "submit";
        popup.hide();
    }
}

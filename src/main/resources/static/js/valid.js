const MESSAGE = {
    name: ["Không được để trống", "Không chứa kí tự đặc biệt", "Viết hoa chữ cái đầu "],
    email: ["Không được để trống", "Đúng định dạng(vd: name@example.com)"],
    phone: ["Không được để trống", "Có 10 chữ số", "Đúng định dạng(vd: 0123456789)"],
    password: ["Không được để trống", "Có ít nhất 1 kí tự đặc biệt", "Có ít nhất 1 kí tự in hoa"],
    rePassword: ["Không được để trống", "Trùng với mật khẩu"]
}

function printMessage(message) {
    document.getElementById("message").innerHTML = message;
}

//register
const firstname = document.getElementById("firstname");
const lastname = document.getElementById("lastname");
const email = document.getElementById("email");
const phone = document.getElementById("phone");
const passwordRegister = document.getElementById("passwordRegister");
const rePasswordRegister = document.getElementById("rePasswordRegister");

const buttonRegister = document.getElementById("buttonRegister");


var validRegister = true;


function validFirstname() {
    let message = `<ul class="custom-list">Trường này yêu cầu: </ul>`;
    let value = firstname.value;
    let listMsg = MESSAGE.name;

    if(value == null || value === "" || value.trim() === "") {
        message+= `<li class="inValid">${listMsg[0]}</li>`;
        validRegister = false;
    } else {
        message+= `<li class="valid">${listMsg[0]}</li>`;
        validRegister = true;
    }

    let specialCharsPattern = /[!@#\$%\^\&*\)\(+=._-]+/g;
    if(specialCharsPattern.test(value)) {
        message+= `<li class="inValid">${listMsg[1]}</li>`;
        validRegister = false;
    } else {
        message+= `<li class="valid">${listMsg[1]}</li>`;
        validRegister = true;
    }

    let upperCasePattern = /^[A-Z][a-z]*( [A-Z][a-z]*)*$/;
    let cleanedString = value.replace(/[@#!]/g, '');
    if(upperCasePattern.test(cleanedString)) {
        message+= `<li class="valid">${listMsg[2]}</li>`;
        validRegister = true;
    } else{
        message+= `<li class="inValid">${listMsg[2]}</li>`;
        validRegister = false;
    }
    printMessage(message);
}

function validLastname() {
    let message = `<ul class="custom-list">Trường này yêu cầu: </ul>`;
    let value = lastname.value;
    let listMsg = MESSAGE.name;

    if(value == null || value === "" || value.trim() === "") {
        message+= `<li class="inValid">${listMsg[0]}</li>`;
        validRegister = false;
    } else {
        message+= `<li class="valid">${listMsg[0]}</li>`;
        validRegister = true;
    }

    let specialCharsPattern = /[!@#\$%\^\&*\)\(+=._-]+/g;
    if(specialCharsPattern.test(value)) {
        message+= `<li class="inValid">${listMsg[1]}</li>`;
        validRegister = false;
    } else {
        message+= `<li class="valid">${listMsg[1]}</li>`;
        validRegister = true;
    }

    let upperCasePattern = /^[A-Z][a-z]*( [A-Z][a-z]*)*$/;
    let cleanedString = value.replace(/[@#!]/g, '');
    if(upperCasePattern.test(cleanedString)) {
        message+= `<li class="valid">${listMsg[2]}</li>`;
        validRegister = true;
    } else{
        message+= `<li class="inValid">${listMsg[2]}</li>`;
        validRegister = false;
    }
    printMessage(message);
}

function validEmail() {
    let message = `<ul class="custom-list">Trường này yêu cầu: </ul>`;
    let value = email.value;
    let listMsg = MESSAGE.email;

    if(value == null || value === "" || value.trim() === "") {
        message+= `<li class="inValid">${listMsg[0]}</li>`;
        validRegister = false;
    } else {
        message+= `<li class="valid">${listMsg[0]}</li>`;
        validRegister = true;
    }

    let emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if(emailPattern.test(value)) {
        message+= `<li class="valid">${listMsg[1]}</li>`;
        validRegister = true;
    } else {
        message+= `<li class="inValid">${listMsg[1]}</li>`;
        validRegister = false;
    }
    printMessage(message);
}

function validPhone() {
    let message = `<ul class="custom-list">Trường này yêu cầu: </ul>`;
    let value = phone.value;
    let listMsg = MESSAGE.phone;

    if(value == null || value === "" || value.trim() === "") {
        message+= `<li class="inValid">${listMsg[0]}</li>`;
        validRegister = false;
    } else {
        message+= `<li class="valid">${listMsg[0]}</li>`;
        validRegister = true;
    }

    if(value.length !== 10) {
        message+= `<li class="inValid">${listMsg[1]}</li>`;
        validRegister = false;
    } else {
        message+= `<li class="valid">${listMsg[1]}</li>`;
        validRegister = true;
    }

    let phonePattern = /^0\d{9}$/;
    if(phonePattern.test(value)) {
        message+= `<li class="valid">${listMsg[2]}</li>`;
        validRegister = true;
    } else {
        message+= `<li class="inValid">${listMsg[2]}</li>`;
        validRegister = false;
    }
    printMessage(message);
}

function validPassword() {
    let message = `<ul class="custom-list">Trường này yêu cầu: </ul>`;
    let value = passwordRegister.value;
    let listMsg = MESSAGE.password;

    if(value == null || value === "" || value.trim() === "") {
        message+= `<li class="inValid">${listMsg[0]}</li>`;
        validRegister = false;
    } else {
        message+= `<li class="valid">${listMsg[0]}</li>`;
        validRegister = true;
    }

    let specialCharsPattern = /[!@#\$%\^\&*\)\(+=._-]+/g;
    if(specialCharsPattern.test(value)) {
        message+= `<li class="valid">${listMsg[1]}</li>`;
        validRegister = true;
    } else {
        message+= `<li class="inValid">${listMsg[1]}</li>`;
        validRegister = false;
    }

    let upperCasePattern = /[A-Z]/;
    if(upperCasePattern.test(value)) {
        message+= `<li class="valid">${listMsg[2]}</li>`;
        validRegister = true;
    } else {
        message+= `<li class="inValid">${listMsg[2]}</li>`;
        validRegister = false;
    }
    printMessage(message);
}

function validRePassword() {
    let message = `<ul class="custom-list">Trường này yêu cầu: </ul>`;
    let value = rePasswordRegister.value;
    let listMsg = MESSAGE.rePassword;

    if(value == null || value === "" || value.trim() === "") {
        message+= `<li class="inValid">${listMsg[0]}</li>`;
        validRegister = false;
    } else {
        message+= `<li class="valid">${listMsg[0]}</li>`;
        validRegister = true;
    }

    let pw = passwordRegister.value;
    if(value === pw) {
        message+= `<li class="valid">${listMsg[1]}</li>`;
        validRegister = true;
    } else {
        message+= `<li class="inValid">${listMsg[1]}</li>`;
        validRegister = false;
    }
    printMessage(message);
}


firstname.addEventListener("click", validFirstname);
lastname.addEventListener("click", validLastname);
email.addEventListener("click", validEmail);
phone.addEventListener("click", validPhone);
passwordRegister.addEventListener("click", validPassword);
rePasswordRegister.addEventListener("click", validRePassword);

firstname.addEventListener("input", validFirstname);
lastname.addEventListener("input", validLastname);
email.addEventListener("input", validEmail);
phone.addEventListener("input", validPhone);
passwordRegister.addEventListener("input", validPassword);
rePasswordRegister.addEventListener("input", validRePassword);








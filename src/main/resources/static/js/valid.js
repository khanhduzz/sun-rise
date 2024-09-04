const MESSAGE = {
    name: ["Không được để trống", "Không chứa kí tự đặc biệt", "Viết hoa chữ cái đầu"],
    email: ["Không được để trống", "Đúng định dạng: name@domain"],
    phone: ["Không được để trống", "Đúng định dạng: 0xxxxxxxxx"],
    password: ["Không được để trống"],
    rePassword: ["Không được để trống","Trùng với mật khẩu đã nhập"]
}

const firstname = document.getElementById("firstname");
const lastname = document.getElementById("lastname");
const email = document.getElementById("email");
const phone = document.getElementById("phone");
const passwordRegister = document.getElementById("password-register");
const rePasswordRegister = document.getElementById("re-password-register");
const buttonSubmitRegister = document.getElementById("submit-button-register");
const username = document.getElementById("username");
const passwordLogin = document.getElementById("password-login");
const buttonSubmitLogin = document.getElementById("submit-button-login");

firstname.addEventListener("focus", () => changeStyle(firstname, validname(firstname.value)));
lastname.addEventListener("focus", () => changeStyle(lastname, validname(lastname.value)));
email.addEventListener("focus", () => changeStyle(email, validEmail(email.value)));
phone.addEventListener("focus", () => changeStyle(phone, validPhone(phone.value)));
passwordRegister.addEventListener("focus", () => changeStyle(passwordRegister, validPassword(passwordRegister.value)));
rePasswordRegister.addEventListener("focus", () => changeStyle(rePasswordRegister, validRePassword(passwordRegister.value, rePasswordRegister.value)));

username.addEventListener("focus", () => changeStyle(username, validEmail(username.value) || validPhone(username.value)));
passwordLogin.addEventListener("focus", () => changeStyle(passwordLogin, validPassword(passwordLogin.value)));

firstname.addEventListener("input", () => changeStyle(firstname, validname(firstname.value)));
lastname.addEventListener("input", () => changeStyle(lastname, validname(lastname.value)));
email.addEventListener("input", () => changeStyle(email, validEmail(email.value)));
phone.addEventListener("input", () => changeStyle(phone, validPhone(phone.value)));
passwordRegister.addEventListener("input", () => changeStyle(passwordRegister, validPassword(passwordRegister.value)));
rePasswordRegister.addEventListener("input", () => changeStyle(rePasswordRegister, validRePassword(passwordRegister.value, rePasswordRegister.value)));

firstname.addEventListener("blur", () => resetStyle(firstname));
lastname.addEventListener("blur", () => resetStyle(lastname));
email.addEventListener("blur", () => resetStyle(email));
phone.addEventListener("blur", () => resetStyle(phone));
passwordRegister.addEventListener("blur", () => resetStyle(passwordRegister));
rePasswordRegister.addEventListener("blur", () => resetStyle(rePasswordRegister));

username.addEventListener("blur", () => resetStyle(username));
passwordLogin.addEventListener("blur", () => resetStyle(passwordLogin));

username.addEventListener("input", () => changeStyle(username, validEmail(username.value) || validPhone(username.value)));
passwordLogin.addEventListener("input", () => changeStyle(passwordLogin, validPassword(passwordLogin.value)));


buttonSubmitRegister.addEventListener("mouseover", () => changeTypeSubmit(buttonSubmitRegister,
    validname(firstname.value) && validname(lastname.value) && validEmail(email.value)
    && validPhone(phone.value) && validPassword(passwordRegister.value)
    && validRePassword(passwordRegister.value, rePasswordRegister.value)));

buttonSubmitLogin.addEventListener("mouseover", () => changeTypeSubmit(buttonSubmitLogin,
    (validEmail(username.value) || validPhone(username.value)) && validPassword(passwordLogin.value)));

function changeTypeSubmit(element, isValid) {
    if(isValid) {
        element.type = "submit";
    } else {
        element.type = "button";
        element.setAttribute('data-bs-toggle', 'modal');
        element.setAttribute('data-bs-target', '#popup');
    }
}

function resetStyle(element) {
    element.style.boxShadow = "";
}

function changeStyle(element, isValid) {
    if (isValid) {
        element.style.border = "1px solid #41cd46";
        element.style.boxShadow = "0 0 10px #4CAF50";
    } else {
        element.style.border = "1px solid red";
        element.style.boxShadow = "0 0 10px red";
    }
}

function containsSpecialCharacter(input) {
    const specialCharsRegex = /[!@#$%^&*)(+=._-]+/g;
    return specialCharsRegex.test(input);
}

function isVietnameseCapitalized(input) {
    const capitalizedVietnameseRegex = /^[A-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠƯÇàáâãèéêìíòóôõùúăđĩũơưçÝỲỴỶỸýỳỵỷỹ][a-zàáâãèéêìíòóôõùúăđĩũơưç]+$/u;
    return capitalizedVietnameseRegex.test(input);
}

function checkBlank(input) {
    return input.trim() !== "";
}

function validname(input) {
    if (!checkBlank(input) || containsSpecialCharacter(input)) return false;
    return input.split(" ").every(word => isVietnameseCapitalized(word));
}

function validEmail(input) {
    if (!checkBlank(input)) return false;
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(input);
}

function validPhone(input) {
    if (!checkBlank(input)) return false;
    const phoneRegex = /^0\d{9}$/;
    return phoneRegex.test(input);
}

function validPassword(input) {
    return input !== "";
}

function validRePassword(password, rePassword) {
    return rePassword !== "" && password === rePassword;
}

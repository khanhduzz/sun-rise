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
const inputCodeLogin = document.getElementById("input-code-login");
const inputCodeRegister = document.getElementById("input-code-register")

function addValidationListeners(inputElement, validationFunction) {
    inputElement.addEventListener("focus", () => changeStyle(inputElement, validationFunction(inputElement.value)));
    inputElement.addEventListener("input", () => changeStyle(inputElement, validationFunction(inputElement.value)));
    inputElement.addEventListener("blur", () => resetStyle(inputElement));
}

addValidationListeners(firstname, validName);
addValidationListeners(lastname, validName);
addValidationListeners(email, validEmail);
addValidationListeners(phone, validPhone);
addValidationListeners(passwordRegister, validPassword);
addValidationListeners(rePasswordRegister, (val) => validRePassword(passwordRegister.value, val));
addValidationListeners(username, (val) => validEmail(val) || validPhone(val));
addValidationListeners(passwordLogin, validPassword);
addValidationListeners(inputCodeLogin, () => validCaptcha("login"));
addValidationListeners(inputCodeRegister, () => validCaptcha("register"));

buttonSubmitRegister.addEventListener("mouseover", () => changeTypeSubmit(buttonSubmitRegister,
    validName(firstname.value) && validName(lastname.value) && validEmail(email.value)
    && validPhone(phone.value) && validPassword(passwordRegister.value)
    && validRePassword(passwordRegister.value, rePasswordRegister.value)
    && validCaptcha("register")));

buttonSubmitLogin.addEventListener("mouseover", () => changeTypeSubmit(buttonSubmitLogin,
    (validEmail(username.value) || validPhone(username.value)) && validPassword(passwordLogin.value) && validCaptcha("login")));

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

function validName(input) {
    if (!checkBlank(input) || containsSpecialCharacter(input)) return false;
    return input.split(" ").every(word => isVietnameseCapitalized(word));
}

function validEmail(input) {
    if (!checkBlank(input)) return false;

    if (!input.includes("@") || !input.includes(".")) return false;

    const [name, domainPart] = input.split("@");
    if (!name || !domainPart) return false;

    if (containsSpecialCharacter(name)) return false;

    const domain = domainPart.split(".");
    return !domain.some(ele => ele === "" || containsSpecialCharacter(ele));
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

function validCaptcha(input) {
    // let inputCode = document.getElementById(`input-code-${input}`).value;
    // let code = document.getElementById(`code-${input}`).textContent;
    // return inputCode === code;
    return true;
}


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

function addValidationListeners(inputElement, validationFunction) {
    inputElement.addEventListener("focus", () => changeStyle(inputElement, validationFunction(inputElement)));
    inputElement.addEventListener("focus", () => showMessage(inputElement));
    inputElement.addEventListener("input", () => changeStyle(inputElement, validationFunction(inputElement)));
    inputElement.addEventListener("input", () => showMessage(inputElement));
    inputElement.addEventListener("blur", () => resetStyle(inputElement));
    inputElement.addEventListener("blur", () => hideMessage(inputElement));
}

addValidationListeners(firstname, validName);
addValidationListeners(lastname, validName);
addValidationListeners(email, validEmail);
addValidationListeners(phone, validPhone);
addValidationListeners(passwordRegister, validPassword);
addValidationListeners(rePasswordRegister, (element) => validRePassword(passwordRegister, element));
addValidationListeners(username, (element) => validEmail(element) || validPhone(element));
addValidationListeners(passwordLogin, validPassword);


function validName(element) {
    const value = element.value;
    let eleMes = document.getElementById(`message-${element.id}`);
    let contentMes = "<ul>";

    if (!checkBlank(value)) {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.name[0] + "</li>";
    } else {
        contentMes += "<li class=\"checked\">" + MESSAGE.name[0] + "</li>";
    }

    if (containsSpecialCharacter(value)) {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.name[1] + "</li>";
    } else {
        contentMes += "<li class=\"checked\">" + MESSAGE.name[1] + "</li>";
    }

    if (value.split(" ").every(word => isVietnameseCapitalized(word))) {
        contentMes += "<li class=\"checked\">" + MESSAGE.name[2] + "</li>";
    } else {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.name[2] + "</li>";
    }

    contentMes += "</ul>";
    eleMes.innerHTML = contentMes;

    return !contentMes.includes("unchecked");
}

function validEmail(element) {
    const value = element.value;
    let eleMes = document.getElementById(`message-${element.id}`);
    let contentMes = "<ul>";

    if (checkBlank(value)) {
        contentMes += "<li class=\"checked\">" + MESSAGE.email[0] + "</li>";
    } else {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.email[0] + "</li>";
    }

    const [name, domainPart] = value.split("@");
    const validFormat = value.includes("@") && value.includes(".") &&
        name && domainPart &&
        !containsSpecialCharacter(name) &&
        domainPart.split(".").every(part => part && !containsSpecialCharacter(part));

    if (!validFormat) {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.email[1] + "</li>";
    } else {
        contentMes += "<li class=\"checked\">" + MESSAGE.email[1] + "</li>";
    }

    contentMes += "</ul>";
    eleMes.innerHTML = contentMes;

    return !contentMes.includes("unchecked");
}

function validPhone(element) {
    const value = element.value;
    let eleMes = document.getElementById(`message-${element.id}`);
    let contentMes = "<ul>";

    if (!checkBlank(value)) {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.phone[0] + "</li>";
    } else {
        contentMes += "<li class=\"checked\">" + MESSAGE.phone[0] + "</li>";
    }

    const phoneRegex = /^0\d{9}$/;
    if (!phoneRegex.test(value)) {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.phone[1] + "</li>";
    } else {
        contentMes += "<li class=\"checked\">" + MESSAGE.phone[1] + "</li>";
    }

    contentMes += "</ul>";
    eleMes.innerHTML = contentMes;

    return !contentMes.includes("unchecked");
}

function validPassword(element) {
    const value = element.value;
    let eleMes = document.getElementById(`message-${element.id}`);
    let contentMes = "<ul>";

    if (value === "") {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.password[0] + "</li>";
    } else {
        contentMes += "<li class=\"checked\">" + MESSAGE.password[0] + "</li>";
    }

    contentMes += "</ul>";
    eleMes.innerHTML = contentMes;

    return !contentMes.includes("unchecked");
}

function validRePassword(passwordElement, rePasswordElement) {
    const passwordValue = passwordElement.value;
    const rePasswordValue = rePasswordElement.value;
    let eleMes = document.getElementById(`message-${rePasswordElement.id}`);
    let contentMes = "<ul>";

    if (rePasswordValue === "") {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.rePassword[0] + "</li>";
    } else {
        contentMes += "<li class=\"checked\">" + MESSAGE.rePassword[0] + "</li>";
    }

    if (passwordValue !== rePasswordValue) {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.rePassword[1] + "</li>";
    } else {
        contentMes += "<li class=\"checked\">" + MESSAGE.rePassword[1] + "</li>";
    }

    contentMes += "</ul>";
    eleMes.innerHTML = contentMes;

    return !contentMes.includes("unchecked");
}


function showMessage(inputField) {
    const popover = document.getElementById(`message-${inputField.id}`);
    const rect = inputField.getBoundingClientRect();
    popover.style.top = `${rect.bottom }px`;
    popover.style.left = `${rect.left }px`;
    popover.style.width = `${rect.width}px`;
    if (!popover.classList.contains('show')) {
        popover.classList.add('show');
    }
}

function hideMessage(inputField) {
    const popover = document.getElementById(`message-${inputField.id}`);
    if (popover.classList.contains('show')) {
        popover.classList.remove('show');
    }
}

buttonSubmitRegister.addEventListener("mouseover", () => changeTypeSubmit(buttonSubmitRegister,
    validName(firstname.value) && validName(lastname.value) && validEmail(email.value)
    && validPhone(phone.value) && validPassword(passwordRegister.value)
    && validRePassword(passwordRegister.value, rePasswordRegister.value) && validReCaptcha(0)));

buttonSubmitLogin.addEventListener("mouseover", () => changeTypeSubmit(buttonSubmitLogin,
    (validEmail(username.value) || validPhone(username.value)) && validPassword(passwordLogin.value) && validReCaptcha(1)));

function changeTypeSubmit(element, isValid) {
    if(isValid) {
        element.type = "submit";
        element.removeAttribute("data-bs-toggle");
        element.removeAttribute("data-bs-target");
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
    const capitalizedVietnameseRegex = /^[A-ZÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠƯÇÝỲỴỶỸàáâãèéêìíòóôõùúăđĩũơưçýỳỵỷỹ][a-zàáâãèéêìíòóôõùúăđĩũơưç]*$/u;

    return capitalizedVietnameseRegex.test(input);
}

function checkBlank(input) {
    return input.trim() !== "";
}

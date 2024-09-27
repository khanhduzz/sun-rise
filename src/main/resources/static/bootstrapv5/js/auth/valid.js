const MESSAGE = {
    name: ["Không được để trống", "Không chứa kí tự đặc biệt", "Viết hoa chữ cái đầu"],
    email: ["Không được để trống", "Đúng định dạng: name@domain"],
    phone: ["Không được để trống", "Có 10 chữ số", "Đúng đầu số Việt Nam"],
    password: ["Không được để trống", "Có it nhất 8 kí tự", "Có it nhất 1 kí tự viết hoa", "Có ít nhât 1 kí tự viết thường",
                "Có ít nhất 1 kí tự đặc biệt"],
    rePassword: ["Không được để trống","Trùng với mật khẩu đã nhập"]
}
const PREFIX_PHONE = ["032", "033", "034", "035", "036", "037", "038", "039", "081", "082", "083", "084", "085",
                            "070", "076", "077", "078", "079", "056", "058", "059"];

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
const buttonSendMail = document.getElementById("btn-sendMail");
const buttonChangePassword = document.getElementById("submit-new-password");
const buttonCreateUserByAdmin = document.getElementById("submit-button-register-by-admin");
const buttonEditUserByAdmin = document.getElementById("submit-button-edit-by-admin");
const buttonEditInfor = document.getElementById("button-edit-infor");
const newPasswordEditInfor= document.getElementById("newPassword");
const newPasswordConfirmEditInfor= document.getElementById("confirmNewPassword");

function addValidationListeners(inputElement, validationFunction) {
    if (inputElement != null) {
        inputElement.addEventListener("focus", () => changeStyle(inputElement, validationFunction(inputElement)));
        inputElement.addEventListener("focus", () => showMessage(inputElement));
        inputElement.addEventListener("input", () => changeStyle(inputElement, validationFunction(inputElement)));
        inputElement.addEventListener("input", () => showMessage(inputElement));
        inputElement.addEventListener("blur", () => resetStyle(inputElement));
        inputElement.addEventListener("blur", () => hideMessage(inputElement));
    }
}

addValidationListeners(firstname, validName);
addValidationListeners(lastname, validName);
addValidationListeners(email, validEmail);
addValidationListeners(phone, validPhone);
addValidationListeners(passwordRegister, validPassword);
addValidationListeners(rePasswordRegister, (element) => validRePassword(passwordRegister, element));
addValidationListeners(username, validUsername);
addValidationListeners(newPasswordEditInfor, validPassword);
addValidationListeners(newPasswordConfirmEditInfor, (element) => validRePassword(newPasswordEditInfor, element));

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
    const noDiacritics = name && name.normalize('NFD').replace(/[\u0300-\u036f]/g, "") === name;
    const validFormat = value.includes("@") && value.includes(".") &&
        name && domainPart &&
        noDiacritics &&
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

    if (PREFIX_PHONE.some(prefix => value.startsWith(prefix))) {
        contentMes += "<li class=\"checked\">" + MESSAGE.phone[2] + "</li>";
    } else {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.phone[2] + "</li>";
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

    if (value.length <= 8) {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.password[1] + "</li>";
    } else {
        contentMes += "<li class=\"checked\">" + MESSAGE.password[1] + "</li>";
    }

    if (!/[A-Z]/.test(value)) {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.password[2] + "</li>";
    } else {
        contentMes += "<li class=\"checked\">" + MESSAGE.password[2] + "</li>";
    }

    if (!/[a-z]/.test(value)) {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.password[3] + "</li>";
    } else {
        contentMes += "<li class=\"checked\">" + MESSAGE.password[3] + "</li>";
    }

    if (!containsSpecialCharacter(value)) {
        contentMes += "<li class=\"unchecked\">" + MESSAGE.password[4] + "</li>";
    } else {
        contentMes += "<li class=\"checked\">" + MESSAGE.password[4] + "</li>";
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

function validUsername(element) {
    const value = element.value.trim();
    if (/[^0-9]/.test(value)) {
        return validEmail(element);
    } else {
        return validPhone(element);
    }
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

buttonSubmitRegister?.addEventListener("mouseover", () => changeTypeSubmit(buttonSubmitRegister,
    validName(firstname) && validName(lastname) && validEmail(email)
    && validPhone(phone) && validPassword(passwordRegister)
    && validRePassword(passwordRegister, rePasswordRegister) && validReCaptcha(0)));

buttonSubmitLogin?.addEventListener("mouseover", () => changeTypeSubmit(buttonSubmitLogin,
    validUsername(username) && checkBlank(passwordLogin.value) && validReCaptcha(1)));

buttonSendMail?.addEventListener("mouseover", () => changeTypeSubmit(buttonSendMail, validEmail(email)));

buttonChangePassword?.addEventListener("mouseover", () =>
    changeTypeSubmit(buttonChangePassword, validPassword(passwordRegister) && validRePassword(passwordRegister, rePasswordRegister)));

buttonCreateUserByAdmin?.addEventListener("mouseover", () => changeTypeSubmit(buttonCreateUserByAdmin,
    validName(firstname) && validName(lastname) && validEmail(email)
    && validPhone(phone) && validPassword(passwordRegister)
    && validRePassword(passwordRegister, rePasswordRegister)));

buttonEditUserByAdmin?.addEventListener("mouseover", () => changeTypeSubmit(buttonEditUserByAdmin,
    validName(firstname) && validName(lastname) && validEmail(email)
    && validPhone(phone)));

buttonEditInfor?.addEventListener("mouseover", () => changeTypeSubmit(buttonEditInfor,
    validName(firstname) && validName(lastname)));

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

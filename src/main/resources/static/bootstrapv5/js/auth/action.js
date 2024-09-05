const boxRegister = document.getElementById("box-register");
const boxLogin = document.getElementById("box-login");

const codeLogin = document.getElementById("code-login");
const codeRegister = document.getElementById("code-register");
const rollLogin = document.getElementById("roll-login");
const rollRegister = document.getElementById("roll-register");

window.onload = function() {
    let page = window.location.href.split("/").pop();

    if (page === "register") {
        boxRegister.style.visibility = "visible";
        boxRegister.style.opacity = "1";
    } else {
        boxLogin.style.visibility = "visible";
        boxLogin.style.opacity = "1";
    }

    codeLogin.textContent = getSecureRandomString(3);
    codeRegister.textContent = getSecureRandomString(3);
}

document.addEventListener("DOMContentLoaded", function() {
    let modalElement = document.getElementById('popup-failed-register');

    if (modalElement) {
        let myModal = new bootstrap.Modal(modalElement);
        myModal.show();
    }
});

document.addEventListener("DOMContentLoaded", function() {
    let modalElement = document.getElementById('popup-failed-login');

    if (modalElement) {
        let myModal = new bootstrap.Modal(modalElement);
        myModal.show();
    }
});

rollLogin.addEventListener("click", function() {
    codeLogin.textContent = getSecureRandomString(3);
});

rollRegister.addEventListener("click", function() {
    codeRegister.textContent = getSecureRandomString(3);
});

function getSecureRandomString(length) {
    const charset = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
    let result = '';
    const buffer = new Uint8Array(length);
    window.crypto.getRandomValues(buffer);

    for (let i = 0; i < length; i++) {
        result += charset.charAt(buffer[i] % charset.length);
    }
    return result;
}
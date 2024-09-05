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
    } else if (page === "login") {
        boxLogin.style.visibility = "visible";
        boxLogin.style.opacity = "1";
    }

    codeLogin.textContent = random();
    codeRegister.textContent = random();
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
    codeLogin.textContent = random();
});

rollRegister.addEventListener("click", function() {
    codeRegister.textContent = random();
});

function random() {
    const randomChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    let resRandom = "";
    for (let i = 1; i <= 3; i++) {
        resRandom += randomChar.charAt(Math.floor(Math.random() * randomChar.length));
    }

    return resRandom;
}
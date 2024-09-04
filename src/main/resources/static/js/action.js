document.addEventListener("DOMContentLoaded", function() {
    var modalElement = document.getElementById('popup-failed-register');

    if (modalElement) {
        var myModal = new bootstrap.Modal(modalElement);
        myModal.show();
    }
});

document.addEventListener("DOMContentLoaded", function() {
    var modalElement = document.getElementById('popup-failed-login');

    if (modalElement) {
        var myModal = new bootstrap.Modal(modalElement);
        myModal.show();
    }
});

const formRegister = document.getElementById("form-register");
const coverRegister = document.getElementById("cover-register");
const formLogin = document.getElementById("form-login");
const coverLogin = document.getElementById("cover-login");
const boxRegister = document.getElementById("box-register");
const boxLogin = document.getElementById("box-login");

window.onload = function() {
    let page = window.location.href.split("/").pop();
    if(page === "register") {
        boxLogin.classList.add("hidden");
        boxRegister.classList.remove("hidden");
    } else if (page === "login"){
        boxLogin.classList.remove("hidden");
        boxRegister.classList.add("hidden");
    }
}
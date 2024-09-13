const boxRegister = document.getElementById("box-register");
const boxLogin = document.getElementById("box-login");

window.onload = function() {
    let page = window.location.href.split("/").pop();

    if (page === "register") {
        boxRegister.style.visibility = "visible";
        boxRegister.style.opacity = "1";
    } else {
        boxLogin.style.visibility = "visible";
        boxLogin.style.opacity = "1";
    }
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
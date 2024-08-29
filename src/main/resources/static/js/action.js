// index file
window.onload = function (e) {
    console.log("Run this");
    let link = window.location.href;
    let endpoint = link.split("/").pop();
    console.log(endpoint);
    if (endpoint === "register") {
        tabLogin.classList.add('hidden');
        formLogin.classList.add('hidden');
        tabRegister.classList.remove('hidden');
        formRegister.classList.remove('hidden');
    }
    console.log("aaaaa");
}


//chuyển đổi login và register
const tabLogin = document.getElementById('tabLogin');
const formLogin = document.getElementById('formLogin');

const tabRegister = document.getElementById('tabRegister');
const formRegister = document.getElementById('formRegister');

const login = document.getElementById('login');
const register = document.getElementById('register');

register.addEventListener('click', function () {
    // Reset các lớp trước khi thêm lớp mới
    tabLogin.classList.remove('slide-out-right', 'slide-in-left');
    formLogin.classList.remove('slide-out-left', 'slide-in-right');
    tabRegister.classList.remove('slide-out-left', 'slide-in-right');
    formRegister.classList.remove('slide-out-right', 'slide-in-left');

    // Đưa form đăng nhập ra khỏi màn hình
    tabLogin.classList.add('slide-out-right');
    formLogin.classList.add('slide-out-left');
    
    // Hiển thị form đăng ký với hiệu ứng
    setTimeout(() => {
        tabLogin.classList.add('hidden');
        formLogin.classList.add('hidden'); 

        tabRegister.classList.remove('hidden');
        formRegister.classList.remove('hidden');
    }, 500);
});


login.addEventListener('click', function () {
    // Reset các lớp trước khi thêm lớp mới
    tabRegister.classList.remove('slide-out-left', 'slide-in-right');
    formRegister.classList.remove('slide-out-right', 'slide-in-left');
    tabLogin.classList.remove('slide-out-right', 'slide-in-left');
    formLogin.classList.remove('slide-out-left', 'slide-in-right');

    // Đưa form đăng ký ra khỏi màn hình
    tabRegister.classList.add('slide-out-left');
    formRegister.classList.add('slide-out-right');

    // Hiển thị form đăng nhập với hiệu ứng
    setTimeout(() => {
        tabRegister.classList.add('hidden');
        formRegister.classList.add('hidden');

        tabLogin.classList.remove('hidden');
        formLogin.classList.remove('hidden');
    }, 500);
});

// đóng popup báo lỗi
function closePopup() {
    var popup = document.getElementById('popup');
    var overlay = document.getElementById('overlay');

    popup.classList.remove('show');
    overlay.classList.remove('show');

    setTimeout(function() {
        popup.style.display = 'none';
        overlay.style.display = 'none';
    }, 300);
}

document.addEventListener("load", function (){
    const clPopup = document.getElementById("closePopup");
    console.log(clPopup);
    clPopup.addEventListener("click", closePopup);
})


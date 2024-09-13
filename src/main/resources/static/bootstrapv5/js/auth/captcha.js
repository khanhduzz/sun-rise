let CaptchaCallback = function() {
    console.log("CaptchaCallback executed");
    try {
        if (document.getElementById('recaptcha-register')) {
            grecaptcha.render('recaptcha-register', {'sitekey': '6LfblD0qAAAAAC1k-Zd8GfiyXmKjNWBV4xMC1BNP'});
        }
        if (document.getElementById('recaptcha-login')) {
            grecaptcha.render('recaptcha-login', {'sitekey': '6LfblD0qAAAAAC1k-Zd8GfiyXmKjNWBV4xMC1BNP'});
        }
    } catch (error) {
        console.error("Error rendering reCAPTCHA: ", error);
    }
};

window.CaptchaCallback = CaptchaCallback;
$(document).ready(function() {
    registerClient();
});

var secretKey;
var publicKey;
var request;
var response;

function registerClient() {
    ajaxPost('/client/register', null, function(data) {
        console.log(data);
        this.secretKey = data.secretKey;
        this.publicKey = data.publicKey;

        this.newCaptcha();
    }.bind(this));
}

function newCaptcha() {
    ajaxGet('/captcha/new?publicKey=' + publicKey, function(data) {
        this.request = data.request;

        if ('answer' in data) {
            console.log(data.answer);
        }

        this.getCaptchaImage();
    }.bind(this));
}

function getCaptchaImage() {
    ajaxGet('/captcha/image?publicKey=' + publicKey + '&request=' + request, function(data) {
        var captcha = data.base64image;
        var img = $('<img>').attr({src: captcha});
        $('.captchaImg').html(img);
    });
}

$("#captchaForm").submit(function(e) {
    e.preventDefault();
    var data = {
        publicKey: publicKey,
        request: request
    };

    $.each(this, function(i, v){
        var input = $(v);
        data[input.attr('name')] = input.val();
        delete data['undefined'];
    });

    console.log(data);
    solveCaptcha(JSON.stringify(data));
});

function solveCaptcha(captchaSolution) {
    ajaxPost('/captcha/solve', captchaSolution, function(data) {
        this.response = data.response;
        console.log(data);
        this.verifyCaptcha();
    }.bind(this));
}

function verifyCaptcha() {
    ajaxGet('/captcha/verify?secretKey=' + secretKey + '&response=' + response, function(data) {
        console.log(data);
        if (!!data.success) {
            this.setHtmlToMainPanel('<h2>Success!</h2>');
        } else {
            alert('Error!\n' + data.errorCode);
            this.newCaptcha();
        }
    }.bind(this));
}

function setHtmlToMainPanel(html) {
    $('.captcha').html(html);
}

function ajaxGet(url, callbackSuccess) {
    $.ajax({
        contentType:"application/json;charset=utf-8",
        type: 'GET',
        url: url,
        dataType: 'json',
        success: function(receivedData) {
            if(receivedData && callbackSuccess) callbackSuccess(receivedData);
        },
        error: function(data) {
            runErrorHandler(data)
        }
    });
}

function ajaxPost(url, data, callbackSuccess) {
    $.ajax({
        contentType:"application/json;charset=utf-8",
        type: 'POST',
        url: url,
        dataType: 'json',
        data: data,
        success: function(receivedData) {
            if(receivedData && callbackSuccess) callbackSuccess(receivedData)
        },
        error: function(data) {
            runErrorHandler(data)
        }
    });
}

function runErrorHandler(data) {
    if (data.status === 403) {
        setHtmlToMainPanel('<h2>Your session is blocked!</h2>');
    } else {
        newCaptcha();
    }
}
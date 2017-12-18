/**
 * Created by Henrichs on 12/17/2017.
 * JS for form validation (registration/login inputs)
 */

// Register page input fields
var inputEmailAddress = $('#email-address');
var inputEmailAddressValid = false;

var inputUsername = $('#username');
var inputUsernameValid = false;

var inputPassword = $('#pass');
var inputPasswordValid = false;

var inputConfirmPassword = $('#pass-confirm');
var inputConfirmPasswordValid = false;

// Attach blur events
inputEmailAddress.blur(function() {
    var validated = validateField('email');
    if(!validated.valid) {
        inputEmailAddressValid = false;
        showFieldInvalid(this, validated.message);
    } else {
        inputEmailAddressValid = true;
        showFieldValid(this);
    }
});
inputUsername.blur(function () {
    var validated = validateField('username');
    if(!validated.valid) {
        inputUsernameValid = false;
        showFieldInvalid(this, validated.message);
    } else {
        inputUsernameValid = true;
        showFieldValid(this);
    }
});
inputPassword.blur(function () {
    var validated = validateField('password');
    if(!validated.valid) {
        inputPasswordValid = false;
        showFieldInvalid(this, validated.message);
    } else {
        inputPasswordValid = true;
        showFieldValid(this);
    }
});
inputConfirmPassword.blur(function () {
    var validated = validateField('confirmPass');
    if(!validated.valid) {
        inputConfirmPasswordValid = false;
        showFieldInvalid(this, validated.message);
    } else {
        inputConfirmPasswordValid = true;
        showFieldValid(this);
    }
});

/**
 * Confirm all fields are valid before submitting
 */
$('#register-form').on('submit', function () {
    var fields = [inputEmailAddressValid, inputUsernameValid, inputPasswordValid, inputConfirmPasswordValid];
    if(!fields.every(function(fld){return fld;})) {
        document.getElementById('submit-invalid').style.display = 'inline';
        return false;
    }
});

/**
 * Validation function
 * @param field input field in question
 * @returns {*} object with boolean value for validity and optional error message
 */
function validateField(field) {
    switch(field) {
        case 'email':
            // check for valid format
            var email = inputEmailAddress.val();
            var re = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            if(email.length > 0 && !re.test(email.toLowerCase())) {
                return {
                    valid: false,
                    message: 'This is not a valid email address.'
                }
            }
            // check if email already in DB
            var emailExists = false;
            if(email.length > 0) {
                $.ajax({
                    type: "GET",
                    url: "http://localhost:9000/checkusersdb?value=" + email + "&type=email_address",
                    async: false,
                    success: function (response) {
                        var json = JSON.parse(JSON.stringify(response));
                        if(json.entryExists) {
                            emailExists = true;
                        }
                    }
                });
            }
            if(emailExists) {
                return {
                    valid: false,
                    message: 'There\'s an account with this email address.'
                }
            }
            return {valid: true};

        case 'username':
            // check if username already in DB
            var username = inputUsername.val();
            var usernameExists = false;
            if(username.length > 0) {
                $.ajax({
                    type: "GET",
                    url: "http://localhost:9000/checkusersdb?value=" + username + "&type=username",
                    async: false,
                    success: function (response) {
                        var json = JSON.parse(JSON.stringify(response));
                        if(json.entryExists) {
                            usernameExists = true;
                        }
                    }
                });
            }
            if(usernameExists) {
                return {
                    valid: false,
                    message: 'Sorry, this username has already been taken.'
                }
            }
            return {valid: true};

        case 'password':
            var password = inputPassword.val();
            // check length constraints
            if(password.length > 0 && password.length < 6) {
                return {
                    valid: false,
                    message: 'Your password must be at least 6 characters long.'
                }
            } else if(password.length > 30) {
                return {
                    valid: false,
                    message: 'We like our passwords under 30 characters long.'
                }
            }
            return {valid: true};

        case 'confirmPass':
            var originalPass = inputPassword.val();
            var confirmedPass = inputConfirmPassword.val();
            if(originalPass.length > 0 && confirmedPass.length > 0 && confirmedPass !== originalPass) {
                return {
                    valid: false,
                    message: 'Passwords do not match.'
                }
            }
            return {valid: true};
    }
}

/**
 * Inform user of invalid field
 * @param field input field in question
 * @param message error msg to be displayed
 */
function showFieldInvalid(field, message) {
    $(field).addClass('invalid');
    $('.input-error')[$('input').index(field)].innerHTML = message;
}

/**
 * Undo invalid field effect
 * @param field input field in question
 */
function showFieldValid(field) {
    $(field).removeClass('invalid');
    if($(field).val().length > 0) {
        $('.input-error')[$('input').index(field)].innerHTML = '<i style="color: #00ff00; font-size: 20px" class="fa fa-check" aria-hidden="true"></i>';
    } else {
        $('.input-error')[$('input').index(field)].innerHTML = '';
    }
}

# CaptchaService
CaptchaService provides a free service that protects your website from spam and abuse.

Implemented with Spring Boot.

Screenshot with an example of the generated captcha:

<img src="https://github.com/102Rudy/CaptchaService/raw/master/CaptchaService.png" width="30%">

Supported properties of the captcha verification system:
<ul>
  <li>length - the length of captcha word</li>
  <li>ttl - time to live, limits the time when the generated captcha test is valid</li>
  <li>amountOfCaptchaTests - the max amount of captcha tests in a session before the server bans the session</li>
</ul>

Supported properties of the captcha image:
<ul>
  <li>width, height (for the whole captcha image)</li>
  <li>rotationAmplitude, scaleAmplitude, shearAmplitude (for every letter in the captcha)</li>
</ul>

A test mode (when the server sends correct answer to the captcha) is also supported. Set the property production = false to enable the test mode.
<br>All properties are located in the <b>application.properties</b> file.

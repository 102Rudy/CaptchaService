# CaptchaService
CaptchaService provides a free service that protects your website from spam and abuse.

Implemented with Spring Boot.

Screenshot with example of generated captcha:

<img src="https://github.com/102Rudy/CaptchaService/raw/master/CaptchaService.png?" width="30%">

Supported properties of captcha verification system:
<ul>
  <li>length - length of captcha word</li>
  <li>ttl - time to live, limits the time when the generated captcha test is valid</li>
  <li>amountOfCaptchaTests - max amount of captcha tests in session before the server will ban the session</li>
</ul>

Supported properties of captcha image:
<ul>
  <li>width, height (for entire captcha image)</li>
  <li>rotationAmplitude, scaleAmplitude, shearAmplitude (for every letter in captcha)</li>
</ul>

Also supported test mode when server sends right answer for captcha. Set property production = false, to enable test mode.
<br>All properties placed in <b>application.properties</b> file

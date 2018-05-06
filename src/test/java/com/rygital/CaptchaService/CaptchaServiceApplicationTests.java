package com.rygital.CaptchaService;

import com.rygital.CaptchaService.captcha.model.RequestIdAndAnswer;
import com.rygital.CaptchaService.controller.CaptchaController;
import com.rygital.CaptchaService.controller.ClientController;
import com.rygital.CaptchaService.model.Result;
import com.rygital.CaptchaService.model.Solution;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CaptchaServiceApplicationTests {

	private static final String INVALID_ANSWER = "invalid_answer";

	@Autowired
	private ClientController clientController;
	@Autowired
	private CaptchaController captchaController;

	@Test
	public void fullRightTest() {
		Pair<String, String> publicAndSecret = createUserSession();
		String publicKey = publicAndSecret.getKey();
		String secretKey = publicAndSecret.getValue();

		RequestIdAndAnswer requestIdAndAnswer = newCaptcha(publicKey);
		produceCaptchaSession(publicKey, secretKey, requestIdAndAnswer.getRequestId(), requestIdAndAnswer.getAnswer());
	}

	@Test
	public void fullTestWithInvalidAnswer() {
		Pair<String, String> publicAndSecret = createUserSession();
		String publicKey = publicAndSecret.getKey();
		String secretKey = publicAndSecret.getValue();

		RequestIdAndAnswer requestIdAndAnswer = newCaptcha(publicKey);
		produceCaptchaSession(publicKey, secretKey, requestIdAndAnswer.getRequestId(), INVALID_ANSWER);
	}

	private void produceCaptchaSession(String publicKey, String secretKey, int request, String answer) {
		String response = solveCaptcha(publicKey, request, answer);
		Result result = verifyCaptcha(secretKey, response);

		boolean expectedSuccess = true;
		if (answer.equals(INVALID_ANSWER)) expectedSuccess = false;

		Assert.assertEquals(expectedSuccess, result.isSuccess());
	}

	private Pair<String, String> createUserSession() {
		Map<String, String> userSession = clientController.register();
		return new Pair<>(userSession.get("publicKey"), userSession.get("secretKey"));
	}

	@SuppressWarnings("unchecked")
	private RequestIdAndAnswer newCaptcha(String publicKey) {
		HashMap<String, String> requestIdAndAnswerMap =
				(HashMap<String, String>) captchaController.newCaptcha(publicKey).getBody();
		return new RequestIdAndAnswer(
				Integer.parseInt(requestIdAndAnswerMap.get("request")),
				requestIdAndAnswerMap.get("answer")
		);
	}

	@SuppressWarnings("unchecked")
	private String solveCaptcha(String publicKey, int request, String answer) {
		HashMap<String, String> responseMap = (HashMap<String, String>) captchaController.solveCaptcha(
				new Solution(publicKey, request, answer)
		).getBody();

		return responseMap.get("response");
	}

	private Result verifyCaptcha(String secretKey, String response) {
		return (Result) captchaController.verify(secretKey, response).getBody();
	}
}
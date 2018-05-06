package com.rygital.CaptchaService;

import com.rygital.CaptchaService.controller.ClientController;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClientControllerTest {
    @Test
    public void testClientController() {
        ClientController clientController = new ClientController();

        Map<String, String> userSession = clientController.register();
        String publicKey = userSession.get("publicKey");
        String secretKey = userSession.get("secretKey");

        Assert.assertEquals(secretKey, clientController.getSecretKeyOfUserSession(publicKey));
    }
}

package ch.wesr.kpay.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class AccountController {

    @Autowired
    private InteractiveQueryService interactiveQueryService;

    @SendTo("/topic/account")
    public String send() throws Exception {

        return "Hallo Schatzi";
    }
}

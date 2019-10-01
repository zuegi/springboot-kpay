package ch.wesr.kpay;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface KpayBindings {

    String PAYMENT_INCOMING = "pinc";

    @Output(PAYMENT_INCOMING)
    MessageChannel paymentIncoming();

}

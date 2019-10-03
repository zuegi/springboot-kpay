package ch.wesr.kpay.payments;

import ch.wesr.kpay.payments.config.KpayBindings;
import ch.wesr.kpay.payments.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccountProcessor {

    @StreamListener
    public void process(@Input(KpayBindings.PAYMENT_INFLIGHT) KStream<String, Payment> stream) {

        stream.foreach((key, value) -> {
            log.info("key: {}, value {}", key, value.toString());
        });
    }
}


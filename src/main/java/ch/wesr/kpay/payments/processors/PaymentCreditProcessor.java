package ch.wesr.kpay.payments.processors;

import ch.wesr.kpay.config.KpayBindings;
import ch.wesr.kpay.payments.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentCreditProcessor {

    @StreamListener
    @SendTo(KpayBindings.PAYMENT_INFLIGHT_CREDIT_OUTPUT)
    public KStream<String, Payment> process(@Input(KpayBindings.PAYMENT_INFLIGHT_CREDIT_INPUT) KStream<String, Payment> paymentCreditStream) {

        return paymentCreditStream
                .filter((key, value) -> value.getState() == Payment.State.credit)
                .map((KeyValueMapper<String, Payment, KeyValue<String, Payment>>) (key, value) -> {
                    value.setStateAndId(Payment.State.complete);
                    return new KeyValue<>(value.getId(), value);
                });

    }
}

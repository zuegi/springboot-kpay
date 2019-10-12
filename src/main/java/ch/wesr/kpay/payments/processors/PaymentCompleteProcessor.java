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
public class PaymentCompleteProcessor {

    @StreamListener
    @SendTo(KpayBindings.PAYMENT_COMPLETE_OUTPUT)
    public KStream<String, Payment> process(@Input(KpayBindings.PAYMENT_COMPLETE_INPUT) KStream<String, Payment> paymentCompleteStream) {

        return paymentCompleteStream
                .filter((key, value) -> value.getState() == Payment.State.complete)
                .map((KeyValueMapper<String, Payment, KeyValue<String, Payment>>) (key, value) -> {
                    value.setStateAndId(Payment.State.confirmed);
                    return new KeyValue<>(value.getId(), value);
                });

    }
}

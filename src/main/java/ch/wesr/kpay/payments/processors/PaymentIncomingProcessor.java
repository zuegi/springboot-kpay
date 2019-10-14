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
public class PaymentIncomingProcessor {


    @StreamListener
    @SendTo(KpayBindings.PAYMENT_INCOMING_OUTPUT)
    public KStream<String, Payment> process(@Input(KpayBindings.PAYMENT_INCOMING_INPUT) KStream<String, Payment> paymentIncomingStream) {
        return paymentIncomingStream
                .map((KeyValueMapper<String, Payment, KeyValue<String, Payment>>) (key, value) -> {
                    if (value.getState() == Payment.State.incoming) {
                        value.setStateAndId(Payment.State.debit);
                    }
                    return new KeyValue<>(value.getId(), value);
                });


    }
}

package ch.wesr.kpay;

import ch.wesr.kpay.payments.model.Payment;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface KpayBindings {

    String PAYMENT_INCOMING_SOURCE = "psrc";
    String PAYMENT_INCOMING = "pinc";
    String PAYMENT_INFLIGHT_OUT = "pinfout";

    @Output(PAYMENT_INCOMING_SOURCE)
    MessageChannel paymentSource();

    @Input(PAYMENT_INCOMING)
    KStream<String, Payment> paymentIncoming();

    @Output(PAYMENT_INFLIGHT_OUT)
    KStream<String, Payment> paymentInflightOut();
}

package ch.wesr.kpay.config;

import ch.wesr.kpay.payments.model.Payment;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface KpayBindings {

    String ACCOUNT_BALANCE_STORE = "accountBalanceStore";
    String PAYMENT_THROUGHPUT_STORE = "paymentThroughputStore";
    String PAYMENT_INFLIGHT_STORE = "paymentInflightStore";
    String PAYMENT_CONFIRMED_STORE = "paymentConfirmedStore";


    String PAYMENT_PRODUCER_OUTPUT = "paymentProducerOutput";

    String PAYMENT_INCOMING_INPUT = "paymentIncomingInput";
    String PAYMENT_INCOMING_OUTPUT = "paymentIncomingOutput";


    String PAYMENT_ACCOUNT_INPUT = "paymentAccountInput";
    String PAYMENT_ACCOUNT_OUTPUT = "paymentAccountOutput";


    String PAYMENT_COMPLETE_INPUT = "paymentCompleteInput";
    String PAYMENT_COMPLETE_OUTPUT = "paymentCompleteOutput";


    String PAYMENT_THROUGHPUT_INPUT = "paymentThroughputInput";

    String PAYMENT_CONFIRMED_INPUT = "paymentConfirmedInput";

    String PAYMENT_INFLIGHTSTATS_INPUT = "paymentInflightStatsInput";
    @Input(PAYMENT_INFLIGHTSTATS_INPUT)
    KStream<String, Payment> paymentInflightsInput();

    @Output(PAYMENT_PRODUCER_OUTPUT)
    MessageChannel paymentProducerOutput();

    @Input(PAYMENT_INCOMING_INPUT)
    KStream<String, Payment> paymentIncomingInput();
    @Output(PAYMENT_INCOMING_OUTPUT)
    KStream<String, Payment> paymentIncomingOutput();

    @Input(PAYMENT_ACCOUNT_INPUT)
    KStream<String, Payment> paymentAccountInput();
    @Output(PAYMENT_ACCOUNT_OUTPUT)
    KStream<String, Payment> paymentAccountOutput();

    @Input(PAYMENT_COMPLETE_INPUT)
    KStream<String, Payment> paymentCompleteInput();
    @Output(PAYMENT_COMPLETE_OUTPUT)
    KStream<String, Payment> paymentCompleteOutput();

    @Input(PAYMENT_THROUGHPUT_INPUT)
    KStream<String, Payment> paymentThroughputInput();

    @Input(PAYMENT_CONFIRMED_INPUT)
    KStream<String, Payment> paymentConfirmedInput();

}

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
    String PAYMENT_INFLIGHT_INPUT = "paymentInflightInput";
    String PAYMENT_DEBIT_INPUT = "paymentDebitInput";
    String PAYMENT_DEBIT_OUTPUT = "paymentDebitOutput";
    String PAYMENT_CREDIT_INPUT = "paymentCreditInput";
    String PAYMENT_CREDIT_OUTPUT = "paymentCreditOutput";
    String PAYMENT_COMPLETE_INPUT = "paymentCompleteInput";
    String PAYMENT_COMPLETE_OUTPUT = "paymentCompleteOutput";
    String PAYMENT_THROUGHPUT_INPUT = "paymentThroughputInput";
    String PAYMENT_CONFIRMED_INPUT = "paymentConfirmedInput";


    @Output(PAYMENT_PRODUCER_OUTPUT)
    MessageChannel paymentProducerOutput();

    @Input(PAYMENT_INCOMING_INPUT)
    KStream<String, Payment> paymentIncomingInput();

    @Output(PAYMENT_INCOMING_OUTPUT)
    KStream<String, Payment> paymentIncomingOutput();

    @Input(PAYMENT_INFLIGHT_INPUT)
    KStream<String, Payment> paymentInflightInput();

    @Input(PAYMENT_DEBIT_INPUT)
    KStream<String, Payment> paymentDebitInput();

    @Output(PAYMENT_DEBIT_OUTPUT)
    KStream<String, Payment> paymentDebitOutput();

    @Input(PAYMENT_CREDIT_INPUT)
    KStream<String, Payment> paymentCreditInput();

    @Output(PAYMENT_CREDIT_OUTPUT)
    KStream<String, Payment> paymentCreditOutput();

    @Input(PAYMENT_COMPLETE_INPUT)
    KStream<String, Payment> paymentCompleteInput();

    @Output(PAYMENT_COMPLETE_OUTPUT)
    KStream<String, Payment> paymentCompleteOutput();

    @Input(PAYMENT_THROUGHPUT_INPUT)
    KStream<String, Payment> paymentThroughputInput();

    @Input(PAYMENT_CONFIRMED_INPUT)
    KStream<String, Payment> paymentConfirmedInput();

}

package ch.wesr.kpay.config;

import ch.wesr.kpay.payments.model.Payment;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface KpayBindings {

    String ACCOUNT_BALANCE_STORE_NAME = "accountBalance";
    String PAYMENT_THROUGHPUT_STORE_NAME = "paymentThroughput";


    String PAYMENT_PRODUCER_OUTPUT = "paymentProducerOutput";
    String PAYMENT_INCOMING_INPUT = "paymentIncomingInput";
    String PAYMENT_INFLIGHT_SOURCE = "paymentInflight";
    String PAYMENT_INFLIGHT_DEBIT_INPUT = "paymentInflightDebitInput";
    String PAYMENT_INFLIGHT_DEBIT_OUTPUT = "paymentInflightDebitOutput";
    String PAYMENT_INFLIGHT_CREDIT_INPUT = "paymentInflightCreditInput";
    String PAYMENT_INFLIGHT_CREDIT_OUTPUT = "paymentInflightCreditOutput";
    String PAYMENT_COMPLETE_INPUT = "paymentCompleteInput";
    String PAYMENT_COMPLETE_OUTPUT = "paymentCompleteOutput";
    String PAYMENT_THROUGHPUT_INPUT = "paymentThroughputInput";


    @Output(PAYMENT_PRODUCER_OUTPUT)
    MessageChannel paymentProducerOutput();

    @Input(PAYMENT_INCOMING_INPUT)
    KStream<String, Payment> paymentIncomingInput();

    @Output(PAYMENT_INFLIGHT_SOURCE)
    KStream<String, Payment> paymentInflightSource();

    @Input(PAYMENT_INFLIGHT_DEBIT_INPUT)
    KStream<String, Payment> paymentInflightDebitInput();

    @Output(PAYMENT_INFLIGHT_DEBIT_OUTPUT)
    KStream<String, Payment> paymentInflightDebitOutput();

    @Input(PAYMENT_INFLIGHT_CREDIT_INPUT)
    KStream<String, Payment> paymentInflightCreditInput();

    @Output(PAYMENT_INFLIGHT_CREDIT_OUTPUT)
    KStream<String, Payment> paymentInflightCreditOutput();

    @Input(PAYMENT_COMPLETE_INPUT)
    KStream<String, Payment> paymentCompleteInput();

    @Output(PAYMENT_COMPLETE_OUTPUT)
    KStream<String, Payment> paymentCompleteOutput();

    @Input(PAYMENT_THROUGHPUT_INPUT)
    KStream<String, Payment> paymentThroughputInput();

    /*
* *************************************************************************************
*/
    // below to be refactored or deleted

    String PAYMENT_INCOMING_OUT = "pincout";
    String PAYMENT_INCOMING = "pinc";
    String PAYMENT_INCOMING_COMPLETED = "pinccompleted";
    String PAYMENT_INFLIGHT_OUT = "pinfout";
    String PAYMENT_INFLIGHT_OUT_OUT = "pinfoutout";
    String PAYMENT_INFLIGHT = "pinf";
    String PAYMENT_COMPLETE_OUT = "pcoout";
    String PAYMENT_COMPLETE = "pcom";
    String PAYMENT_COMPLETE_THROUGHPUT = "pcothroughput";
    String PAYMENT_CONFIRMED_OUT = "pconfout";


    public static final String STORE_NAME_INFLIGHT_METRICS = "inflight";

    @Output(PAYMENT_INCOMING_OUT)
    MessageChannel paymentIncomingOut();

    @Input(PAYMENT_INCOMING)
    KStream<String, Payment> paymentIncoming();

//    @Output(PAYMENT_INCOMING_COMPLETED)
//    KStream<String, Payment> paymentIncomingCompleted();

    @Output(PAYMENT_INFLIGHT_OUT)
    KStream<String, Payment> paymentInflightOut();

//    @Input(PAYMENT_INFLIGHT)
//    KStream<String, Payment> paymentInflight();

//    @Output(PAYMENT_INFLIGHT_OUT_OUT)
//    KStream<String, Payment> paymentInflightOutOut();

//    @Output(PAYMENT_COMPLETE_OUT)
//    KStream<String, Payment> paymentCompleteOut();

    @Input(PAYMENT_COMPLETE)
    KStream<String, Payment> paymentComplete();

    @Input(PAYMENT_COMPLETE_THROUGHPUT)
    KStream<String, Payment> paymentCompleteThroughput();

    @Output(PAYMENT_CONFIRMED_OUT)
    KStream<String, Payment>  paymentConfirmedOut();

}

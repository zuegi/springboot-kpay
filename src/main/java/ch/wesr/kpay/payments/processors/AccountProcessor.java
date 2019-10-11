package ch.wesr.kpay.payments.processors;

import ch.wesr.kpay.config.KpayBindings;
import ch.wesr.kpay.payments.model.AccountBalance;
import ch.wesr.kpay.payments.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

import java.io.UncheckedIOException;

@Slf4j
@Component
@SuppressWarnings("unchecked")
public class AccountProcessor {

    public static final String STORE_NAME = "account";


    private final Materialized<String, AccountBalance, KeyValueStore<Bytes, byte[]>> account;
    private final Materialized<String, AccountBalance, KeyValueStore<Bytes, byte[]>> accountStore;

    public AccountProcessor(@Qualifier("valueAccountBalanceJsonSerde") JsonSerde valueJsonSerde) {
        this.account = Materialized.as(STORE_NAME);
        this.accountStore = account.withKeySerde(new Serdes.StringSerde()).withValueSerde(
                valueJsonSerde);
    }

    @StreamListener
    @SendTo({KpayBindings.PAYMENT_INFLIGHT_OUT_OUT, KpayBindings.PAYMENT_COMPLETE_OUT, KpayBindings.PAYMENT_INCOMING_COMPLETED})
    public KStream<String, Payment>[] process(@Input(KpayBindings.PAYMENT_INFLIGHT) KStream<String, Payment> inflight) {
       /* inflight.foreach((key, value) -> {
            log.info("key: {}, value: {}", key, value);
        });*/
        /*
         * Debit & credit processing
         */
        KTable<String, AccountBalance> ktable = inflight.groupByKey()
                .aggregate(
                        AccountBalance::new,
                        (key, value, aggregate) -> aggregate.handle(key, value),
                        accountStore
                );




        Predicate<String, Payment> isCreditRecord =  (key, value) -> value.getState() == Payment.State.credit;
        Predicate<String, Payment> isCompleteRecord =  (key, value) -> value.getState() == Payment.State.complete;
        Predicate<String, Payment> isCompleteRecordForStats =  (key, value) ->  value.getFrom().equals("rene");


        /*
          * Data flow and state processing
          */
       return inflight
                .map((KeyValueMapper<String, Payment, KeyValue<String, Payment>>) (key, value) -> {
                    if (value.getState() == Payment.State.debit) {
                        value.setStateAndId(Payment.State.credit);
                    } else if (value.getState() == Payment.State.credit) {
                        value.setStateAndId(Payment.State.complete);
                    } else if (value.getState() == Payment.State.complete) {
                        log.error("Invalid payment:{}", value);
                        throw new RuntimeException("Invalid payment state:" + value);
                    }
                    return new KeyValue<>(value.getId(), value);
                })
                .branch(isCreditRecord, isCompleteRecord, isCompleteRecordForStats);

    }
}


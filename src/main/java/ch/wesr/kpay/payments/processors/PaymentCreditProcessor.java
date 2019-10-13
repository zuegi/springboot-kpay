package ch.wesr.kpay.payments.processors;

import ch.wesr.kpay.config.KpayBindings;
import ch.wesr.kpay.payments.model.AccountBalance;
import ch.wesr.kpay.payments.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentCreditProcessor {


    private final Materialized<String, AccountBalance, KeyValueStore<Bytes, byte[]>> account;
    private final Materialized<String, AccountBalance, KeyValueStore<Bytes, byte[]>> accountStore;

    public PaymentCreditProcessor(@Qualifier("valueAccountBalanceJsonSerde") JsonSerde valueJsonSerde) {
        this.account = Materialized.as(KpayBindings.ACCOUNT_BALANCE_STORE);
        this.accountStore = account.withKeySerde(new Serdes.StringSerde()).withValueSerde(
                valueJsonSerde);
    }


    @StreamListener
    @SendTo(KpayBindings.PAYMENT_CREDIT_OUTPUT)
    public KStream<String, Payment> process(@Input(KpayBindings.PAYMENT_CREDIT_INPUT) KStream<String, Payment> paymentCreditStream) {

        /*
         * Debit & credit processing
         *  KTable<String, AccountBalance>
         */
       paymentCreditStream
               .filter((key, value) -> value.getState() == Payment.State.credit)
               .groupByKey()
                .aggregate(
                        AccountBalance::new,
                        (key, value, aggregate) -> aggregate.handle(key, value),
                        accountStore
                );

        return paymentCreditStream
                .filter((key, value) -> value.getState() == Payment.State.credit)
                .map((KeyValueMapper<String, Payment, KeyValue<String, Payment>>) (key, value) -> {
                    value.setStateAndId(Payment.State.complete);
                    return new KeyValue<>(value.getId(), value);
                });

    }
}

package ch.wesr.kpay.payments.processors;

import ch.wesr.kpay.config.KpayBindings;
import ch.wesr.kpay.payments.model.ConfirmedStats;
import ch.wesr.kpay.payments.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentsConfirmedProcessor {

    public static final String STORE_NAME = "confirmed";
    long ONE_DAY = 24 * 60 * 60 * 1000L;

    Materialized<String, ConfirmedStats, WindowStore<Bytes, byte[]>> confirmedStore = Materialized.as(STORE_NAME);
    Materialized<String, ConfirmedStats, WindowStore<Bytes, byte[]>> confirmedWindowStore = confirmedStore.withKeySerde(new Serdes.StringSerde()).withValueSerde(new ConfirmedStats.Serde());

    @StreamListener
    @SendTo(KpayBindings.PAYMENT_CONFIRMED_OUT)
    public KStream<String, Payment> process(@Input(KpayBindings.PAYMENT_COMPLETE) KStream<String, Payment> complete) {

        KStream<String, Payment> completeStream = complete.map((KeyValueMapper<String, Payment, KeyValue<String, Payment>>) (key, value) -> {
            if (value.getState() == Payment.State.complete) {
                value.setStateAndId(Payment.State.confirmed);
            }
            return new KeyValue<>(value.getId(), value);
        });

        complete
                .groupBy((key, value) -> Integer.toString(key.hashCode() % 10)) // redistribute to restricted key-set
//                .groupByKey()
                .windowedBy(TimeWindows.of(ONE_DAY))
                .aggregate(
                        ConfirmedStats::new,
                        (key, value, aggregate) -> aggregate.update(value),
                        confirmedWindowStore
                );

        return completeStream;

    }
}

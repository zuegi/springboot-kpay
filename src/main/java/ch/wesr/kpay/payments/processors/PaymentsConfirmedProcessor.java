package ch.wesr.kpay.payments.processors;

import ch.wesr.kpay.config.KpayBindings;
import ch.wesr.kpay.payments.model.ConfirmedStats;
import ch.wesr.kpay.payments.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@SuppressWarnings("unchecked")
public class PaymentsConfirmedProcessor {

    public static final String STORE_NAME = "confirmed";
    long ONE_DAY = 24 * 60 * 60 * 1000L;

    private final Materialized<String, ConfirmedStats, WindowStore<Bytes, byte[]>> confirmedStore;
    private final Materialized<String, ConfirmedStats, WindowStore<Bytes, byte[]>> confirmedWindowStore;

    public PaymentsConfirmedProcessor(@Qualifier("valueConfirmedStatsJsonSerde") JsonSerde valueConfirmedStatsJsonSerde) {
        this.confirmedStore = Materialized.as(STORE_NAME);
        this.confirmedWindowStore = confirmedStore.withKeySerde(new Serdes.StringSerde()).withValueSerde(
                valueConfirmedStatsJsonSerde);
    }

    @StreamListener
    @SendTo(KpayBindings.PAYMENT_CONFIRMED_OUT)
    public KStream<String, Payment> process(@Input(KpayBindings.PAYMENT_COMPLETE) KStream<String, Payment> complete) {

        KStream<String, Payment> completeStream = complete.map((KeyValueMapper<String, Payment, KeyValue<String, Payment>>) (key, value) -> {
            if (value.getState() == Payment.State.complete) {
                value.setStateAndId(Payment.State.confirmed);
            }
            return new KeyValue<>(value.getId(), value);
        });

        completeStream
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

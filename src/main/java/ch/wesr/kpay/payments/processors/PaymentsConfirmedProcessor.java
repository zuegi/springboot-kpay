package ch.wesr.kpay.payments.processors;

import ch.wesr.kpay.config.KpayBindings;
import ch.wesr.kpay.payments.model.ConfirmedStats;
import ch.wesr.kpay.payments.model.InflightStats;
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

    Materialized<String, InflightStats, WindowStore<Bytes, byte[]>> inflightFirst;
    Materialized<String, InflightStats, WindowStore<Bytes, byte[]>> inflightWindowStore;


    public PaymentsConfirmedProcessor(@Qualifier("valueConfirmedStatsJsonSerde") JsonSerde valueConfirmedStatsJsonSerde, @Qualifier("valueInflightStatsJsonSerde") JsonSerde valueInflightStatsJsonSerde) {
        this.inflightFirst = Materialized.as(KpayBindings.STORE_NAME_INFLIGHT_METRICS);
        this.inflightWindowStore = inflightFirst.withKeySerde(new Serdes.StringSerde()).withValueSerde(
                valueInflightStatsJsonSerde);

        this.confirmedStore = Materialized.as(STORE_NAME);
        this.confirmedWindowStore = confirmedStore.withKeySerde(new Serdes.StringSerde()).withValueSerde(
                valueConfirmedStatsJsonSerde);
    }

    @StreamListener
    @SendTo(KpayBindings.PAYMENT_CONFIRMED_OUT)
    public KStream<String, Payment> process(@Input(KpayBindings.PAYMENT_COMPLETE) KStream<String, Payment> complete) {
        complete.foreach((key, value) -> log.info("Processing complete topic => Complete key {}", key));

        complete
                .groupBy((key, value) -> Integer.toString(key.hashCode() % 10))// reduce event key space for cross event aggregation
//               .groupByKey()
                .windowedBy(TimeWindows.of(ONE_DAY))
                .aggregate(
                        InflightStats::new,
                        (key, value, aggregate) -> aggregate.update(value),
                        inflightWindowStore
                );

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

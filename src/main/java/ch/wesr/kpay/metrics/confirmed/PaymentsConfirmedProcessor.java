package ch.wesr.kpay.metrics.confirmed;

import ch.wesr.kpay.config.KpayBindings;
import ch.wesr.kpay.metrics.confirmed.model.ConfirmedStats;
import ch.wesr.kpay.payments.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@SuppressWarnings("unchecked")
public class PaymentsConfirmedProcessor {

    long ONE_DAY = 24 * 60 * 60 * 1000L;

    private final Materialized<String, ConfirmedStats, WindowStore<Bytes, byte[]>> confirmedStore;
    private final Materialized<String, ConfirmedStats, WindowStore<Bytes, byte[]>> confirmedWindowStore;

    public PaymentsConfirmedProcessor(@Qualifier("valueConfirmedStatsJsonSerde") JsonSerde valueConfirmedStatsJsonSerde, @Qualifier("valueInflightStatsJsonSerde") JsonSerde valueInflightStatsJsonSerde) {
        this.confirmedStore = Materialized.as(KpayBindings.PAYMENT_CONFIRMED_STORE);
        this.confirmedWindowStore = confirmedStore.withKeySerde(new Serdes.StringSerde()).withValueSerde(
                valueConfirmedStatsJsonSerde);
    }


    @StreamListener
    public void process(@Input(KpayBindings.PAYMENT_CONFIRMED_INPUT) KStream<String, Payment> confirmedStream) {

        confirmedStream
                .groupBy((key, value) -> Integer.toString(key.hashCode() % 10)) // redistribute to restricted key-set
//                .groupByKey()
                .windowedBy(TimeWindows.of(ONE_DAY))
                .aggregate(
                        ConfirmedStats::new,
                        (key, value, aggregate) -> aggregate.update(value),
                        confirmedWindowStore
                );

    }
}

package ch.wesr.kpay.metrics.inflightstats;

import ch.wesr.kpay.config.KpayBindings;
import ch.wesr.kpay.metrics.inflightstats.model.InflightStats;
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
public class PaymentsInFlightStatsProcessor {


    private static long ONE_DAY = 24 * 60 * 60 * 1000L;

    Materialized<String, InflightStats, WindowStore<Bytes, byte[]>> inflightFirst;
    Materialized<String, InflightStats, WindowStore<Bytes, byte[]>> inflightWindowStore;

    public PaymentsInFlightStatsProcessor(@Qualifier("valueInflightStatsJsonSerde") JsonSerde valueInflightStatsJsonSerde) {
        this.inflightFirst = Materialized.as(KpayBindings.PAYMENT_INFLIGHT_STORE);
        this.inflightWindowStore = inflightFirst.withKeySerde(new Serdes.StringSerde()).withValueSerde(
                valueInflightStatsJsonSerde);
    }


    @StreamListener
    public void process(@Input (KpayBindings.PAYMENT_INFLIGHTSTATS_INPUT) KStream<String, Payment> paymentKStream) {

        paymentKStream
                .filter((key, value) -> (value.getState() == Payment.State.incoming || value.getState() == Payment.State.complete))
                //.groupBy((key, value) -> Integer.toString(key.hashCode() % 10))// reduce event key space for cross event aggregation
                .groupByKey()
                .windowedBy(TimeWindows.of(ONE_DAY))
                .aggregate(
                        InflightStats::new,
                        (key, value, aggregate) -> aggregate.update(value),
                        inflightWindowStore
                );

    }
}

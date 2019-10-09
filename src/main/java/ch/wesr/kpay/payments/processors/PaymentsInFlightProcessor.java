package ch.wesr.kpay.payments.processors;

import ch.wesr.kpay.config.KpayBindings;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentsInFlightProcessor {


    private static long ONE_DAY = 24 * 60 * 60 * 1000L;

    Materialized<String, InflightStats, WindowStore<Bytes, byte[]>> inflightFirst;
    Materialized<String, InflightStats, WindowStore<Bytes, byte[]>> inflightWindowStore;

    public PaymentsInFlightProcessor(@Qualifier("valueInflightStatsJsonSerde") JsonSerde valueInflightStatsJsonSerde) {
        this.inflightFirst = Materialized.as(KpayBindings.STORE_NAME_INFLIGHT_METRICS);
        this.inflightWindowStore = inflightFirst.withKeySerde(new Serdes.StringSerde()).withValueSerde(
                valueInflightStatsJsonSerde);
    }


    @StreamListener
    @SendTo(KpayBindings.PAYMENT_INFLIGHT_OUT)
    public KStream<String, Payment> process(@Input (KpayBindings.PAYMENT_INCOMING) KStream<String, Payment> paymentKStream) {

        paymentKStream
                .groupBy((key, value) -> Integer.toString(key.hashCode() % 10))// reduce event key space for cross event aggregation
//               .groupByKey()
                .windowedBy(TimeWindows.of(ONE_DAY))
                .aggregate(
                        InflightStats::new,
                        (key, value, aggregate) -> aggregate.update(value),
                        inflightWindowStore
                );
        /**
         * Data flow processing; flip incoming --> debit and filter complete events
         */
       return paymentKStream.map((KeyValueMapper<String, Payment, KeyValue<String, Payment>>) (key, value) -> {
            if (value.getState() == Payment.State.incoming) {
                value.setStateAndId(Payment.State.debit);
            }
            return new KeyValue<>(value.getId(), value);
        }).filter((key, value) -> value.getState() == Payment.State.debit);

    }
}

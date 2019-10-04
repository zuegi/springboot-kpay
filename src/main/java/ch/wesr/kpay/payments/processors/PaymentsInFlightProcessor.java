package ch.wesr.kpay.payments.processors;

import ch.wesr.kpay.payments.config.KpayBindings;
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
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentsInFlightProcessor {

    public static final String STORE_NAME = "inflight";
    private static long ONE_DAY = 24 * 60 * 60 * 1000L;

    Materialized<String, InflightStats, WindowStore<Bytes, byte[]>> inflightFirst = Materialized.as(STORE_NAME);
    Materialized<String, InflightStats, WindowStore<Bytes, byte[]>> inflightWindowStore = inflightFirst.withKeySerde(new Serdes.StringSerde()).withValueSerde(new InflightStats.Serde());

    @StreamListener
    @SendTo(KpayBindings.PAYMENT_INFLIGHT_OUT)
    public KStream<String, Payment> process(@Input (KpayBindings.PAYMENT_INCOMING) KStream<String, Payment> paymentKStream) {

        /*paymentKStream.foreach((key, value) -> {
            log.info("key: {}, value {}", key, value.toString());
        });*/

       paymentKStream
//                .groupBy((key, value) -> Integer.toString(key.hashCode() % 10))// reduce event key space for cross event aggregation
               .groupByKey()
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

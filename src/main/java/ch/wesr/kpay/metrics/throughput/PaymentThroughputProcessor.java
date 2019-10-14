package ch.wesr.kpay.metrics.throughput;

import ch.wesr.kpay.config.KpayBindings;
import ch.wesr.kpay.metrics.throughput.model.ThroughputStats;
import ch.wesr.kpay.payments.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentThroughputProcessor {
    private long processedLast;
    long ONE_MINUTE = 60 * 1000L;

    private KTable<Windowed<String>, ThroughputStats> statsKTable;
    private ThroughputStats stats = new ThroughputStats();
    private ThroughputStats lastStats = new ThroughputStats();

    /**
     * Data flow; emit the payments as Confirmed once they have been processed
     */
    private final Materialized<String, ThroughputStats, WindowStore<Bytes, byte[]>> completeStore;
    private final Materialized<String, ThroughputStats, WindowStore<Bytes, byte[]>> completeWindowStore;

    public PaymentThroughputProcessor(@Qualifier("valueThroughputsStatsJsonSerde") JsonSerde valueThroughputsStatsJsonSerde) {
        this.completeStore = Materialized.as(KpayBindings.PAYMENT_THROUGHPUT_STORE);
        this.completeWindowStore = completeStore.withKeySerde(new Serdes.StringSerde()).withValueSerde(valueThroughputsStatsJsonSerde);
    }

    //@StreamListener
    public void process(@Input(KpayBindings.PAYMENT_THROUGHPUT_INPUT) KStream<String, Payment> complete) {
        log.info("complete: " + complete);
        statsKTable = complete
                .filter((key, value) -> value.getState() == Payment.State.complete)
                .groupBy((key, value) -> "all-payments") // forces a repartition
                .windowedBy(TimeWindows.of(ONE_MINUTE))
                .aggregate(
                        ThroughputStats::new,
                        (key, value, aggregate) -> aggregate.update(value),
                        completeWindowStore
                );

        /**
         * Accumulate each window event internally by tracking the window as part of the aggregation
         */
        statsKTable.toStream().foreach((key, metricStats) -> {
            lastStats = metricStats;
            if (processedLast != key.window().end()) {
                stats = stats.merge(metricStats);
            }
            processedLast = key.window().end();
        });
    }

    /**
     * Expose statistics for rendering
     *
     * @return
     */
    public ThroughputStats getStats() {
        return stats.merge(lastStats);
    }
}

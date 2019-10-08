package ch.wesr.kpay.metrics.processors;

import ch.wesr.kpay.config.KpayBindings;
import ch.wesr.kpay.metrics.model.ThroughputStats;
import ch.wesr.kpay.payments.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.WindowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
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
    Materialized<String, ThroughputStats, WindowStore<Bytes, byte[]>> completeStore = Materialized.as("throughput");
    Materialized<String, ThroughputStats, WindowStore<Bytes, byte[]>> completeWindowStore = completeStore.withKeySerde(new Serdes.StringSerde()).withValueSerde(new ThroughputStats.Serde());

    @StreamListener
    public void process(@Input(KpayBindings.PAYMENT_COMPLETE_THROUGHPUT) KStream<String, Payment> complete) {
        statsKTable = complete
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
     * @return
     */
    public ThroughputStats getStats() {
        return stats.merge(lastStats);
    }
}
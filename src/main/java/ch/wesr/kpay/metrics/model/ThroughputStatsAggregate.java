package ch.wesr.kpay.metrics.model;


import ch.wesr.kpay.util.JsonDeserializer;
import ch.wesr.kpay.util.JsonSerializer;
import ch.wesr.kpay.util.WrapperSerde;
import lombok.extern.slf4j.Slf4j;

/**
 * Tracks aggregate view of per second instances of ThroughPutStatsPerSecond
 */
@Slf4j
public class ThroughputStatsAggregate {

    ThroughputStats stats;

    public ThroughputStatsAggregate update(ThroughputStats value) {
        log.debug("handle:{}" + value);
        if (stats == null) {
            stats = value;
        }

        stats.merge(value);
        return this;
    }

    static public final class Serde extends WrapperSerde<ThroughputStatsAggregate> {
        public Serde() {
            super(new JsonSerializer<>(), new JsonDeserializer(ThroughputStatsAggregate.class));
        }
    }
}

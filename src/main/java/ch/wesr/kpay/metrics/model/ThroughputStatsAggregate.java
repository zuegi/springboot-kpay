package ch.wesr.kpay.metrics.model;


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

}

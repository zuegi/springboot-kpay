package ch.wesr.kpay.rest;


import ch.wesr.kpay.config.KpayBindings;
import ch.wesr.kpay.metrics.model.ThroughputStats;
import ch.wesr.kpay.metrics.processors.PaymentThroughputProcessor;
import ch.wesr.kpay.payments.model.ConfirmedStats;
import ch.wesr.kpay.payments.model.InflightStats;
import ch.wesr.kpay.payments.model.Payment;
import ch.wesr.kpay.payments.processors.PaymentsConfirmedProcessor;
import ch.wesr.kpay.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    @Autowired
    PaymentThroughputProcessor paymentThroughputProcessor;

    @Autowired
    private InteractiveQueryService interactiveQueryService;

    @Autowired
    private WindowedKTableImpl windowedKTableImpl;

    @GetMapping("throughput")
    public ThroughputStats viewMetrics() {
        return paymentThroughputProcessor.getStats();
    }


    @GetMapping("pipeline")
    public Pair<InflightStats, ConfirmedStats> pipelineStats() {

        ReadOnlyWindowStore<String, InflightStats> paymentInflightStore = interactiveQueryService.getQueryableStore(KpayBindings.STORE_NAME_INFLIGHT_METRICS, QueryableStoreTypes.<String, InflightStats>windowStore());
        ReadOnlyWindowStore<String, ConfirmedStats> confirmedStore = interactiveQueryService.getQueryableStore(PaymentsConfirmedProcessor.STORE_NAME, QueryableStoreTypes.<String, ConfirmedStats>windowStore());
        if (paymentInflightStore != null && confirmedStore != null) {

            List<Pair<String, InflightStats>> inflightStats = this.windowedKTableImpl.get(paymentInflightStore, new ArrayList<>(this.windowedKTableImpl.keySet(paymentInflightStore)));

            inflightStats.sort(new Comparator<Pair<String, InflightStats>>() {
                @Override
                public int compare(Pair<String, InflightStats> o1, Pair<String, InflightStats> o2) {
                    return Long.compare(o2.getV().getTimestamp(), o1.getV().getTimestamp());
                }
            });

            List<Pair<String, ConfirmedStats>> confirmedStats = this.windowedKTableImpl.get(confirmedStore, new ArrayList<>(this.windowedKTableImpl.keySet(confirmedStore)));
            confirmedStats.sort(new Comparator<Pair<String, ConfirmedStats>>() {
                @Override
                public int compare(Pair<String, ConfirmedStats> o1, Pair<String, ConfirmedStats> o2) {
                    return Long.compare(o2.getV().getTimestamp(), o1.getV().getTimestamp());
                }
            });

            if (inflightStats.size() == 0 || confirmedStats.size() == 0) {
                InflightStats inflightStats1 = new InflightStats();
                inflightStats1.setTimestamp(System.currentTimeMillis());
                ConfirmedStats confirmedStats1 = new ConfirmedStats();
                confirmedStats1.setTimestamp(System.currentTimeMillis());
                return new Pair<>(inflightStats1, confirmedStats1);
            }

            Iterator<Pair<String, InflightStats>> iterator = inflightStats.iterator();
            InflightStats
                    inflightStatsValue = iterator.next().getV();
            while (iterator.hasNext()) {
                inflightStatsValue.add(iterator.next().getV());
            }

            Iterator<Pair<String, ConfirmedStats>> confirmedIterator = confirmedStats.iterator();
            ConfirmedStats confirmedStatsValue = confirmedIterator.next().getV();
            while (confirmedIterator.hasNext()) {
                confirmedStatsValue.add(confirmedIterator.next().getV());
            }

            // FIXME Fake timestamp
            //inflightStatsValue.setTimestamp(confirmedStatsValue.getTimestamp());

            return new Pair<>(inflightStatsValue, confirmedStatsValue);
        }
        return null;
    }

}

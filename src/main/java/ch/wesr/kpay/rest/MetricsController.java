package ch.wesr.kpay.rest;


import ch.wesr.kpay.metrics.model.ThroughputStats;
import ch.wesr.kpay.metrics.processors.PaymentThroughputProcessor;
import ch.wesr.kpay.payments.model.ConfirmedStats;
import ch.wesr.kpay.payments.model.InflightStats;
import ch.wesr.kpay.payments.model.Payment;
import ch.wesr.kpay.payments.processors.PaymentsConfirmedProcessor;
import ch.wesr.kpay.payments.processors.PaymentsInFlightProcessor;
import ch.wesr.kpay.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    @Autowired
    PaymentThroughputProcessor paymentThroughputProcessor;

    @Autowired
    private InteractiveQueryService interactiveQueryService;

    @Autowired
    private WindowedKTableResource windowedKTableResource;

    @GetMapping("throughput")
    public ThroughputStats viewMetrics() {
        return paymentThroughputProcessor.getStats();
    }


    @GetMapping("getAllTroughputs")
    public ReadOnlyKeyValueStore<String, Payment> getAllMetrics() {
        return interactiveQueryService.getQueryableStore("throughput", QueryableStoreTypes.<String, Payment>keyValueStore());
    }

    @GetMapping("pipeline")
    public Pair<InflightStats, ConfirmedStats> pipelineStats() {

        ReadOnlyWindowStore<String, Payment> paymentInflightStore = interactiveQueryService.getQueryableStore(PaymentsInFlightProcessor.STORE_NAME, QueryableStoreTypes.<String, Payment>windowStore());
        List<Pair<String, InflightStats>> inflightStats = this.windowedKTableResource.get(paymentInflightStore, new ArrayList<>(this.windowedKTableResource.keySet(paymentInflightStore)));

        ReadOnlyWindowStore<String, Payment> confirmedStore = interactiveQueryService.getQueryableStore(PaymentsConfirmedProcessor.STORE_NAME, QueryableStoreTypes.<String, Payment>windowStore());
        List<Pair<String, ConfirmedStats>> confirmedStats = this.windowedKTableResource.get(confirmedStore, new ArrayList<>(this.windowedKTableResource.keySet(confirmedStore)));

        if (inflightStats.size() == 0 || confirmedStats.size() == 0) return new Pair<>(new InflightStats(), new ConfirmedStats());

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

        return new Pair<>(inflightStatsValue, confirmedStatsValue);
    }

}

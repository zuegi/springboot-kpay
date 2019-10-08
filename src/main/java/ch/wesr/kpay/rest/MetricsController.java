package ch.wesr.kpay.rest;


import ch.wesr.kpay.metrics.model.ThroughputStats;
import ch.wesr.kpay.metrics.processors.PaymentThroughputProcessor;
import ch.wesr.kpay.payments.model.ConfirmedStats;
import ch.wesr.kpay.payments.model.InflightStats;
import ch.wesr.kpay.payments.model.Payment;
import ch.wesr.kpay.util.Pair;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    @Autowired
    PaymentThroughputProcessor paymentThroughputProcessor;

    @Autowired
    private InteractiveQueryService interactiveQueryService;

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


        // FIXME return a value not null
        return new Pair<InflightStats, ConfirmedStats>();
    }
}

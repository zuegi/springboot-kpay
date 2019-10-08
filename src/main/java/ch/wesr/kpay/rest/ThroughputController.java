package ch.wesr.kpay.rest;


import ch.wesr.kpay.metrics.model.ThroughputStats;
import ch.wesr.kpay.metrics.processors.PaymentThroughputProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metrics")
public class ThroughputController {

    @Autowired
    PaymentThroughputProcessor paymentThroughputProcessor;

    @GetMapping("throughput")
    public ThroughputStats viewMetrics() {
        return paymentThroughputProcessor.getStats();
    }
}

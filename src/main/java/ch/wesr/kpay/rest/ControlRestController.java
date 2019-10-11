package ch.wesr.kpay.rest;

import ch.wesr.kpay.payments.PaymentsIncomingProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@RestController
@RequestMapping("/api/control")
public class ControlRestController {

    private static final long FIXED_RATE = 100L;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private PaymentsIncomingProducer paymentsIncomingProducer;

    private ScheduledFuture<?> scheduledFuture;

    @GetMapping("paymentProducer/start")
    public ResponseEntity<Void> startScheduledPaymentsIncomingProducer() {
        scheduledFuture = taskScheduler.scheduleAtFixedRate(paymentsIncomingProducer.paymentProducer(), FIXED_RATE);
        log.info("PaymentsIncomingProducer has been startet");
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @GetMapping("paymentProducer/stop")
    public ResponseEntity<Void> stopScheduledPaymentIncomingProducer() {
        scheduledFuture.cancel(false);
        log.info("PaymentsIncomingProducer has been stopped");
        return new ResponseEntity<Void>(HttpStatus.OK);
    }



}

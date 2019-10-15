package ch.wesr.kpay.rest;

import ch.wesr.kpay.payments.PaymentProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ScheduledFuture;

@Slf4j
@RestController
@RequestMapping("/api/control")
public class ControlRestController {


    @Value("${kpay.scheduled.paymentsIncomingProducer.fixedRate}")
    private long fixedRate;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private PaymentProducer paymentProducer;

    private ScheduledFuture<?> scheduledFuture;

    @GetMapping("paymentProducer/running")
    public Boolean isScheduledPaymentsIncomingProducerRunning() {
       return isRunning();
    }

    @GetMapping("paymentProducer/start")
    public ResponseEntity<Void> startScheduledPaymentsIncomingProducer() {
        return startProducer();
    }

    private ResponseEntity<Void> startProducer() {
        return start();
    }

    private ResponseEntity<Void> start() {
        if(!isRunning()) {
            scheduledFuture = taskScheduler.scheduleAtFixedRate(paymentProducer.paymentProducer(), fixedRate);
            log.debug("PaymentsIncomingProducer has been startet");
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        throw new IllegalArgumentException("Scheduler already running");
    }

    @GetMapping("paymentProducer/stop")
    public ResponseEntity<Void> stopScheduledPaymentIncomingProducer() {
        return stop();
    }

    @GetMapping("paymentProducer/adjustRate/{newRate}")
    public synchronized void adjustRate(@PathVariable("newRate") Long newRate) {
            this.fixedRate = newRate;
            if (isRunning()) { stop(); }
            start();
    }

    private ResponseEntity<Void> stop() {
        if(isRunning()) {
            scheduledFuture.cancel(false);
            log.debug("PaymentsIncomingProducer has been stopped");
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        throw new IllegalArgumentException("Scheduler not running");
    }


    public Boolean isRunning() {
        return this.scheduledFuture != null && !this.scheduledFuture.isCancelled() && !this.scheduledFuture.isDone();
    }


}

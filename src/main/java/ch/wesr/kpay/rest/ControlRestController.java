package ch.wesr.kpay.rest;

import ch.wesr.kpay.payments.PaymentsIncomingProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ScheduledFuture;

@Slf4j
@RestController
@RequestMapping("/api/control")
public class ControlRestController {


    @Value("${kpay.scheduled.paymentsIncomingProducer.fixedRate}")
    private long FIXED_RATE;

    private Boolean running = false;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private PaymentsIncomingProducer paymentsIncomingProducer;

    private ScheduledFuture<?> scheduledFuture;

    @GetMapping("paymentProducer/running")
    public Boolean isScheduledPaymentsIncomingProducerRunning() {
        log.info("isRunning called: {}", isRunning());
       return isRunning();
    }

    @GetMapping("paymentProducer/start")
    public ResponseEntity<Void> startScheduledPaymentsIncomingProducer() {
        if(!isRunning()) {
            scheduledFuture = taskScheduler.scheduleAtFixedRate(paymentsIncomingProducer.paymentProducer(), FIXED_RATE);
            log.info("PaymentsIncomingProducer has been startet");
            setRunning(true);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        throw new IllegalArgumentException("Scheduler already running");
    }

    @GetMapping("paymentProducer/stop")
    public ResponseEntity<Void> stopScheduledPaymentIncomingProducer() {
        if(isRunning()) {
            scheduledFuture.cancel(false);
            log.info("PaymentsIncomingProducer has been stopped");
            setRunning(false);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        throw new IllegalArgumentException("Scheduler not running");
    }


    public Boolean isRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }
}

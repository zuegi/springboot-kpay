package ch.wesr.kpay.payments;

import ch.wesr.kpay.config.KpayBindings;
import ch.wesr.kpay.payments.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PaymentsIncomingSource  {

    @Value("${kpay.executors.corePoolSize}")
    private int corePoolSize;

    @Value("${kpay.executors.initialDelay}")
    private int initalDelay;

    @Value("${kpay.executors.period}")
    private int period;

    @Autowired
    private PaymentSourceMessagingGateway paymentSourceMessagingGateway;

    public PaymentsIncomingSource(KpayBindings bindings) {
      //  this.paymentSource = bindings.paymentIncomingOut();
    }

    @Scheduled(fixedRate = 1000, initialDelay = 1000)
    public void paymentProducer() {
        List<String> froms = Arrays.asList("peter", "ueli", "simon", "carsten", "lars", "phong", "rene");
        String from = froms.get(new Random().nextInt(froms.size()));
        String to = froms.get(new Random().nextInt(froms.size()));
        String txnId = RandomStringUtils.random(10, true, true);
        BigDecimal bigDecimal = BigDecimalGenerator.get("10.00", "150.00");

        Payment payment = new Payment(txnId, "id", from, to, bigDecimal, Payment.State.incoming, System.currentTimeMillis());
        log.info("Sent message: " + payment);
        this.paymentSourceMessagingGateway.publishPayment(payment, payment.getTxnId().getBytes());
    }
}


class BigDecimalGenerator {

    public static BigDecimal get(String minString, String maxString) {
        BigDecimal min = new BigDecimal(minString);
        BigDecimal max = new BigDecimal(maxString);
        BigDecimal range = max.subtract(min);
        return min.add(range.multiply(new BigDecimal(Math.random())));
    }
}

package ch.wesr.kpay;

import ch.wesr.kpay.payments.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.xml.stream.XMLInputFactory;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
@EnableBinding(KpayBindings.class)
@Slf4j
public class KpayApplication {

    @Component
    public static class PaymentsIncomingSource implements ApplicationRunner {
        private final MessageChannel paymentIncoming;

        public PaymentsIncomingSource(KpayBindings bindings) {
            this.paymentIncoming = bindings.paymentIncoming();
        }


        @Override
        public void run(ApplicationArguments args) throws Exception {
            List<String> froms = Arrays.asList("peter", "ueli", "simon", "carsten", "lars", "phong", "rene");

            Runnable runnable = () -> {
                String from = froms.get(new Random().nextInt(froms.size()));
                String to = froms.get(new Random().nextInt(froms.size()));
                String txnId = RandomStringUtils.random(10, true, true);
                BigDecimal bigDecimal = BigDecimalGenerator.get("10.00", "150.00");

                Payment payment = new Payment(txnId, "id", from, to, bigDecimal, Payment.State.complete, System.currentTimeMillis());

                Message<Payment> message = MessageBuilder
                        .withPayload(payment)
                        .setHeader(KafkaHeaders.MESSAGE_KEY, payment.getTxnId().getBytes())
                        .build();
                try {
                    this.paymentIncoming.send(message);
                    log.info("Sent message: " + message.toString());
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            };
            Executors.newScheduledThreadPool(1).scheduleAtFixedRate(runnable, 1, 1, TimeUnit.SECONDS);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(KpayApplication.class, args);
    }

}

class BigDecimalGenerator {

    public static BigDecimal get(String minString, String maxString ) {
        BigDecimal min = new BigDecimal(minString);
        BigDecimal max = new BigDecimal(maxString);
        BigDecimal range = max.subtract(min);
        return min.add(range.multiply(new BigDecimal(Math.random())));
    }
}

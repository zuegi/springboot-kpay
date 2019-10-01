package ch.wesr.kpay;

import ch.wesr.kpay.payments.model.Payment;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@SpringBootApplication
@EnableBinding(KpayBindings.class)
public class KpayApplication {

    @Component
    public static class PaymentsIncomingSource implements ApplicationRunner {
        private final MessageChannel paymentIncoming;

        public PaymentsIncomingSource(KpayBindings bindings) {
            this.paymentIncoming = bindings.paymentIncoming();
        }


        @Override
        public void run(ApplicationArguments args) throws Exception {
            // synthesize data
            Payment payment = new Payment( "id","txnId", "from","to", new BigDecimal(123.0), Payment.State.complete, System.currentTimeMillis());

            MessageBuilder
                    .withPayload(payment)
                    .setHeader(KafkaHeaders.MESSAGE_KEY, payment. // was ist der message key)
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(KpayApplication.class, args);
    }

}

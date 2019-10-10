package ch.wesr.kpay.payments;

import ch.wesr.kpay.payments.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PaymentsIncomingSource  {


    @Autowired
    private PaymentSourceMessagingGateway paymentSourceMessagingGateway;

    @Scheduled(fixedRate = 100, initialDelay = 1000)
    public void paymentProducer() {
        List<String> froms = Arrays.asList("peter", "ueli", "simon", "carsten", "lars", "phong", "rene");
        String from = froms.get(new Random().nextInt(froms.size()));
        List<String> dynamicsTos = froms.stream().filter(s -> s.compareTo(from) != 0).collect(Collectors.toList());
        String to = dynamicsTos.get(new Random().nextInt(dynamicsTos.size()));
        BigDecimal bigDecimal = BigDecimalGenerator.get("5.00", "30.00");

        Payment payment = new Payment("pay-" + System.currentTimeMillis(), System.currentTimeMillis() + "", from, to, new BigDecimal(Math.round((Math.random() * 100.0)*100.0)/100.0).setScale(2, RoundingMode.CEILING), Payment.State.incoming, System.currentTimeMillis() );
        log.info("Sent message: " + payment);
        this.paymentSourceMessagingGateway.publishPayment(payment, payment.getTxnId().getBytes());
    }
}


class BigDecimalGenerator {

    public static BigDecimal get(String minString, String maxString) {
        BigDecimal min = new BigDecimal(minString);
        BigDecimal max = new BigDecimal(maxString);
        BigDecimal range = max.subtract(min);
        BigDecimal amount = min.add(range.multiply(new BigDecimal(Math.random())));
        amount = amount.setScale(2, RoundingMode.CEILING);
        return amount;
    }
}

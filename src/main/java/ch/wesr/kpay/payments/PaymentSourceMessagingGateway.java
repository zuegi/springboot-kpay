package ch.wesr.kpay.payments;

import ch.wesr.kpay.config.KpayBindings;
import ch.wesr.kpay.payments.model.Payment;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

@MessagingGateway
public interface PaymentSourceMessagingGateway {

    @Gateway(requestChannel = KpayBindings.PAYMENT_INCOMING_OUT)
    public void publishPayment(@Payload Payment payment, @Header(KafkaHeaders.MESSAGE_KEY) byte[] key);
}

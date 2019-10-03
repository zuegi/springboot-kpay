package ch.wesr.kpay.payments.config;

import ch.wesr.kpay.KpayBindings;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding({KpayBindings.class})
public class KpayConfiguration {
}

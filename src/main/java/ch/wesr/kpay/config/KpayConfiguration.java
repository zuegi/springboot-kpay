package ch.wesr.kpay.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBinding({KpayBindings.class})
public class KpayConfiguration {
}
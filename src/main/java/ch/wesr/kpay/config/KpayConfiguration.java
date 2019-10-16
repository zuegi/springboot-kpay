package ch.wesr.kpay.config;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.CleanupConfig;

@Configuration
@EnableBinding({KpayBindings.class})
public class KpayConfiguration {

    @Bean
    public CleanupConfig cleanupConfig() {
        return new CleanupConfig(true, true);
    }
}

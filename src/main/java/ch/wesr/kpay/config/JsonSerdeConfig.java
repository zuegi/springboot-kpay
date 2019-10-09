package ch.wesr.kpay.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.serializer.JsonSerde;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class JsonSerdeConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);

        return objectMapper;
    }

    @Bean
    @Qualifier("valuePaymentJsonSerde")
    public JsonSerde valuePaymentJsonSerde(ObjectMapper objectMapper) {
        JsonSerde jsonSerde = new JsonSerde(objectMapper);
        Map<String, String> configMap = new HashMap<>();
        configMap.put("spring.json.value.default.type", "ch.wesr.kpay.payments.model.Payment");
        jsonSerde.configure(configMap, false);
        return jsonSerde;
    }

    @Bean
    @Qualifier("valueInflightStatsJsonSerde")
    public JsonSerde valueInflightStatsJsonSerde(ObjectMapper objectMapper) {
        JsonSerde jsonSerde = new JsonSerde(objectMapper);
        Map<String, String> configMap = new HashMap<>();
        configMap.put("spring.json.value.default.type", "ch.wesr.kpay.payments.model.InflightStats");
        jsonSerde.configure(configMap, false);
        return jsonSerde;
    }

    @Bean
    @Qualifier("valueAccountBalanceJsonSerde")
    public JsonSerde valueAccountBalanceJsonSerde(ObjectMapper objectMapper) {
        JsonSerde jsonSerde = new JsonSerde(objectMapper);
        Map<String, String> configMap = new HashMap<>();
        configMap.put("spring.json.value.default.type", "ch.wesr.kpay.payments.model.AccountBalance");
        jsonSerde.configure(configMap, false);
        return jsonSerde;
    }

    @Bean
    @Qualifier("valueThroughputsStatsJsonSerde")
    public JsonSerde valueThroughputsStatsJsonSerde(ObjectMapper objectMapper) {
        JsonSerde jsonSerde = new JsonSerde(objectMapper);
        Map<String, String> configMap = new HashMap<>();
        configMap.put("spring.json.value.default.type", "ch.wesr.kpay.metrics.model.ThroughputStats");
        jsonSerde.configure(configMap, false);
        return jsonSerde;
    }

    @Bean
    @Qualifier("valueConfirmedStatsJsonSerde")
    public JsonSerde valueConfirmedStatsJsonSerde(ObjectMapper objectMapper) {
        JsonSerde jsonSerde = new JsonSerde(objectMapper);
        Map<String, String> configMap = new HashMap<>();
        configMap.put("spring.json.value.default.type", "ch.wesr.kpay.payments.model.ConfirmedStats");
        jsonSerde.configure(configMap, false);
        return jsonSerde;
    }
}

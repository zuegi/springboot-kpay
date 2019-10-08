package ch.wesr.kpay.payments.model;

import ch.wesr.kpay.util.JsonDeserializer;
import ch.wesr.kpay.util.JsonSerializer;
import ch.wesr.kpay.util.WrapperSerde;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InflightStats {

    private int count;
    private BigDecimal amount = new BigDecimal(0);
    private long timestamp;

    public InflightStats update(Payment value) {
        log.debug(" InflightStats. update, processing:{} current:{} state:{}", value, this.amount, value.getState());

        this.timestamp = System.currentTimeMillis();
        if (value.getState() == Payment.State.incoming) {
            // accumulate on 'incoming' payment
            this.amount = this.amount.add(value.getAmount());
            this.count++;
        } else if (value.getState() == Payment.State.complete) {
            // remove 'complete'd payments
            this.amount = this.amount.subtract(value.getAmount());
            this.count--;
        }
        return this;
    }

    @Override
    public String toString() {
        return "InflightStats{" +
                "count=" + count +
                ", amount=" + amount +
                '}';
    }

    public void add(InflightStats other) {
        this.amount.add(other.amount);
        this.count += other.count;
    }

    static public final class Serde extends WrapperSerde<InflightStats> {
        public Serde() {
            super(new JsonSerializer<>(), new JsonDeserializer(InflightStats.class));
        }
    }
}

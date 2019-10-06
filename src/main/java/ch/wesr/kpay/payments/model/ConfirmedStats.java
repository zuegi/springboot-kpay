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
public class ConfirmedStats {

    private int count;
    private BigDecimal amount = new BigDecimal(0);
    private long timestamp;

    public ConfirmedStats update(Payment value) {

        this.timestamp = System.currentTimeMillis();
        log.debug("handle:{}" + value);
        if (value.getState() == Payment.State.confirmed) {
            // remove 'complete'd payments
            this.amount = this.amount.add(value.getAmount());
            this.count++;
        } else {
            // log error
        }
        return this;
    }

    @Override
    public String toString() {
        return "ConfirmedStats{" +
                "count=" + count +
                ", amount=" + amount +
                '}';
    }

    public void add(ConfirmedStats other) {
        this.count += other.getCount();
        this.amount.add(other.getAmount());
    }

    static public final class Serde extends WrapperSerde<ConfirmedStats> {
        public Serde() {
            super(new JsonSerializer<>(), new JsonDeserializer(ConfirmedStats.class));
        }
    }
}
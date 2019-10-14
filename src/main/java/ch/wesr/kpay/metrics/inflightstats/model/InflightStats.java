package ch.wesr.kpay.metrics.inflightstats.model;

import ch.wesr.kpay.payments.model.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InflightStats {

    private int count;
    private BigDecimal amount = new BigDecimal(0).setScale(2, RoundingMode.CEILING);
    private long timestamp;

    public InflightStats update(Payment value) {
        log.info(" InflightStats. update, processing:{} current amount:{} state:{}", value, this.amount, value.getState());

        this.timestamp = System.currentTimeMillis();
        if (value.getState() == Payment.State.incoming) {
            // accumulate on 'incoming' payment
            this.amount = this.amount.add(value.getAmount()).setScale(2, RoundingMode.CEILING);
            this.count++;
        } else if (value.getState() == Payment.State.complete) {
            // remove 'complete'd payments
            this.amount = this.amount.subtract(value.getAmount()).setScale(2, RoundingMode.CEILING);
            this.count--;
        }
        log.info(" InflightStats. update completed, processing:{} current amount:{} state:{}", value, this.amount, value.getState());

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
        this.amount = this.amount.add(other.amount).setScale(2, RoundingMode.CEILING);
        this.count += other.count;
    }

}

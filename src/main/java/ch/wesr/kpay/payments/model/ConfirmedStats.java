package ch.wesr.kpay.payments.model;

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
public class ConfirmedStats {

    private int count;
    private BigDecimal amount = new BigDecimal(0).setScale(2, RoundingMode.CEILING);
    private long timestamp;

    public ConfirmedStats update(Payment value) {

        this.timestamp = System.currentTimeMillis();
        log.debug("handle:{}" + value);
        if (value.getState() == Payment.State.confirmed) {
            log.debug("Payment.State: {}", value.getState());
            // remove 'complete'd payments
            this.amount = this.amount.add(value.getAmount()).setScale(2, RoundingMode.CEILING);
            this.count++;
        } else {
            // log error
            log.error("PaymentState not confirmed: [{}]", value.getState());
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
        this.amount = this.amount.add(other.getAmount()).setScale(2, RoundingMode.CEILING);
    }


}

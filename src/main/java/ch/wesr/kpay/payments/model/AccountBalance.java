package ch.wesr.kpay.payments.model;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Slf4j
public class AccountBalance {

    private String name;

    private Payment lastPayment;
    private BigDecimal amount = new BigDecimal(0).setScale(2, RoundingMode.CEILING);
    private BigDecimal lastAmount = new BigDecimal(0).setScale(2, RoundingMode.CEILING);

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastPayment(Payment lastPayment) {
        this.lastPayment = lastPayment;
    }

    public BigDecimal getAmount() {
        return amount.setScale(2, RoundingMode.CEILING);
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount.setScale(2, RoundingMode.CEILING);
    }

    public AccountBalance handle(String key, Payment value) {

        this.name = value.getId();
        this.lastAmount = this.amount;

        if (value.getState() == Payment.State.debit) {
            this.amount = this.amount.subtract(value.getAmount().setScale(2, RoundingMode.CEILING));
        } else if (value.getState() == Payment.State.credit) {
            this.amount = this.amount.add(value.getAmount()).setScale(2, RoundingMode.CEILING);
        } else {
            // report to dead letter queue via exception handler
            throw new RuntimeException("Invalid payment received:" + value);
        }
        log.debug("      id: {} amount: {}", this.name, this.amount.doubleValue());

        this.lastPayment = value;
        return this;
    }


    public Payment getLastPayment() {
        return lastPayment;
    }


    @Override
    public String toString() {
        return "AccountBalance{" +
                "name='" + name + '\'' +
                ", amount=" + amount +
                '}';
    }


    public BigDecimal getLastAmount() {
        return lastAmount;
    }

    public void setLastAmount(BigDecimal lastAmount) {
        this.lastAmount = lastAmount;
    }
}

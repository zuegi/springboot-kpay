package ch.wesr.kpay.metrics.throughput.model;

import ch.wesr.kpay.payments.model.Payment;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * Tracks per second metrics
 * - totalPayments number of events per second
 * - totalPayments dollar amount
 * - latency min/max/avg (getElapsedMillis)
 */
@Slf4j
public class ThroughputStats {

    private int totalPayments;
    private int throughputPerWindow;
    private BigDecimal totalDollarAmount = new BigDecimal(0);
    private long minLatency = Long.MAX_VALUE;
    private long maxLatency;
    private Payment largestPayment;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    private long timestamp;

    public ThroughputStats update(Payment payment) {
        totalPayments++;
        totalDollarAmount = totalDollarAmount.add(payment.getAmount());
        maxLatency = Math.max(maxLatency, payment.getElapsedMillis());
        minLatency = Math.min(minLatency, payment.getElapsedMillis());
        if (largestPayment == null || payment.getAmount().doubleValue() > largestPayment.getAmount().doubleValue()) {
            largestPayment = payment;
        }
        timestamp = System.currentTimeMillis();
        return this;
    }

    public ThroughputStats merge(ThroughputStats otherStats) {
        ThroughputStats result = new ThroughputStats();
        result.throughputPerWindow =  Math.max(this.throughputPerWindow, otherStats.throughputPerWindow);
        result.totalPayments += otherStats.totalPayments;
        result.largestPayment = this.largestPayment != null && this.largestPayment.getAmount().doubleValue() > otherStats.largestPayment.getAmount().doubleValue() ? this.largestPayment : otherStats.largestPayment;
        result.totalDollarAmount = result.totalDollarAmount.add(otherStats.totalDollarAmount);
        result.minLatency = Math.min(this.minLatency, otherStats.minLatency);
        result.maxLatency = Math.max(this.maxLatency, otherStats.maxLatency);
        result.timestamp = System.currentTimeMillis();
        return result;
    }


    @Override
    public String toString() {
        return "ThroughputStats{" +
                "totalPayments=" + totalPayments +
                ", throughputPerWindow=" + throughputPerWindow +
                ", totalDollarAmount=" + totalDollarAmount.doubleValue() +
                ", minLatency=" + minLatency +
                ", maxLatency=" + maxLatency +
                ", largestPayment=" + largestPayment +
                '}';
    }

    public void setTotalPayments(int totalPayments) {
        this.totalPayments = totalPayments;
    }

    public void setThroughputPerWindow(int throughputPerWindow) {
        this.throughputPerWindow = throughputPerWindow;
    }

    public void setTotalDollarAmount(BigDecimal totalDollarAmount) {
        this.totalDollarAmount = totalDollarAmount;
    }

    public void setMinLatency(long minLatency) {
        this.minLatency = minLatency;
    }

    public void setMaxLatency(long maxLatency) {
        this.maxLatency = maxLatency;
    }

    public void setLargestPayment(Payment largestPayment) {
        this.largestPayment = largestPayment;
    }

    public int getTotalPayments() {
        return totalPayments;
    }

    public int getThroughputPerWindow() {
        return throughputPerWindow;
    }

    public BigDecimal getTotalDollarAmount() {
        return totalDollarAmount;
    }

    public long getMinLatency() {
        return minLatency;
    }

    public long getMaxLatency() {
        return maxLatency;
    }

    public Payment getLargestPayment() {
        return largestPayment;
    }

}

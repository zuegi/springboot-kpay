package ch.wesr.kpay.payments.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    public enum State {incoming, debit, credit, complete, confirmed};

    private String id;
    private String txnId;
    private String from;
    private String to;
    private BigDecimal amount;
    private State state;

    private long timestamp;
    private long processStartTime;

    public Payment(String txnId, String id, String from, String to, BigDecimal amount, State state, long timestamp){
        this.txnId = txnId;
        this.id = id;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.state = state;
        this.timestamp = timestamp;
    }

    public State getState() {
        return state;
    }

    /**
     * When changing state we need to rekey to the correct 'id' for debit and credit account processor instances so they are processed by the correct instance.
     * Upon completion need to rekey back to the txnId
     * @param state
     */
    public void setState(State state) {
        this.state = state;
    }

    public void setStateAndId(State state) {
        this.state = state;
        if (state == State.credit) {
            id = to;
        } else if (state == State.debit) {
            this.processStartTime = System.currentTimeMillis();
            id = from;
        } else {
            id = txnId;
        }
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id='" + id + '\'' +
                ", txnId='" + txnId + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", amount=" + amount.doubleValue() +
                ", state=" + getState() +
                ", timestamp=" + getTimestamp() +
                '}';
    }


    public long getElapsedMillis(){
        return System.currentTimeMillis() - this.processStartTime;
    }

}

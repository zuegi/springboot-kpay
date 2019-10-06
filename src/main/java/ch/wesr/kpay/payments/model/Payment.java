package ch.wesr.kpay.payments.model;


import ch.wesr.kpay.util.JsonDeserializer;
import ch.wesr.kpay.util.JsonSerializer;
import ch.wesr.kpay.util.WrapperSerde;
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
    private int state;

    private long timestamp;
    private long processStartTime;

    public Payment(String txnId, String id, String from, String to, BigDecimal amount, State state, long timestamp){
        this.txnId = txnId;
        this.id = id;
        this.from = from;
        this.to = to;
        this.amount = amount;
        this.state = state.ordinal();
        this.timestamp = timestamp;
    }

    public State getState() {
        return State.values()[state];
    }

    /**
     * When changing state we need to rekey to the correct 'id' for debit and credit account processor instances so they are processed by the correct instance.
     * Upon completion need to rekey back to the txnId
     * @param state
     */
    public void setState(State state) {
        this.state = state.ordinal();
    }

    public void setStateAndId(State state) {
        this.state = state.ordinal();
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
                '}';
    }


    public long getElapsedMillis(){
        return System.currentTimeMillis() - this.processStartTime;
    }

    static public final class Serde extends WrapperSerde<Payment> {
        public Serde() {
            super(new JsonSerializer<>(), new JsonDeserializer<>(Payment.class));
        }
    }

}

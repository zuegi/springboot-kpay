package ch.wesr.kpay.rest;

import ch.wesr.kpay.util.Pair;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;

import java.util.List;
import java.util.Set;

public interface WindowedKTable<K,V> {

    Set<K> keySet(ReadOnlyWindowStore store);

    List<Pair<K,V>> get(ReadOnlyWindowStore store, List<K> query);

}

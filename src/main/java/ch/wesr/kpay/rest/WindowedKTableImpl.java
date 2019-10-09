package ch.wesr.kpay.rest;

import ch.wesr.kpay.payments.model.InflightStats;
import ch.wesr.kpay.payments.model.Payment;
import ch.wesr.kpay.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Windowed;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.ReadOnlyWindowStore;
import org.springframework.stereotype.Component;

import java.util.*;


@Slf4j
@Component
public class WindowedKTableImpl<K,V> implements WindowedKTable<K, V> {

    @Override
    public Set<K> keySet(ReadOnlyWindowStore store) {
        HashSet<K> results = new HashSet<>();
        KeyValueIterator<Windowed<K>, V> all = store.all();
        try {
            while (all.hasNext()) {
                KeyValue<Windowed<K>, V> next = all.next();
                results.add(next.key.key());
            }
        } finally {
            all.close();
        }
        return results;
    }

    @Override
    public List<Pair> get(ReadOnlyWindowStore store, List query) {
        Set<Pair<K, V>> results = new HashSet<>();
        KeyValueIterator<Windowed<K>, V> all = store.all();
        try {
            // TODO: fix me - yuck
            while (all.hasNext()) {
                KeyValue<Windowed<K>, V> next = all.next();
                if (query.contains(next.key.key())) {
                    results.add(new Pair(next.key.key(), next.value));
                    log.debug("next Value: {}", next.value);
                }
            }
        } finally {
            all.close();
        }
        return new ArrayList<>(results);

    }


}

package ch.wesr.kpay.rest;

import ch.wesr.kpay.payments.model.Payment;
import ch.wesr.kpay.payments.processors.AccountProcessor;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AccountRestController {


    @Autowired
    private InteractiveQueryService interactiveQueryService;


    @GetMapping("listAccounts")
    public KeyValueIterator<String, Payment> listAccounts(){

        ReadOnlyKeyValueStore<String, Payment> paymentStore = interactiveQueryService.getQueryableStore(AccountProcessor.STORE_NAME, QueryableStoreTypes.<String, Payment>keyValueStore());

        if (paymentStore != null) {
            return paymentStore.all();
        }
        return null;
    }
}

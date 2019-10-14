package ch.wesr.kpay.rest;

import ch.wesr.kpay.config.KpayBindings;
import ch.wesr.kpay.payments.model.AccountBalance;
import ch.wesr.kpay.payments.model.Payment;
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
    public KeyValueIterator<String, AccountBalance> listAccounts(){

        ReadOnlyKeyValueStore<String, AccountBalance> paymentStore = interactiveQueryService.getQueryableStore(KpayBindings.ACCOUNT_BALANCE_STORE, QueryableStoreTypes.<String, AccountBalance>keyValueStore());

        if (paymentStore != null) {
            return paymentStore.all();
        }
        return null;
    }
}

package com.socarras.accountsservice.functions;

import com.socarras.accountsservice.service.IAccountsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Slf4j
@Configuration
public class AccountsFunctions {

    @Bean
    public Consumer<Long> updateCommunicationStatus(IAccountsService accountsService) {
        return accountNumber -> {
            log.info("Updating communication status for account number: " + accountNumber.toString());
            accountsService.updateCommunicationStatus(accountNumber);
        };
    }
}

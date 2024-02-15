package com.socarras.accountsservice.service.client;

import com.socarras.accountsservice.dto.LoansDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class LoansFallback implements LoansFeignClient {

    @Override
    public ResponseEntity<LoansDto> fetchCardDetails(String correlationId, String mobileNumber) {
        return null;
    }
}

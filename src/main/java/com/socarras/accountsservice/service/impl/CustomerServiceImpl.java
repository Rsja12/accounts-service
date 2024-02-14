package com.socarras.accountsservice.service.impl;

import com.socarras.accountsservice.dto.AccountsDto;
import com.socarras.accountsservice.dto.CardsDto;
import com.socarras.accountsservice.dto.CustomerDetailsDto;
import com.socarras.accountsservice.dto.LoansDto;
import com.socarras.accountsservice.entity.Accounts;
import com.socarras.accountsservice.entity.Customer;
import com.socarras.accountsservice.exception.ResourceNotFoundException;
import com.socarras.accountsservice.mapper.AccountsMapper;
import com.socarras.accountsservice.mapper.CustomerMapper;
import com.socarras.accountsservice.repository.AccountsRepository;
import com.socarras.accountsservice.repository.CustomerRepository;
import com.socarras.accountsservice.service.ICustomerService;
import com.socarras.accountsservice.service.client.CardsFeignClient;
import com.socarras.accountsservice.service.client.LoansFeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements ICustomerService {

    private final CardsFeignClient cardsFeignClient;
    private final LoansFeignClient loansFeignClient;
    private final AccountsRepository accountsRepository;
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CardsFeignClient cardsFeignClient,
                               LoansFeignClient loansFeignClient,
                               AccountsRepository accountsRepository,
                               CustomerRepository customerRepository) {
        this.cardsFeignClient = cardsFeignClient;
        this.loansFeignClient = loansFeignClient;
        this.accountsRepository = accountsRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchCardDetails(correlationId, mobileNumber);
        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
        customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());
        customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());

        return customerDetailsDto;
    }
}

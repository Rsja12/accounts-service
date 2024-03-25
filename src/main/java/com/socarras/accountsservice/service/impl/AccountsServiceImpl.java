package com.socarras.accountsservice.service.impl;

import com.socarras.accountsservice.constants.AccountsConstants;
import com.socarras.accountsservice.dto.AccountsDto;
import com.socarras.accountsservice.dto.AccountsMessageDto;
import com.socarras.accountsservice.dto.CustomerDto;
import com.socarras.accountsservice.entity.Accounts;
import com.socarras.accountsservice.entity.Customer;
import com.socarras.accountsservice.exception.CustomerAlreadyExistsException;
import com.socarras.accountsservice.exception.ResourceNotFoundException;
import com.socarras.accountsservice.mapper.AccountsMapper;
import com.socarras.accountsservice.mapper.CustomerMapper;
import com.socarras.accountsservice.repository.AccountsRepository;
import com.socarras.accountsservice.repository.CustomerRepository;
import com.socarras.accountsservice.service.IAccountsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
public class AccountsServiceImpl implements IAccountsService {

    private final AccountsRepository accountsRepository;
    private final CustomerRepository customerRepository;
    private final StreamBridge streamBridge;

    public AccountsServiceImpl(AccountsRepository accountsRepository,
                               CustomerRepository customerRepository,
                               StreamBridge streamBridge) {
        this.accountsRepository = accountsRepository;
        this.customerRepository = customerRepository;
        this.streamBridge = streamBridge;
    }

    @Override
    public void createAccount(CustomerDto customerDto) {
        Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        Optional<Customer> optionalCustomer = customerRepository.findByMobileNumber(customerDto.getMobileNumber());

        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered for mobile number: " +
                    customerDto.getMobileNumber());
        }

        Customer savedCustomer = customerRepository.save(customer);
        Accounts savedAccount = accountsRepository.save(createNewAccount(savedCustomer));
        sendCommunication(savedAccount, savedCustomer);
    }

    @Override
    public CustomerDto fetchAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));

        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "customerId",
                        customer.getCustomerId().toString()));

        CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        AccountsDto accountsDto = AccountsMapper.mapToAccountsDto(accounts, new AccountsDto());
        customerDto.setAccountsDto(accountsDto);

        return customerDto;
    }

    @Override
    public boolean updateAccount(CustomerDto customerDto) {
        boolean isUpdated = false;
        AccountsDto accountsDto = customerDto.getAccountsDto();

        if (accountsDto != null) {
            Accounts accounts = accountsRepository.findById(accountsDto.getAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber",
                            accountsDto.getAccountNumber().toString()));

            AccountsMapper.mapToAccounts(accountsDto, accounts);
            accounts = accountsRepository.save(accounts);

            Long customerId = accounts.getCustomerId();
            Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "customerId", customerId.toString()));

            CustomerMapper.mapToCustomer(customerDto, customer);
            customerRepository.save(customer);
            isUpdated = true;
        }

        return isUpdated;
    }

    @Override
    public boolean deleteAccount(String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }

    @Override
    public boolean updateCommunicationStatus(Long accountNumber) {
        boolean isUpdated = false;
        if (accountNumber != null) {
            Accounts accounts = accountsRepository.findById(accountNumber)
                    .orElseThrow(() -> new ResourceNotFoundException("Account", "AccountNumber",
                            accountNumber.toString()));
            accounts.setCommunicationSw(true);
            accountsRepository.save(accounts);
            isUpdated = true;
        }

        return isUpdated;
    }

    private Accounts createNewAccount(Customer customer) {
        Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        long randomAccNumber = 1000000000L + new Random().nextInt(900000000);
        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        return newAccount;
    }

    private void sendCommunication(Accounts account, Customer customer) {
        var accountsMessageDto = new AccountsMessageDto(account.getAccountNumber(), customer.getName(),
                customer.getEmail(), customer.getMobileNumber());

        log.info("Sending communication request for details: {}", accountsMessageDto);
        var result = streamBridge.send("sendCommunication-out-0", accountsMessageDto);
        log.info("Is the communication request successfully sent?: {}", result);
    }
}

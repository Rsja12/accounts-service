package com.socarras.accountsservice.service;

import com.socarras.accountsservice.dto.CustomerDetailsDto;

public interface ICustomerService {

    CustomerDetailsDto fetchCustomerDetails(String mobileNumber);
}

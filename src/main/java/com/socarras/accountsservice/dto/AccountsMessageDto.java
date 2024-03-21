package com.socarras.accountsservice.dto;

public record AccountsMessageDto(Long accountNumber,
                                 String name,
                                 String email,
                                 String mobileNumber) {
}

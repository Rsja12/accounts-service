package com.socarras.accountsservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(name = "Account", description = "Schema for the account details of a customer")
public class AccountsDto {

    @NotEmpty(message = "accountNumber cannot be null or empty")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be 10 digits")
    @Schema(description = "Account number of a Customer")
    private Long accountNumber;

    @NotEmpty(message = "accountType cannot be null or empty")
    @Schema(description = "Type of Account", example = "Savings")
    private String accountType;

    @NotEmpty(message = "branchAddress cannot be null or empty")
    @Schema(description = "EazyBank branch address")
    private String branchAddress;
}

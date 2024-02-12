package com.socarras.accountsservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(name = "CustomerDetails", description = "Schema for all Customer Details info")
public class CustomerDetailsDto {

    @NotEmpty(message = "name cannot be null or empty")
    @Size(min = 5, max = 30, message = "name length must be between 5 and 30")
    @Schema(description = "Name of the customer", example = "Madan Reddy")
    private String name;

    @NotEmpty(message = "email cannot be null or empty")
    @Email(message = "email must be a valid value")
    @Schema(description = "Email of the customer", example = "tutor@eazybytes.com")
    private String email;

    @Pattern(regexp = "(^$|[0-9]{10})", message = "mobileNumber must be 10 digits")
    @Schema(description = "Mobile number of the customer", example = "5553429898")
    private String mobileNumber;

    @Schema(description = "Account details of the Customer")
    private AccountsDto accountsDto;

    @Schema(description = "Loan details of the Customer")
    private LoansDto loansDto;

    @Schema(description = "Card details of the Customer")
    private CardsDto cardsDto;
}

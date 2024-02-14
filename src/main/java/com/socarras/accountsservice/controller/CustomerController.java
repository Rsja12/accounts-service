package com.socarras.accountsservice.controller;

import com.socarras.accountsservice.dto.CustomerDetailsDto;
import com.socarras.accountsservice.dto.ErrorResponseDto;
import com.socarras.accountsservice.service.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Tag(name = "Customer Details APIs in EazyBank", description = "CRUD REST APIs for Customer in EazyBank")
public class CustomerController {

    private final ICustomerService customerService;

    public CustomerController(ICustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/fetch-customer-details")
    @Operation(summary = "Fetch Customer Details", description = "Fetches customer details by mobileNumber")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Created"),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    public ResponseEntity<CustomerDetailsDto> fetchCustomerDetails(@RequestHeader("eazybank-correlation-id") String correlationId,
                                                                   @RequestParam
                                                                   @Pattern(regexp = "(^$|[0-9]{10})",
                                                                           message = "mobileNumber must be 10 digits") String mobileNumber) {
        log.info("eazybank-correlation-id found: {}", correlationId);
        CustomerDetailsDto customerDetailsDto = customerService.fetchCustomerDetails(mobileNumber, correlationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(customerDetailsDto);
    }
}

package com.socarras.accountsservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(name = "Response", description = "Schema to hold successful response info")
public class ResponseDto {

    @Schema(description = "Response status code")
    private String statusCode;

    @Schema(description = "Response status message")
    private String statusMsg;
}

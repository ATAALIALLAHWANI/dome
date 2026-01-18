package com.api.demo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL) // allows partial JSON
public class UpdatePatientRequest extends BasePatientRequest {
    // No additional fields needed
    // All fields are optional for updates
}

package com.api.demo.dto;

import lombok.Data;

@Data
public class SearchPatientRequest {
    private String searchText;
    private Boolean isEnglish;
    private Integer limit ; // Default to 100, but can be changed
}
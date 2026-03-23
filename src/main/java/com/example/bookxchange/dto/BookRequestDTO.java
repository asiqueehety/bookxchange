package com.example.bookxchange.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDTO {

    private String reqId;
    private String reqSubject;
    private String reqDescription;
    private String buyerUsername;
    private String buyerEmail;
    private String reqFulfillerUsername;
    private LocalDateTime createdAt;
}


package edu.ptithcm.learnnextbackend.common.core.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FieldErrorDetail {
    private String field;
    private Object rejectedValue;
    private String message;
}
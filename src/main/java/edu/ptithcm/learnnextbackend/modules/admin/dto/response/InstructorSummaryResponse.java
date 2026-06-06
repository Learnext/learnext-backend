package edu.ptithcm.learnnextbackend.modules.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class InstructorSummaryResponse {
    private String id;
    private String fullName;
    private String email;
    private String status;
    private long courseCount;
    private long studentCount;
    private BigDecimal revenue;
    private LocalDateTime createdAt;
}

package edu.ptithcm.learnnextbackend.modules.support.dto.response;

import edu.ptithcm.learnnextbackend.modules.support.enums.SupportLeadStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportLeadResponse {
    private UUID id;
    private String name;
    private String email;
    private String category;
    private String subject;
    private String message;
    private SupportLeadStatus status;
    private LocalDateTime createdAt;
}

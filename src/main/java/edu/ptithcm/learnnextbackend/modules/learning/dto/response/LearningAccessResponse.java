package edu.ptithcm.learnnextbackend.modules.learning.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LearningAccessResponse {
    private UUID courseId;
    private boolean access;
}

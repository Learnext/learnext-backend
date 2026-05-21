package edu.ptithcm.learnnextbackend.modules.auth.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterResponse {
    private String id;
    private String email;
    private String name;
}

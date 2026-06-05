package edu.ptithcm.learnnextbackend.modules.course.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class CourseSearchRequest {

    private String keyword;
    private UUID categoryId;
    private Integer page = 0;
    private Integer size = 10;

}

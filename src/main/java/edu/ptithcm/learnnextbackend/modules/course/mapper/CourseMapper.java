package edu.ptithcm.learnnextbackend.modules.course.mapper;

import edu.ptithcm.learnnextbackend.modules.course.dto.response.CourseResponse;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;

public final class CourseMapper {
    private CourseMapper() {
    }

    public static CourseResponse toResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .price(course.getPrice())
                .thumbnailUrl(course.getThumbnailUrl())
                .previewVideoUrl(course.getPreviewVideoUrl())
                .hasPreview(course.isHasPreview())
                .rating(course.getRating())
                .status(course.getStatus().name())
                .categoryId(course.getCategory().getId())
                .categoryName(course.getCategory().getName())
                .instructorId(course.getInstructor().getId())
                .instructorName(course.getInstructor().getFullName())
                .createdAt(course.getCreatedAt())
                .build();
    }
}

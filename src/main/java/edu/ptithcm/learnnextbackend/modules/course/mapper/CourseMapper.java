package edu.ptithcm.learnnextbackend.modules.course.mapper;

import edu.ptithcm.learnnextbackend.modules.course.dto.response.CourseResponse;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.PreviewLessonResponse;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

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
                .previewLesson(toPreviewLesson(course))
                .rating(course.getRating())
                .status(course.getStatus().name())
                .categoryId(course.getCategory().getId())
                .categoryName(course.getCategory().getName())
                .instructorId(course.getInstructor().getId())
                .instructorName(course.getInstructor().getFullName())
                .createdAt(course.getCreatedAt())
                .build();
    }

    private static PreviewLessonResponse toPreviewLesson(Course course) {
        if (!course.isHasPreview()
                || course.getPreviewVideoUrl() == null
                || course.getPreviewVideoUrl().isBlank()) {
            return null;
        }

        return PreviewLessonResponse.builder()
                .id(UUID.nameUUIDFromBytes(("preview-" + course.getId()).getBytes(StandardCharsets.UTF_8)))
                .title("Preview")
                .type("video")
                .videoUrl(course.getPreviewVideoUrl())
                .build();
    }
}

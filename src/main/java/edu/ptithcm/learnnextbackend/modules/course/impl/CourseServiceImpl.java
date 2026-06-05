package edu.ptithcm.learnnextbackend.modules.course.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.common.core.exception.NotFoundException;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.course.CourseService;
import edu.ptithcm.learnnextbackend.modules.course.dto.request.CourseSearchRequest;
import edu.ptithcm.learnnextbackend.modules.course.dto.response.CourseResponse;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;
import edu.ptithcm.learnnextbackend.modules.course.mapper.CourseMapper;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public List<CourseResponse> searchCourses(CourseSearchRequest request) {
        validatePriceRange(request);

        Specification<Course> spec = publishedOnly()
                .and(matchesKeyword(request.getQ()))
                .and(matchesCategory(request.getCategoryId()))
                .and(matchesInstructor(request.getInstructorId()))
                .and(matchesPrice(request))
                .and(matchesPreview(request.getHasPreview()));

        return courseRepository.findAll(spec, buildSort(request.getSort()))
                .stream()
                .map(CourseMapper::toResponse)
                .toList();
    }

    @Override
    public CourseResponse getPublishedCourse(UUID id) {
        return courseRepository.findByIdAndStatus(id, CourseStatus.PUBLISHED)
                .map(CourseMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("Course not found"));
    }

    private void validatePriceRange(CourseSearchRequest request) {
        if (request.getMinPrice() != null
                && request.getMaxPrice() != null
                && request.getMinPrice().compareTo(request.getMaxPrice()) > 0) {
            throw new BadRequestException("minPrice must be less than or equal to maxPrice");
        }
    }

    private Specification<Course> publishedOnly() {
        return (root, query, cb) -> cb.equal(root.get("status"), CourseStatus.PUBLISHED);
    }

    private Specification<Course> matchesKeyword(String keyword) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(keyword)) {
                return cb.conjunction();
            }

            String like = "%" + keyword.trim().toLowerCase() + "%";
            var category = root.join("category", JoinType.LEFT);
            var instructor = root.join("instructor", JoinType.LEFT);

            return cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(category.get("name")), like),
                    cb.like(cb.lower(instructor.get("fullName")), like)
            );
        };
    }

    private Specification<Course> matchesCategory(UUID categoryId) {
        return (root, query, cb) -> categoryId == null
                ? cb.conjunction()
                : cb.equal(root.get("category").get("id"), categoryId);
    }

    private Specification<Course> matchesInstructor(UUID instructorId) {
        return (root, query, cb) -> instructorId == null
                ? cb.conjunction()
                : cb.equal(root.get("instructor").get("id"), instructorId);
    }

    private Specification<Course> matchesPrice(CourseSearchRequest request) {
        return (root, query, cb) -> {
            if (request.getMinPrice() == null && request.getMaxPrice() == null) {
                return cb.conjunction();
            }
            if (request.getMinPrice() != null && request.getMaxPrice() != null) {
                return cb.between(root.get("price"), request.getMinPrice(), request.getMaxPrice());
            }
            if (request.getMinPrice() != null) {
                return cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice());
            }
            return cb.lessThanOrEqualTo(root.get("price"), request.getMaxPrice());
        };
    }

    private Specification<Course> matchesPreview(Boolean hasPreview) {
        return (root, query, cb) -> hasPreview == null
                ? cb.conjunction()
                : cb.equal(root.get("hasPreview"), hasPreview);
    }

    private Sort buildSort(String sort) {
        return switch (sort == null ? "relevance" : sort) {
            case "newest", "relevance" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "priceAsc" -> Sort.by(Sort.Direction.ASC, "price");
            case "priceDesc" -> Sort.by(Sort.Direction.DESC, "price");
            case "rating" -> Sort.by(Sort.Direction.DESC, "rating");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }
}

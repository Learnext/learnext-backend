package edu.ptithcm.learnnextbackend.modules.wishlist.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import edu.ptithcm.learnnextbackend.modules.wishlist.WishlistRepository;
import edu.ptithcm.learnnextbackend.modules.wishlist.WishlistService;
import edu.ptithcm.learnnextbackend.modules.wishlist.dto.WishlistResponse;
import edu.ptithcm.learnnextbackend.modules.wishlist.entity.Wishlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WishlistServiceImpl implements WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public WishlistResponse add(UUID courseId) {
        User user = getCurrentUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BadRequestException("Course not found"));

        if (wishlistRepository.existsByUserAndCourse(user, course)) {
            throw new BadRequestException("Course already in wishlist");
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .course(course)
                .build();

        return toResponse(wishlistRepository.save(wishlist));
    }

    @Override
    public void remove(UUID courseId) {
        User user = getCurrentUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BadRequestException("Course not found"));

        Wishlist wishlist = wishlistRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new BadRequestException("wishlist item not found"));

        wishlistRepository.delete(wishlist);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }

    private WishlistResponse toResponse(Wishlist wishlist) {
        Course course = wishlist.getCourse();

        return WishlistResponse.builder()
                .id(wishlist.getId())
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .thumbnailUrl(course.getThumbnailUrl())
                .createdAt(wishlist.getCreatedAt())
                .build();
    }
}

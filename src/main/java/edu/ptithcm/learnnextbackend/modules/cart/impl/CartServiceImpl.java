package edu.ptithcm.learnnextbackend.modules.cart.impl;

import edu.ptithcm.learnnextbackend.modules.cart.CartRepository;
import edu.ptithcm.learnnextbackend.modules.cart.CartService;
import edu.ptithcm.learnnextbackend.modules.cart.dto.request.AddToCartRequest;
import edu.ptithcm.learnnextbackend.modules.cart.dto.response.CartItemResponse;
import edu.ptithcm.learnnextbackend.modules.cart.dto.response.CartResponse;
import edu.ptithcm.learnnextbackend.modules.cart.entity.CartItem;
import edu.ptithcm.learnnextbackend.modules.course.CourseRepository;
import edu.ptithcm.learnnextbackend.modules.course.entity.Course;
import edu.ptithcm.learnnextbackend.modules.course.enums.CourseStatus;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public CartResponse getMyCart() {
        User user = getCurrentUser();

        List<CartItem> items = cartRepository.findByUserOrderByCreatedAtDesc(user);

        return toCartResponse(items);
    }

    @Override
    public CartResponse addToCart(AddToCartRequest request) {
        User user = getCurrentUser();

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new RuntimeException("Course is not available");
        }

        if (cartRepository.existsByUserAndCourse(user, course)) {
            throw new RuntimeException("Course already in cart");
        }

        CartItem item = CartItem.builder()
                .user(user)
                .course(course)
                .build();

        cartRepository.save(item);

        return getMyCart();
    }

    @Override
    public CartResponse removeFromCart(UUID courseId) {
        User user = getCurrentUser();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        CartItem item = cartRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        cartRepository.delete(item);

        return getMyCart();
    }

    @Override
    @Transactional
    public void clearCart() {
        User user = getCurrentUser();
        cartRepository.deleteByUser(user);
    }

    private CartResponse toCartResponse(List<CartItem> items) {
        List<CartItemResponse> itemResponses = items.stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal total = items.stream()
                .map(item -> item.getCourse().getPrice() != null
                        ? item.getCourse().getPrice()
                        : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(itemResponses)
                .totalAmount(total)
                .totalItems(itemResponses.size())
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        Course course = item.getCourse();

        return CartItemResponse.builder()
                .id(item.getId())
                .courseId(course.getId())
                .courseTitle(course.getTitle())
                .thumbnailUrl(course.getThumbnailUrl())
                .price(course.getPrice())
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}

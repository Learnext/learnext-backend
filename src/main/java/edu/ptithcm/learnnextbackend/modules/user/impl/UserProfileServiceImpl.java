package edu.ptithcm.learnnextbackend.modules.user.impl;

import edu.ptithcm.learnnextbackend.common.core.exception.BadRequestException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.ptithcm.learnnextbackend.modules.user.UserProfileRepository;
import edu.ptithcm.learnnextbackend.modules.user.UserProfileService;
import edu.ptithcm.learnnextbackend.modules.user.UserRepository;
import edu.ptithcm.learnnextbackend.modules.user.dto.request.UpdateProfileRequest;
import edu.ptithcm.learnnextbackend.modules.user.dto.response.ProfileResponse;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;
import edu.ptithcm.learnnextbackend.modules.user.entity.UserProfile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    private static final String UPLOAD_DIR = "uploads/avatars/";

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public ProfileResponse getMyProfile() {
        User currentUser = getCurrentUser();
        UserProfile profile = getProfileByUser(currentUser);
        return toProfileResponse(currentUser, profile);
    }

    @Override
    public ProfileResponse updateProfile(UpdateProfileRequest request) {
        User currentUser = getCurrentUser();
        UserProfile profile = getProfileByUser(currentUser);

        profile.setFullName(request.getFullName());
        profile.setBio(request.getBio());
        profile.setPhone(request.getPhone());

        UserProfile saved = userProfileRepository.save(profile);
        return toProfileResponse(currentUser, saved);
    }

    @Override
    public ProfileResponse uploadAvatar(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Avatar file is required");
        }

        User currentUser = getCurrentUser();
        UserProfile profile = getProfileByUser(currentUser);

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(UPLOAD_DIR);

        try {
            // Tạo folder nếu chưa có
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Files.copy(file.getInputStream(), uploadPath.resolve(fileName));
        } catch (IOException e) {
            throw new BadRequestException("Failed to upload avatar: " + e.getMessage());
        }

        profile.setAvatarUrl(UPLOAD_DIR + fileName);
        UserProfile saved = userProfileRepository.save(profile);
        return toProfileResponse(currentUser, saved);
    }

    @Override
    public void createProfileRegister(User user, String fullName) {
        UserProfile profile = UserProfile.builder()
                .user(user)
                .fullName(fullName)
                .build();
        userProfileRepository.save(profile);
    }

    private User getCurrentUser() {
        UUID userId = UUID.fromString(SecurityContextHolder.getContext()
                .getAuthentication()
                .getName());

        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("Authenticated user not found"));
    }

    private UserProfile getProfileByUser(User user) {
        return userProfileRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Profile not found"));
    }

    private ProfileResponse toProfileResponse(User user, UserProfile profile) {
        return ProfileResponse.builder()
                .email(user.getEmail())       // lấy từ User
                .fullName(profile.getFullName())
                .avatarUrl(profile.getAvatarUrl())
                .bio(profile.getBio())
                .phone(profile.getPhone())
                .build();
    }
}

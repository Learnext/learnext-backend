package edu.ptithcm.learnnextbackend.modules.user;

import org.springframework.web.multipart.MultipartFile;

import edu.ptithcm.learnnextbackend.modules.user.dto.request.UpdateProfileRequest;
import edu.ptithcm.learnnextbackend.modules.user.dto.response.ProfileResponse;
import edu.ptithcm.learnnextbackend.modules.user.entity.User;

public interface UserProfileService {
    ProfileResponse getMyProfile();
    ProfileResponse updateProfile(UpdateProfileRequest request);
    ProfileResponse uploadAvatar(MultipartFile file);
    void createProfileRegister(User user, String fullName);
}

package com.byteboxcodes.byteboxbackend.service;

import com.byteboxcodes.byteboxbackend.dto.LoginRequest;
import com.byteboxcodes.byteboxbackend.dto.ProfileUpdateRequest;
import com.byteboxcodes.byteboxbackend.dto.PublicProfileResponse;
import com.byteboxcodes.byteboxbackend.dto.UserRequest;
import com.byteboxcodes.byteboxbackend.entity.ProgrammingLanguage;

public interface UserService {

    void register(UserRequest request);

    String login(LoginRequest request);

    PublicProfileResponse getCurrentUser();

    void updateProfile(ProfileUpdateRequest request);

    void verifyEmail(String token);

    String loginWithGoogle(String idTokenString);

    void updatePreferredLanguage(ProgrammingLanguage language);

    void forgotPassword(String email);

    void resetPassword(String token, String newPassword);

}

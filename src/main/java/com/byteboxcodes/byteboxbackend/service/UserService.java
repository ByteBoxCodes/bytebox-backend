package com.byteboxcodes.byteboxbackend.service;

import com.byteboxcodes.byteboxbackend.dto.LoginRequest;
import com.byteboxcodes.byteboxbackend.dto.ProfileUpdateRequest;
import com.byteboxcodes.byteboxbackend.dto.PublicProfileResponse;
import com.byteboxcodes.byteboxbackend.dto.UserRequest;

public interface UserService {

    void register(UserRequest request);

    String login(LoginRequest request);

    PublicProfileResponse getCurrentUser();

    void updateProfile(ProfileUpdateRequest request);

    void verifyEmail(String token);

    String loginWithGoogle(String idTokenString);

}

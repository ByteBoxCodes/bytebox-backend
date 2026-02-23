package com.byteboxcodes.byteboxbackend.service;

import com.byteboxcodes.byteboxbackend.dto.LoginRequest;
import com.byteboxcodes.byteboxbackend.dto.UserRequest;
import com.byteboxcodes.byteboxbackend.dto.UserResponse;

public interface UserService {

    void register(UserRequest request);

    String login(LoginRequest request);

    UserResponse getCurrentUser();

}

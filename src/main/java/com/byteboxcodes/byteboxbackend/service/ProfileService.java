package com.byteboxcodes.byteboxbackend.service;

import com.byteboxcodes.byteboxbackend.dto.HeaderProfileResponse;
import com.byteboxcodes.byteboxbackend.dto.ProfileStatsResponse;
import com.byteboxcodes.byteboxbackend.dto.PublicUserProfileResponse;

public interface ProfileService {

    ProfileStatsResponse getProfileStats();

    HeaderProfileResponse getHeaderDetails();

    PublicUserProfileResponse getPublicProfile(String username);

}

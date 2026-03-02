package com.byteboxcodes.byteboxbackend.service;

import com.byteboxcodes.byteboxbackend.dto.HeaderProfileResponse;
import com.byteboxcodes.byteboxbackend.dto.ProfileStatsResponse;

public interface ProfileService {

    ProfileStatsResponse getProfileStats();

    HeaderProfileResponse getHeaderDetails();

}

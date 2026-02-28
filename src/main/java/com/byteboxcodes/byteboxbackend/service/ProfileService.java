package com.byteboxcodes.byteboxbackend.service;

import java.time.LocalDate;
import java.util.List;

import com.byteboxcodes.byteboxbackend.dto.ProfileStatsResponse;

public interface ProfileService {

    ProfileStatsResponse getProfileStats();

}

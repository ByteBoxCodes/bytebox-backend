package com.byteboxcodes.byteboxbackend.service;

import java.util.List;
import java.util.UUID;

import com.byteboxcodes.byteboxbackend.dto.SubmissionRequest;
import com.byteboxcodes.byteboxbackend.entity.Submission;

public interface SubmissionService {

    Submission submitSolution(SubmissionRequest request);

    List<Submission> getSubmissionsByUser(UUID userId);

    List<Submission> getSubmissionsByProblem(UUID problemId);

}

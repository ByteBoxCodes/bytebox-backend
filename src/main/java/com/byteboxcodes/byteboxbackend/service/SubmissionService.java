package com.byteboxcodes.byteboxbackend.service;

import java.util.List;
import java.util.UUID;

import com.byteboxcodes.byteboxbackend.dto.JudgeResult;
import com.byteboxcodes.byteboxbackend.dto.SubmissionListResponse;
import com.byteboxcodes.byteboxbackend.dto.SubmissionRequest;
import com.byteboxcodes.byteboxbackend.dto.SubmissionResponse;

public interface SubmissionService {

    SubmissionResponse submitSolution(SubmissionRequest request);

    List<SubmissionListResponse> getSubmissionsByUser();

    List<SubmissionListResponse> getSubmissionsByProblem(UUID problemId);

    List<SubmissionListResponse> getMySubmissionsByProblemId(UUID problemId);

    JudgeResult runCode(SubmissionRequest request);
}

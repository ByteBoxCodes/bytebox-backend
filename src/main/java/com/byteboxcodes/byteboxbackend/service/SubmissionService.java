package com.byteboxcodes.byteboxbackend.service;

import java.util.List;
import java.util.UUID;

import com.byteboxcodes.byteboxbackend.dto.JudgeResult;
import com.byteboxcodes.byteboxbackend.dto.SubmissionRequest;
import com.byteboxcodes.byteboxbackend.dto.SubmissionResponse;
import com.byteboxcodes.byteboxbackend.entity.Submission;

public interface SubmissionService {

    SubmissionResponse submitSolution(SubmissionRequest request);

    List<Submission> getSubmissionsByUser();

    List<Submission> getSubmissionsByProblem(UUID problemId);

    JudgeResult runCode(SubmissionRequest request);
}

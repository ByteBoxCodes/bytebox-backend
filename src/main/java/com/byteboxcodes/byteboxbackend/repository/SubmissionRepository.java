package com.byteboxcodes.byteboxbackend.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.byteboxcodes.byteboxbackend.entity.Problem;
import com.byteboxcodes.byteboxbackend.entity.Submission;
import com.byteboxcodes.byteboxbackend.entity.User;

public interface SubmissionRepository extends JpaRepository<Submission, UUID> {
    List<Submission> findByUser(User user);

    List<Submission> findByProblem(Problem problem);
}

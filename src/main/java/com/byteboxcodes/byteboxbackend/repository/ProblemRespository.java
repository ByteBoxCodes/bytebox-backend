package com.byteboxcodes.byteboxbackend.repository;

import java.util.UUID;

import org.springframework.beans.factory.parsing.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRespository extends JpaRepository<Problem, UUID> {

}

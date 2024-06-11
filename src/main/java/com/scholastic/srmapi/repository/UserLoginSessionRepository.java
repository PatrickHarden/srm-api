package com.scholastic.srmapi.repository;

import com.scholastic.srmapi.model.UserLoginSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginSessionRepository extends JpaRepository<UserLoginSession, Long> {
}


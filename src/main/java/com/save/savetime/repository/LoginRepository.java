package com.save.savetime.repository;

import com.save.savetime.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
}

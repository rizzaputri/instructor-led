package com.enigma.Instructor_Led.repository;

import com.enigma.Instructor_Led.entity.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, String>, JpaSpecificationExecutor<Trainee> {
    Trainee findByUserAccountId(String userAccountId);
}

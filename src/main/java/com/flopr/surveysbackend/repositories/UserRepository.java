package com.flopr.surveysbackend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.flopr.surveysbackend.entities.UserEntity;

@Repository
public interface UserRepository extends CrudRepository<UserEntity, Long>{
    
    public UserEntity findByEmail(String email);
    
}

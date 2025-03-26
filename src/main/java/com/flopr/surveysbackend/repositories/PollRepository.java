package com.flopr.surveysbackend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.flopr.surveysbackend.entities.PollEntity;

@Repository
public interface PollRepository extends CrudRepository<PollEntity, Long> {
    
    public PollEntity findByPollId(String pollId);
}

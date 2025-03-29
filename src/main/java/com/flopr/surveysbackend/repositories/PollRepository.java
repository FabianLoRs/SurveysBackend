package com.flopr.surveysbackend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.flopr.surveysbackend.entities.PollEntity;

@Repository
public interface PollRepository extends CrudRepository<PollEntity, Long> {
    PollEntity findByPollId(String pollId);

    PollEntity findById(long id);

    Page<PollEntity> findAllByUserId(long userId, Pageable pegeable);

    PollEntity findByPollIdAndUserId(String pollId, long userId);
}

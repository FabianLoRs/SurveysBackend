package com.flopr.surveysbackend.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.flopr.surveysbackend.entities.PollReplyEntity;

public interface PollReplyRepository extends CrudRepository<PollReplyEntity, Long>{
    public List<PollReplyEntity> findAll();
}

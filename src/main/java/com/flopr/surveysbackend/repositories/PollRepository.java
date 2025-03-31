package com.flopr.surveysbackend.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flopr.surveysbackend.entities.PollEntity;
import com.flopr.surveysbackend.interfaces.PollResult;

@Repository
public interface PollRepository extends CrudRepository<PollEntity, Long> {
    PollEntity findByPollId(String pollId);

    PollEntity findById(long id);

    Page<PollEntity> findAllByUserId(long userId, Pageable pegeable);

    PollEntity findByPollIdAndUserId(String pollId, long userId);

    @Query(value = "SELECT q.question_order AS questionOrder, prd.question_id AS questionId, q.content AS question, prd.answer_id AS answerId, a.content AS answer, COUNT(prd.answer_id) AS result FROM poll_reply pr LEFT JOIN poll_reply_detail prd ON prd.poll_reply_id = pr.id LEFT JOIN answer a ON a.id = prd.answer_id LEFT JOIN question q ON q.id = prd.question_id WHERE pr.poll_id = :pollId GROUP BY prd.question_id, prd.answer_id ORDER BY questionOrder ASC", nativeQuery = true)
    List<PollResult> getPollResults(@Param("pollId") long id);
}

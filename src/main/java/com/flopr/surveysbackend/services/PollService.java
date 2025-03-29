package com.flopr.surveysbackend.services;

import org.springframework.data.domain.Page;

import com.flopr.surveysbackend.entities.PollEntity;
import com.flopr.surveysbackend.models.requests.PollCreationRequestModel;

public interface PollService {

    public String createPoll(PollCreationRequestModel model, String email);

    public PollEntity getPoll(String pollId);

    public Page<PollEntity> getPolls(int page, int limit, String email);

    public void togglePollOpened(String pollId, String email);

    public void deletePoll(String pollId, String email);
}

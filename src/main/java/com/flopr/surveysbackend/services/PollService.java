package com.flopr.surveysbackend.services;

import com.flopr.surveysbackend.models.requests.PollCreationRequestModel;

public interface PollService {

    public String createPoll(PollCreationRequestModel model, String email);
}

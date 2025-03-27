package com.flopr.surveysbackend.services;

import com.flopr.surveysbackend.models.requests.PollReplyRequestModel;

public interface PollReplyService {

    public long createPollReply(PollReplyRequestModel model);
    
}

package com.flopr.surveysbackend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flopr.surveysbackend.models.requests.PollReplyRequestModel;
import com.flopr.surveysbackend.models.responses.CreatedPollReplyRest;
import com.flopr.surveysbackend.services.PollReplyService;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/polls/reply")
public class PollReplyController {
    
    @Autowired
    PollReplyService pollReplyService;

    @PostMapping()
    public CreatedPollReplyRest replyPoll(@RequestBody @Valid PollReplyRequestModel model) {

        System.out.println("model");

        return new CreatedPollReplyRest(pollReplyService.createPollReply(model));
                
    }
    
}

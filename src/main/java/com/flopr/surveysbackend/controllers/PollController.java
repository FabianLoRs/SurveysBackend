package com.flopr.surveysbackend.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flopr.surveysbackend.models.requests.PollCreationRequestModel;
import com.flopr.surveysbackend.models.responses.CreatedPollRest;
import com.flopr.surveysbackend.services.PollService;

@RestController
@RequestMapping("/polls")
public class PollController {

    @Autowired
    PollService pollService;
    
    @PostMapping
    public CreatedPollRest createPoll(@RequestBody @Valid PollCreationRequestModel model, Authentication authentication) {

        String pollId = pollService.createPoll(model, authentication.getPrincipal().toString());
        System.out.println(pollId);
        return new CreatedPollRest(pollId);
        
    }
}

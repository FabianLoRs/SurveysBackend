package com.flopr.surveysbackend.controllers;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.flopr.surveysbackend.entities.PollEntity;
import com.flopr.surveysbackend.models.requests.PollCreationRequestModel;
import com.flopr.surveysbackend.models.responses.CreatedPollRest;
import com.flopr.surveysbackend.models.responses.PollRest;
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

    @GetMapping(path = "{id}/questions")
    public PollRest getPollWithQuestion(@PathVariable String id) {
        PollEntity poll = pollService.getPoll(id);

        ModelMapper mapper = new ModelMapper();

        return mapper.map(poll, PollRest.class);
    }
    
}

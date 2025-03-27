package com.flopr.surveysbackend.services;

import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.flopr.surveysbackend.entities.AnswerEntity;
import com.flopr.surveysbackend.entities.PollEntity;
import com.flopr.surveysbackend.entities.QuestionEntity;
import com.flopr.surveysbackend.entities.UserEntity;
import com.flopr.surveysbackend.models.requests.PollCreationRequestModel;
import com.flopr.surveysbackend.repositories.PollRepository;
import com.flopr.surveysbackend.repositories.UserRepository;

@Service
public class PollServiceImpl implements PollService {

    PollRepository pollRepository;
    UserRepository userRepository;

    public PollServiceImpl(PollRepository pollRepository, UserRepository userRepository) { 

        this.pollRepository = pollRepository;
        this.userRepository = userRepository;

    }

    @Override
    public String createPoll(PollCreationRequestModel model, String email) {

        UserEntity user = userRepository.findByEmail(email);

        ModelMapper mapper = new ModelMapper();

        PollEntity pollEntity = mapper.map(model, PollEntity.class);

        pollEntity.setUser(user);

        pollEntity.setPollId(UUID.randomUUID().toString());

        for(QuestionEntity question: pollEntity.getQuestions()){ 
            question.setPoll(pollEntity);
            for (AnswerEntity answer: question.getAnswers()) {
                answer.setQuestion(question);
            }
        }

        pollRepository.save(pollEntity);

        return pollEntity.getPollId();

    }

    @Override
    public PollEntity getPoll(String pollId) {
        PollEntity poll = pollRepository.findByPollId(pollId);

        if (poll == null) {
            throw new RuntimeException("Poll not found");
        } 

        if (!poll.isOpened()) {
            throw new RuntimeException("Poll does not accept more replies");
        }

        return poll;
        
    }
    
}

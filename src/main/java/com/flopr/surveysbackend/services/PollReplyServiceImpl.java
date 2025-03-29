package com.flopr.surveysbackend.services;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.flopr.surveysbackend.entities.PollEntity;
import com.flopr.surveysbackend.entities.PollReplyDetailEntity;
import com.flopr.surveysbackend.entities.PollReplyEntity;
import com.flopr.surveysbackend.models.requests.PollReplyRequestModel;
import com.flopr.surveysbackend.repositories.PollReplyRepository;
import com.flopr.surveysbackend.repositories.PollRepository;

@Service
public class PollReplyServiceImpl implements PollReplyService {

    PollReplyRepository pollReplyRepository;
    PollRepository pollRepository;

    public PollReplyServiceImpl(PollReplyRepository pollReplyRepository, PollRepository pollRepository) {
        this.pollReplyRepository = pollReplyRepository;
        this.pollRepository = pollRepository;
    }

    @Override
    public long createPollReply(PollReplyRequestModel model) {

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setAmbiguityIgnored(true);

        PollReplyEntity pollReply = mapper.map(model, PollReplyEntity.class);
        PollEntity poll = pollRepository.findById(model.getPoll());
        
        // Asignar la encuesta persistida a la respuesta
        pollReply.setPoll(poll);

        Set<Long> uniqueReplies = new HashSet<>();

        for (PollReplyDetailEntity pollReplyDetailEntity: pollReply.getPollReplies()) {
            pollReplyDetailEntity.setPollReply(pollReply);
            uniqueReplies.add(pollReplyDetailEntity.getQuestionId());
        }

        if (uniqueReplies.size() != poll.getQuestions().size()) {
            throw new RuntimeException("You must answer all the questions");
        }

        PollReplyEntity replyEntity = pollReplyRepository.save(pollReply);

        return replyEntity.getId();
    }
}

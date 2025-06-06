package com.flopr.surveysbackend;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import com.flopr.surveysbackend.entities.AnswerEntity;
import com.flopr.surveysbackend.entities.PollEntity;
import com.flopr.surveysbackend.entities.QuestionEntity;
import com.flopr.surveysbackend.entities.UserEntity;
import com.flopr.surveysbackend.enums.QuestionType;
import com.flopr.surveysbackend.models.requests.AnswerCreationRequestModel;
import com.flopr.surveysbackend.models.requests.PollCreationRequestModel;
import com.flopr.surveysbackend.models.requests.PollReplyDetailRequestModel;
import com.flopr.surveysbackend.models.requests.PollReplyRequestModel;
import com.flopr.surveysbackend.models.requests.QuestionCreationRequestModel;
import com.flopr.surveysbackend.models.requests.UserRegisterRequestModel;

public class TestUtil {
    public static UserRegisterRequestModel createValidUser() {
        UserRegisterRequestModel user = new UserRegisterRequestModel();
        user.setEmail(generateRandomString(16) + "@mail.com");
        user.setName(generateRandomString(8));
        user.setPassword(generateRandomString(8));
        
        return user;
    }

    public static PollCreationRequestModel createValidPoll() { 
        ArrayList<AnswerCreationRequestModel> answers = new ArrayList<>();
        AnswerCreationRequestModel answer = new AnswerCreationRequestModel();
        answer.setContent(generateRandomString(16));
        answers.add(answer);

        ArrayList<QuestionCreationRequestModel> questions = new ArrayList<>();

        QuestionCreationRequestModel question = new QuestionCreationRequestModel();
        question.setContent(generateRandomString(16));
        question.setQuestionOrder(1);
        question.setType("CHECKBOX");
        question.setAnswers(answers);
        questions.add(question);

        PollCreationRequestModel poll = new PollCreationRequestModel();
        poll.setContent(generateRandomString(16));
        poll.setOpened(true);
        poll.setQuestions(questions);

        return poll;
    }

    public static PollReplyRequestModel createValidPollReply(PollEntity poll) {
        List<PollReplyDetailRequestModel> pollReplies = new ArrayList<>();
        PollReplyDetailRequestModel reply = new PollReplyDetailRequestModel();
        reply.setQuestionId(poll.getQuestions().get(0).getId());
        reply.setAnswerId(poll.getQuestions().get(0).getAnswers().get(0).getId());
        pollReplies.add(reply);

        PollReplyRequestModel model = new PollReplyRequestModel();
        model.setPoll(poll.getId());
        model.setUser(generateRandomString(8));
        model.setPollReplies(pollReplies);

        return model;
    }

    public static String generateRandomString(int len) {

        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        Random rnd = new Random();

        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }

        return sb.toString();
    }


    public static PollEntity createValidPollEntity(UserEntity user) { 

        PollEntity poll = new PollEntity();
        poll.setContent(generateRandomString(16));
        poll.setOpened(true);
        poll.setUser(user);
        poll.setPollId(UUID.randomUUID().toString());

        ArrayList<QuestionEntity> questions = new ArrayList<>();

        QuestionEntity question = new QuestionEntity();
        question.setContent(generateRandomString(16));
        question.setQuestionOrder(1);
        question.setType(QuestionType.CHECKBOX);
        question.setPoll(poll);
        questions.add(question);

        ArrayList<AnswerEntity> answers = new ArrayList<>();

        AnswerEntity answer = new AnswerEntity();
        answer.setContent(generateRandomString(16));
        answer.setQuestion(question);
        answers.add(answer);

        question.setAnswers(answers);
        poll.setQuestions(questions);

        return poll;
        
    }
}

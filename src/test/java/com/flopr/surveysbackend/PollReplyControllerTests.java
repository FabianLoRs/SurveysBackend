package com.flopr.surveysbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.flopr.surveysbackend.entities.PollEntity;
import com.flopr.surveysbackend.entities.PollReplyEntity;
import com.flopr.surveysbackend.entities.UserEntity;
import com.flopr.surveysbackend.models.requests.PollReplyRequestModel;
import com.flopr.surveysbackend.models.responses.ValidationErrors;
import com.flopr.surveysbackend.repositories.PollReplyRepository;
import com.flopr.surveysbackend.repositories.PollRepository;
import com.flopr.surveysbackend.repositories.UserRepository;
import com.flopr.surveysbackend.services.UserService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class PollReplyControllerTests {
    
    private static final String API_URL = "/polls/reply";

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PollRepository pollRepository;

    @Autowired
    PollReplyRepository pollReplyRepository;

    PollEntity poll;

    @BeforeAll
    public void initializedObjects() {
        UserEntity user = userService.createUser(TestUtil.createValidUser());
        this.poll = pollRepository.save(TestUtil.createValidPollEntity(user));
    }

    @AfterAll
    public void cleanAfter(){
        pollRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    public void cleanup() {
        pollReplyRepository.deleteAll();
    }

    @Test
    public void replyPoll_withoutUser_returnBadRequest() {
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.setUser(null);

        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void replyPoll_withoutUser_returnValidationError() {
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.setUser(null);

        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertTrue(response.getBody().getErrors().containsKey("user"));
    }

    @Test
    public void replyPoll_withInvalidIDPoll_returnBadRequest() {
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.setPoll(0);

        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void replyPoll_withInvalidIDPoll_returnValidationError() {
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.setPoll(0);

        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertTrue(response.getBody().getErrors().containsKey("poll"));
    }

    @Test
    public void replyPoll_withEmptyRepliesPollList_returnBadRequest() {
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.setPollReplies(null);

        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void replyPoll_withEmptyRepliesPollList_returnValidationError() {
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.setPollReplies(null);

        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertTrue(response.getBody().getErrors().containsKey("pollReplies"));
    }

    @Test
    public void replyPoll_withInvalidIDQuestion_returnBadRequest() {
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.getPollReplies().get(0).setQuestionId(0);

        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void replyPoll_withInvalidIDQuestion_returnValidationError() {
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.getPollReplies().get(0).setQuestionId(0);

        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertTrue(response.getBody().getErrors().containsKey("pollReplies[0].questionId"));
    }

    @Test
    public void replyPoll_withInvalidIDAnswer_returnBadRequest() {
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.getPollReplies().get(0).setAnswerId(0);

        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void replyPoll_withInvalidIDAnswer_returnValidationError() {
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        model.getPollReplies().get(0).setAnswerId(0);

        ResponseEntity<ValidationErrors> response = createPollReply(model, ValidationErrors.class);
        assertTrue(response.getBody().getErrors().containsKey("pollReplies[0].answerId"));
    }

    @Test
    public void replyPoll_withValidData_returnOk() {
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        
        ResponseEntity<Object> response = createPollReply(model, Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void replyPoll_withValidData_saveToDataBase() {
        PollReplyRequestModel model = TestUtil.createValidPollReply(poll);
        createPollReply(model, Object.class);

        List<PollReplyEntity> replies = pollReplyRepository.findAll();
        assertEquals(replies.size(), 1);
    }
    
    public <T> ResponseEntity<T> createPollReply(PollReplyRequestModel data, Class<T> responseType) {
        return testRestTemplate.postForEntity(API_URL, data, responseType);
    }
}

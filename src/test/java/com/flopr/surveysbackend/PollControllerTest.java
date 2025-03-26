package com.flopr.surveysbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.flopr.surveysbackend.entities.PollEntity;
import com.flopr.surveysbackend.models.requests.PollCreationRequestModel;
import com.flopr.surveysbackend.models.requests.UserLoginRequestModel;
import com.flopr.surveysbackend.models.requests.UserRegisterRequestModel;
import com.flopr.surveysbackend.models.responses.ValidationErrors;
import com.flopr.surveysbackend.repositories.PollRepository;
import com.flopr.surveysbackend.repositories.UserRepository;
import com.flopr.surveysbackend.services.UserService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(Lifecycle.PER_CLASS)
public class PollControllerTest {

    private static final String API_URL = "/polls";

    private static final String API_LOGIN_URL = "/users/login";

    private String token = "";
    
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PollRepository pollRepository;

    @BeforeAll
    public void initializedObjects() {

        // Crear y guardar usuario
        UserRegisterRequestModel user = TestUtil.createValidUser();
        userService.createUser(user);

        // Crear modelo de login con las mismas credenciales
        UserLoginRequestModel model = new UserLoginRequestModel();
        model.setEmail(user.getEmail());
        model.setPassword(user.getPassword());

        // Intentar login
        //ResponseEntity<String> response = login(model, String.class);
        
        ResponseEntity<Map<String, String>> response = login(model, new ParameterizedTypeReference<Map<String, String>>(){});

        Map<String, String> body = response.getBody();
        
        this.token = body.get("token");

    }

    @AfterEach
    public void cleanup() { 

        pollRepository.deleteAll();

    }

    @AfterAll
    public void cleanupAfter() { 

        userRepository.deleteAll();

    }

    @Test
    public void createPoll_withOutAuthentication_returnForbidden() { 

        ResponseEntity<Object> response = createPoll(new PollCreationRequestModel(), Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);

    }

    @Test
    public void createPoll_withAuthenticationWithoutData_returnBadRequest() { 

        ResponseEntity<ValidationErrors> response = createPoll(new PollCreationRequestModel(), new ParameterizedTypeReference<ValidationErrors>(){});

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        
    }

    @Test
    public void createPoll_withAuthenticationWithoutPollContent_returnBadRequest() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        poll.setContent("");

        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        
    }

    @Test
    public void createPoll_withAuthenticationWithoutPollQuestion_returnBadRequest() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        poll.setQuestions(null);

        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithoutContent_returnBadRequest() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        poll.getQuestions().get(0).setContent("");

        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithoutContent_returnValidationErrorToQuestionContent() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        poll.getQuestions().get(0).setContent("");

        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});

        assertTrue(response.getBody().getErrors().containsKey("questions[0].content"));
        
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithBadOrder_returnBadRequest() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        poll.getQuestions().get(0).setQuestionOrder(0);

        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithBadOrder_returnValidationErrorToQuestionOrder() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        poll.getQuestions().get(0).setQuestionOrder(0);

        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});

        assertTrue(response.getBody().getErrors().containsKey("questions[0].questionOrder"));
        
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithIncorrectQuestionType_returnBadRequest() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        poll.getQuestions().get(0).setType("ksjjdfks");

        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithIncorrectQuestionType_returnValidationErrorToQuestionType() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        poll.getQuestions().get(0).setType("ksjjdfks");

        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});

        assertTrue(response.getBody().getErrors().containsKey("questions[0].type"));
        
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithoutAnswer_returnBadRequest() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        poll.getQuestions().get(0).setAnswers(null);

        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        
    }

    @Test
    public void createPoll_withAuthenticationWithValidQuestionWithoutAnswer_returnValidationErrorToListAnswer() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        poll.getQuestions().get(0).setAnswers(null);

        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});

        assertTrue(response.getBody().getErrors().containsKey("questions[0].answers"));
        
    }

    @Test
    public void createPoll_withAuthenticationWithoutAnswerContent_returnBadRequest() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        poll.setContent("");

        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});

        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
        
    }

    @Test
    public void createPoll_withAuthenticationWithoutAnswerContent_returnValidationErrorToAnswerContent() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        poll.getQuestions().get(0).getAnswers().get(0).setContent("");

        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});

        assertTrue(response.getBody().getErrors().containsKey("questions[0].answers[0].content"));
        
    }

    @Test
    public void createPoll_withAuthenticationWitValidPoll_returnOK() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        ResponseEntity<Object> response = createPoll(poll, new ParameterizedTypeReference<Object>(){});

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void createPoll_withAuthenticationWitValidPoll_returnCreatedPollId() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        ResponseEntity<Map<String, String>> response = createPoll(poll, new ParameterizedTypeReference<Map<String, String>>(){});

        Map<String, String> body = response.getBody();
        assertTrue(body.containsKey("pollId"));
    }

    @Test
    public void createPoll_withAuthenticationWitValidPoll_saveInDataBase() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        ResponseEntity<Map<String, String>> response = createPoll(poll, new ParameterizedTypeReference<Map<String, String>>(){});

        Map<String, String> body = response.getBody();

        PollEntity pollBD = pollRepository.findByPollId(body.get("pollId"));

        assertNotNull(pollBD);
        
    }

    @Test
    public void createPoll_withAuthenticationWithoutAnswerContent_returnValidationErrorToContent() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        poll.setContent("");

        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});

        assertTrue(response.getBody().getErrors().containsKey("content"));
        
    }

    @Test
    public void createPoll_withAuthenticationWithoutAnswerToPoll_returnValidationErrorToQuestion() { 

        PollCreationRequestModel poll = TestUtil.createValidPoll();

        poll.setQuestions(null);

        ResponseEntity<ValidationErrors> response = createPoll(poll, new ParameterizedTypeReference<ValidationErrors>(){});

        assertTrue(response.getBody().getErrors().containsKey("questions"));
        
    }


    private <T> ResponseEntity<T> createPoll(PollCreationRequestModel data, Class<T> responseType) {

        return testRestTemplate.postForEntity(API_URL, data, responseType);

    }

    private <T> ResponseEntity<T> createPoll(PollCreationRequestModel data, ParameterizedTypeReference<T> responseType ) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<PollCreationRequestModel> entity = new HttpEntity<PollCreationRequestModel>(data, headers);
        return testRestTemplate.exchange(API_URL, HttpMethod.POST, entity, responseType);

    }

    private <T> ResponseEntity<T> login(UserLoginRequestModel data, ParameterizedTypeReference<T> responseType ) {

        HttpEntity<UserLoginRequestModel> entity = new HttpEntity<UserLoginRequestModel>(data, new HttpHeaders());
        return testRestTemplate.exchange(API_LOGIN_URL, HttpMethod.POST, entity, responseType);

    }
}

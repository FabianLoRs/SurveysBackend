package com.flopr.surveysbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import com.flopr.surveysbackend.entities.UserEntity;
import com.flopr.surveysbackend.models.requests.UserLoginRequestModel;
import com.flopr.surveysbackend.models.requests.UserRegisterRequestModel;
import com.flopr.surveysbackend.models.responses.UserRest;
import com.flopr.surveysbackend.models.responses.ValidationErrors;
import com.flopr.surveysbackend.repositories.UserRepository;
import com.flopr.surveysbackend.services.UserService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {

    private static final String API_URL = "/users";

    private static final String API_LOGIN_URL = "/users/login";
    
    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @AfterEach
    public void cleanup() {

        userRepository.deleteAll();

    }

    @Test
    public void createUser_withoutField_returnBadRequest() {

        ResponseEntity<Object> response = register(new UserRegisterRequestModel(), Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createUser_withoutNameField_returnBadRequest() {
        UserRegisterRequestModel user = TestUtil.createValidUser();
        user.setName(null);

        ResponseEntity<Object> response = register(user, Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createUser_withoutPasswordField_returnBadRequest() {
        UserRegisterRequestModel user = TestUtil.createValidUser();
        user.setPassword(null);

        ResponseEntity<Object> response = register(user, Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createUser_withoutEmailField_returnBadRequest() {
        UserRegisterRequestModel user = TestUtil.createValidUser();
        user.setPassword(null);

        ResponseEntity<Object> response = register(user, Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
    } 

    @Test
    public void createUser_withoutField_returnValidationErrors() {

        ResponseEntity<ValidationErrors> response = register(new UserRegisterRequestModel(), ValidationErrors.class);

        Map<String, String> errors = response.getBody().getErrors();
        
        
        assertEquals(errors.size(), 3);
    }

    @Test
    public void createUser_withoutName_returnNameValidationError() {

        UserRegisterRequestModel user = TestUtil.createValidUser();
        user.setName(null);

        ResponseEntity<ValidationErrors> response = register(user, ValidationErrors.class);

        Map<String, String> errors = response.getBody().getErrors();

        assertTrue(errors.containsKey("name"));
    }

    @Test
    public void createUser_withoutEmail_returnEmailValidationError() {

        UserRegisterRequestModel user = TestUtil.createValidUser();
        user.setEmail(null);

        ResponseEntity<ValidationErrors> response = register(user, ValidationErrors.class);

        Map<String, String> errors = response.getBody().getErrors();

        assertTrue(errors.containsKey("email"));
    }

    @Test
    public void createUser_withoutPassword_returnPasswordValidationError() {

        UserRegisterRequestModel user = TestUtil.createValidUser();
        user.setPassword(null);

        ResponseEntity<ValidationErrors> response = register(user, ValidationErrors.class);

        Map<String, String> errors = response.getBody().getErrors();

        assertTrue(errors.containsKey("password"));
    }

    @Test
    public void createUser_withValidUser_returnOK() {

        UserRegisterRequestModel user = TestUtil.createValidUser();

        ResponseEntity<UserRest> response = register(user, UserRest.class);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void createUser_withValidUser_returnUserRest() {

        UserRegisterRequestModel user = TestUtil.createValidUser();

        ResponseEntity<UserRest> response = register(user, UserRest.class);
        assertEquals(response.getBody().getName(), user.getName());
    }

    @Test
    public void createUser_withValidUser_returnSaveUserDataBase() {

        UserRegisterRequestModel user = TestUtil.createValidUser();

        ResponseEntity<UserRest> response = register(user, UserRest.class);

        UserEntity userDB = userRepository.findById(response.getBody().getId()).get();
        assertNotNull(userDB);
    }

    @Test
    public void createUser_withValidUser_SaveHashPasswordDataBase() {

        UserRegisterRequestModel user = TestUtil.createValidUser();

        ResponseEntity<UserRest> response = register(user, UserRest.class);

        UserEntity userDB = userRepository.findById(response.getBody().getId()).get();
        assertNotEquals(user.getPassword(), userDB.getEncryptedPassword());
    }
    
    @Test
    public void createUser_withValidUserWithExistingEmail_resturnBadRequest() {

        UserRegisterRequestModel user = TestUtil.createValidUser();

        register(user, UserRest.class);
        ResponseEntity<UserRest> response2 = register(user, UserRest.class);

        assertEquals(response2.getStatusCode(), HttpStatus.BAD_REQUEST);
    }

    @Test
    public void createUser_withValidUserWithExistingEmail_returnErrorMessageToValidationEmail() {

        UserRegisterRequestModel user = TestUtil.createValidUser();

        register(user, UserRest.class);
        ResponseEntity<ValidationErrors> response2 = register(user, ValidationErrors.class);

        Map<String, String> errors = response2.getBody().getErrors();

        assertTrue(errors.containsKey("email"));
    }

    @Test
    public void getUser_withoutAuthenticationToken_returnForbidden() {

        ResponseEntity<Object> response = getUser(null, new ParameterizedTypeReference<Object>(){});

        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void getUser_withAuthenticationToken_returnUserOK() {

        UserRegisterRequestModel user = TestUtil.createValidUser();
        userService.createUser(user);

        UserLoginRequestModel model = new UserLoginRequestModel();
        model.setEmail(user.getEmail());
        model.setPassword(user.getPassword());

        ResponseEntity<Map<String, String>> responseLogin = login(model, new ParameterizedTypeReference<Map<String, String>>(){});

        Map<String, String> body = responseLogin.getBody();

        String token = body.get("token").replace("Bearer ", "");

        ResponseEntity<UserRest> response = getUser(token, new ParameterizedTypeReference<UserRest>(){});

        assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void getUser_withAuthenticationToken_returnUserRest() {

        UserRegisterRequestModel user = TestUtil.createValidUser();
        userService.createUser(user);

        UserLoginRequestModel model = new UserLoginRequestModel();
        model.setEmail(user.getEmail());
        model.setPassword(user.getPassword());

        ResponseEntity<Map<String, String>> responseLogin = login(model, new ParameterizedTypeReference<Map<String, String>>(){});

        Map<String, String> body = responseLogin.getBody();

        String token = body.get("token").replace("Bearer ", "");

        ResponseEntity<UserRest> response = getUser(token, new ParameterizedTypeReference<UserRest>(){});

        assertEquals(user.getName(), response.getBody().getName());
    }


    private <T> ResponseEntity<T> getUser(String token, ParameterizedTypeReference<T> responseType) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);
        return testRestTemplate.exchange(API_URL, HttpMethod.GET, entity, responseType);

    }
    
    private <T> ResponseEntity<T> login(UserLoginRequestModel data, ParameterizedTypeReference<T> responseType ) {

        HttpEntity<UserLoginRequestModel> entity = new HttpEntity<UserLoginRequestModel>(data, new HttpHeaders());
        return testRestTemplate.exchange(API_LOGIN_URL, HttpMethod.POST, entity, responseType);

    } 

    private <T> ResponseEntity<T> register(UserRegisterRequestModel data, Class<T> responseType) {

        return testRestTemplate.postForEntity(API_URL, data, responseType);

    }
}

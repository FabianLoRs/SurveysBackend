package com.flopr.surveysbackend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
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

import com.flopr.surveysbackend.models.requests.UserLoginRequestModel;
import com.flopr.surveysbackend.models.requests.UserRegisterRequestModel;
import com.flopr.surveysbackend.repositories.UserRepository;
import com.flopr.surveysbackend.services.UserService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginTest {
    
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
    public void postLogin_withOutCredentials_returnForbidden() {
        ResponseEntity<Object> response = login(null, Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void postLogin_withBadCredentials_returnForbidden() {
        // Crear un usuario válido
        UserRegisterRequestModel user = TestUtil.createValidUser();
        userService.createUser(user);

        UserLoginRequestModel  model = new UserLoginRequestModel();
        model.setEmail("asd@gmail.com");
        model.setPassword("12345678");

        ResponseEntity<Object> response = login(null, Object.class);
        assertEquals(response.getStatusCode(), HttpStatus.FORBIDDEN);
    }

    @Test
    public void postLogin_withValidCredentials_returnOK() {
        // Crear y guardar usuario
        UserRegisterRequestModel user = TestUtil.createValidUser();
        userService.createUser(user);

        // Crear modelo de login con las mismas credenciales
        UserLoginRequestModel model = new UserLoginRequestModel();
        model.setEmail(user.getEmail());
        model.setPassword(user.getPassword());

        // Intentar login
        ResponseEntity<String> response = login(model, String.class);
        
        // Verificar respuesta
        assertEquals(HttpStatus.OK, response.getStatusCode());
        /* assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("token")); */
    }

    @Test
    public void postLogin_withValidCredentials_returnAuthToken() {
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

        String token = body.get("token");

        // Verificar respuesta
        assertTrue(token.contains("Bearer"));
/*         assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("token")); */
    }

    private <T> ResponseEntity<T> login(UserLoginRequestModel data, Class<T> responseType) {

        return testRestTemplate.postForEntity(API_LOGIN_URL, data, responseType);

    }

    private <T> ResponseEntity<T> login(UserLoginRequestModel data, ParameterizedTypeReference<T> responseType ) {

        HttpEntity<UserLoginRequestModel> entity = new HttpEntity<UserLoginRequestModel>(data, new HttpHeaders());
        return testRestTemplate.exchange(API_LOGIN_URL, HttpMethod.POST, entity, responseType);

    } 
}

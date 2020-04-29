package ru.javamentor.Front.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.javamentor.Front.entity.User;
import ru.javamentor.Front.exception.UserAlreadyExistsException;
import ru.javamentor.Front.response.UserSuccessResponse;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;


@Service
@PropertySource("classpath:restServiceData.properties")
public class UserServiceImpl implements UserDetailsService, UserService {

    @Value("${rest.url}")
    private String url;

    @Value("${rest.login}")
    private String login;

    @Value("${rest.password}")
    private String password;

    private RestTemplate restTemplate;

    public UserServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        HttpEntity request = new HttpEntity<>(getAuthorizationHeader());
        try {
            return restTemplate.exchange(url + "?username=" + username, HttpMethod.GET, request, User.class).getBody();
        } catch (Exception e) {
            throw new UsernameNotFoundException("User not found");
        }
    }

    @Override
    public void save(User user) throws UserAlreadyExistsException {
        HttpEntity<User> request = new HttpEntity<>(user);
        try {
            restTemplate
                    .exchange(url, HttpMethod.POST, request, UserSuccessResponse.class);
        } catch (Exception e) {
            throw new UserAlreadyExistsException("Username " + user.getUsername() + " already exists");
        }
    }

    @Override
    public void update(User user) {
        HttpEntity<User> request = new HttpEntity<>(user);
        final ResponseEntity<UserSuccessResponse> response = restTemplate.exchange(url, HttpMethod.PUT, request, UserSuccessResponse.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new UserAlreadyExistsException("Username " + user.getUsername() + " already exists");
        }
    }

    @Override
    public List<User> listAll() {
        final ResponseEntity<User[]> response = restTemplate.getForEntity(url + "list", User[].class);
        return Arrays.asList(response.getBody());
    }

    @Override
    public User get(Long id) {
        return restTemplate.getForObject(url + id, User.class);
    }

    @Override
    public void delete(Long id) {
        restTemplate.exchange(url + id, HttpMethod.DELETE, null, UserSuccessResponse.class);
    }

    private HttpHeaders getAuthorizationHeader(){
        String authStr = login + ":" + password;
        String base64Creds = Base64.getEncoder().encodeToString(authStr.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        return headers;
    }
}


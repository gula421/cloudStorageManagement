package com.udacity.jwdnd.course1.cloudstorage.services;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthenticationService implements AuthenticationProvider {
    private final HashService hashService;
    private final UserService userService;

    public AuthenticationService(HashService hashService, UserService userService) {
        this.hashService = hashService;
        this.userService = userService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userService.getUser(username);
        // if user exist, return AuthenticationToken
        if (user != null){
            // get hashed password
            String encodedSalt = user.getSalt();
            String hashedPassword = hashService.getHashedValue(password, encodedSalt);
            // validate the password
            if(hashedPassword.equals(user.getPassword())){
                return new UsernamePasswordAuthenticationToken(username, password, new ArrayList<>());
            }
        }
        // if user doesn't exist, no AuthenticationToken returned
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}

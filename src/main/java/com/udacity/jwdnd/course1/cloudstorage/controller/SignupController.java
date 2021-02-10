package com.udacity.jwdnd.course1.cloudstorage.controller;

import com.udacity.jwdnd.course1.cloudstorage.model.User;
import com.udacity.jwdnd.course1.cloudstorage.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/signup")
public class SignupController {

    private final UserService userService;

    public SignupController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String signupView(HttpSession session){
        return "signup";
    };

    @PostMapping
    public String signupUser(@ModelAttribute User user, HttpSession session){
        String signupError = null;

        // check if user already exist
        if (!userService.isUsernameAvailable(user.getUsername())){
            signupError = "The username already exists.";

        }
        System.out.println("user not yet exist");
        // create user
        if (signupError==null){
            int rowAdded = userService.createUser(user);
            if(rowAdded<0){
                signupError = "There was an error signing you up. Please try again.";
            }
        }

        // add model Attribute
        if (signupError==null){
            session.setAttribute("signupSuccess",true);
            return "redirect:/login";
        } else {
            session.setAttribute("signupError",signupError);
            return "redirect:/signup";
        }


    }
}

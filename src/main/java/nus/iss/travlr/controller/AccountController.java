package nus.iss.travlr.controller;

import java.lang.reflect.Field;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

import nus.iss.travlr.model.RegisterUser;
import nus.iss.travlr.model.User;
import nus.iss.travlr.service.AccountService;

@Controller
@RequestMapping
public class AccountController {

    @Autowired
    private AccountService accSvc;

    @GetMapping(path = "/login")
    public String getLogin(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "login";
    }

    @PostMapping(path = "/login")
    public String postLogin(@ModelAttribute User user) {
        System.out.println(user);
        return "login";
    }

    @GetMapping(path = "/register")
    public String getRegister(Model model) {
        model.addAttribute("registerUser", new RegisterUser());
        return "register";
    }

    @PostMapping(path = "/register")
    public ModelAndView postRegister(@Valid @ModelAttribute RegisterUser registerUser, BindingResult result, RedirectAttributes redirectAttributes) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("register");

        if (result.hasErrors()) {
            mav.setStatus(HttpStatus.BAD_REQUEST);
            return mav;
        }

        if (!accSvc.validPassword(registerUser.getPassword(), registerUser.getPassword2())) {
            mav.setStatus(HttpStatus.BAD_REQUEST);
            FieldError err = new FieldError("registerUser", "password2", "Passwords do not match.");
            result.addError(err);
            return mav;
        }

        if (accSvc.userExists(registerUser.getUserName())) {
            mav.setStatus(HttpStatus.BAD_REQUEST);
            FieldError err = new FieldError("registerUser", "userName", "This username has been used!");
            result.addError(err);
            return mav;
        }

        accSvc.register(registerUser);
        ModelAndView redirectMav = new ModelAndView("redirect:/login");
        redirectMav.setStatus(HttpStatus.SEE_OTHER);
        // redirectMav.addObject("message", "You have registered successfully!"); // Success message
        // redirectMav.addObject("user", new User());
        
        return redirectMav;
    }
}

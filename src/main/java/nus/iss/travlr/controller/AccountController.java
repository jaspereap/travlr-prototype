package nus.iss.travlr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import nus.iss.travlr.model.RegisterUser;
import nus.iss.travlr.model.User;
import nus.iss.travlr.service.AccountService;
import nus.iss.travlr.service.SessionService;

@Controller
@RequestMapping
public class AccountController {

    @Autowired
    private AccountService accSvc;
    @Autowired
    private SessionService sessSvc;

    @GetMapping(path = "/login")
    public String getLogin(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        return "login";
    }

    @PostMapping(path = "/login")
    public String postLogin(@Valid @ModelAttribute User user, BindingResult result, HttpSession session) {
        if (result.hasErrors()) {
            return "login";
        }
    
        if (!accSvc.hasUser(user.getUserName())) {
            FieldError err = new FieldError("user", "userName", "Login failed!");
            result.addError(err);
            return "login";
        }

        if (!accSvc.isValidUser(user.getUserName(), user.getPassword())) {
            FieldError err = new FieldError("user", "userName", "Login failed!");
            result.addError(err);
            System.out.println("\tLogin failed");
            return "login";
        }

        System.out.println("\tLogin success");
        User retrievedUser = accSvc.getUser(user.getUserName());
        System.out.println("\tLogin Session id: " + session.getId());
        System.out.println("\tLogin User id: " + retrievedUser.getUserId());
        sessSvc.loginUser(session.getId(), retrievedUser.getUserName());
        return "redirect:/home";
    }

    @GetMapping(path = "/logout")
    public String getLogout(HttpSession session, RedirectAttributes redirectAttributes) {
        String sessId = session.getId();
        sessSvc.logoutUser(sessId);
        redirectAttributes.addFlashAttribute("message", "Logged out successfully.");
        return "redirect:/";
    }

    @GetMapping(path = "/register")
    public String getRegister(Model model) {
        model.addAttribute("registerUser", new RegisterUser());
        return "register";
    }

    @PostMapping(path = "/register")
    public String postRegister(@Valid @ModelAttribute RegisterUser registerUser, BindingResult result, Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "register";
        }

        if (!accSvc.validPassword(registerUser.getPassword(), registerUser.getPassword2())) {
            FieldError err = new FieldError("registerUser", "password2", "Passwords do not match.");
            result.addError(err);

            return "register";
        }

        if (accSvc.hasUser(registerUser.getUserName())) {
            FieldError err = new FieldError("registerUser", "userName", "This username has been used!");
            result.addError(err);

            return "register";
        }

        accSvc.register(registerUser);

        redirectAttributes.addFlashAttribute("message", "You have registered successfully!");
        model.addAttribute("user", new User());
        return "redirect:/login";
    }
}

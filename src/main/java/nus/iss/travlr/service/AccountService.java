package nus.iss.travlr.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nus.iss.travlr.model.OpStatus;
import nus.iss.travlr.model.RegisterUser;
import nus.iss.travlr.model.User;
import nus.iss.travlr.repository.UserRepository;

@Service
public class AccountService {
    @Autowired
    private UserRepository userRepo;

    public boolean userExists(String userName) {
        return userRepo.hasUser(userName);
    }

    public boolean validPassword(String password, String password2) {
        if (password.equals(password2)) {
            return true;
        }
        return false;
    }

    public String generateUserId() {
        return UUID.randomUUID().toString();
    }

    public void register(RegisterUser registerUser) {
        User newUser = new User();

        newUser.setUserId(generateUserId());
        newUser.setUserName(registerUser.getUserName());
        newUser.setPassword(registerUser.getPassword());
        userRepo.addUser(newUser);
    }
}

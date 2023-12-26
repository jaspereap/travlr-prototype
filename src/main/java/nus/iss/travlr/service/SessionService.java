package nus.iss.travlr.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nus.iss.travlr.repository.SessionRepository;

@Service
public class SessionService {
    @Autowired
    private SessionRepository sessRepo;

    public void loginUser(String sessId, String userName) {
        sessRepo.loginUser(sessId, userName);
    }

    public Optional<String> getUserName(String sessId) {
        return Optional.ofNullable(sessRepo.getCurrentUserId(sessId));
    }

    public void logoutUser(String sessId) {
        sessRepo.logoutUser(sessId);
    }
}

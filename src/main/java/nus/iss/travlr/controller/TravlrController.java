package nus.iss.travlr.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import nus.iss.travlr.model.Itinerary;
import nus.iss.travlr.model.User;
import nus.iss.travlr.service.AccountService;
import nus.iss.travlr.service.SessionService;
import nus.iss.travlr.service.TravlrService;

@Controller
@RequestMapping
public class TravlrController {

    @Autowired
    private SessionService sessSvc;
    @Autowired
    private AccountService accSvc;
    @Autowired
    private TravlrService travSvc;

    @GetMapping(path = "/home")
    public String getHome(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        String currentSessionId = session.getId();
        Optional<String> optUserName = sessSvc.getUserName(currentSessionId);
        if (!optUserName.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login first.");
            return "redirect:/login";
        }
        String userName = optUserName.get();
        User retrievedUser = accSvc.getUser(userName);
        model.addAttribute("user", retrievedUser);

        // Get all itineraries
        Optional<List<Itinerary>> optItineraryList = travSvc.getItinerary(retrievedUser.getUserName());
        if (optItineraryList.isPresent()) {
            model.addAttribute("itineraries", optItineraryList.get());
        }
        return "home";
    }

    @PostMapping(path = "/create")
    public String postCreate(@RequestParam("name") String name, @RequestParam("country") String country, @RequestParam("description") String description, Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        String currentSessionId = session.getId();
        Optional<String> optUserName = sessSvc.getUserName(currentSessionId);
        if (!optUserName.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login first.");
            return "redirect:/login";
        }
        String userName = optUserName.get();
        User retrievedUser = accSvc.getUser(userName);

        // TODO add validations for adding itinerary
        travSvc.addItinerary(retrievedUser.getUserName(), new Itinerary(userName, country, description));

        return "redirect:/home";
    }
}

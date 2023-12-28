package nus.iss.travlr.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import nus.iss.travlr.model.Activity;
import nus.iss.travlr.model.Itinerary;
import nus.iss.travlr.model.User;
import nus.iss.travlr.service.AccountService;
import nus.iss.travlr.service.SessionService;
import nus.iss.travlr.service.ItineraryService;

@Controller
@RequestMapping
public class ItineraryController {

    @Autowired
    private SessionService sessSvc;
    @Autowired
    private AccountService accSvc;
    @Autowired
    private ItineraryService itiSvc;

    // Home page
    @GetMapping(path = "/home")
    public String getHome(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (!sessSvc.isLoggedIn(session.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login first.");
            return "redirect:/login";
        }
        String sessionUserName = sessSvc.getUserName(session.getId()).get();
        User sessionUser = accSvc.getUser(sessionUserName);
        model.addAttribute("user", sessionUser);
        // Get all itineraries
        Optional<List<Itinerary>> optItineraryList = itiSvc.getItinerary(sessionUser.getUserName());
        if (optItineraryList.isPresent()) {
            model.addAttribute("itineraries", optItineraryList.get());
        }
        return "home";
    }

    // Create a new itinerary
    @PostMapping(path = "/create")
    public String postCreate(@RequestParam("name") String name,
                            @RequestParam("country") String country, 
                            @RequestParam("description") String description,
                            Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        String currentSessionId = session.getId();
        Optional<String> optUserName = sessSvc.getUserName(currentSessionId);
        if (!optUserName.isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login first.");
            return "redirect:/login";
        }
        String userName = optUserName.get();
        User retrievedUser = accSvc.getUser(userName);

        itiSvc.addItinerary(retrievedUser.getUserName(), new Itinerary(name, country, description));

        return "redirect:/home";
    }

    // Delete a itinerary
    @GetMapping(path = "/itinerary/{userName}/{iid}/delete")
    public String getActivity(@PathVariable String userName, 
                                @PathVariable Integer iid,
                                HttpSession session, RedirectAttributes redirectAttributes, Model model) {

        if (!sessSvc.isLoggedIn(session.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login first.");
            return "redirect:/login";
        }
        String sessionUserName = sessSvc.getUserName(session.getId()).get();
        User sessionUser = accSvc.getUser(sessionUserName);
        model.addAttribute("user", sessionUser);
        model.addAttribute("userName", userName);
        model.addAttribute("iid", iid);
        model.addAttribute("activity", new Activity());

        itiSvc.deleteItinerary(sessionUserName, iid);
        return "redirect:/home";
    }
}

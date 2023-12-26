package nus.iss.travlr.controller;

import java.time.LocalDateTime;
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
 
        if (!sessSvc.isLoggedIn(session.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login first.");
            return "redirect:/login";
        }

        String sessionUserName = sessSvc.getUserName(session.getId()).get();
        User sessionUser = accSvc.getUser(sessionUserName);
        model.addAttribute("user", sessionUser);

        // Get all itineraries
        Optional<List<Itinerary>> optItineraryList = travSvc.getItinerary(sessionUser.getUserName());
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

        // TODO add validations for adding itinerary
        travSvc.addItinerary(retrievedUser.getUserName(), new Itinerary(name, country, description));

        return "redirect:/home";
    }

    // Retrieve a specific itinerary for a user
    @GetMapping(path = "/itinerary/{userName}/{iid}")
    public String getItinerary(@PathVariable String userName, 
                                @PathVariable Integer iid, 
                                Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!sessSvc.isLoggedIn(session.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login first.");
            return "redirect:/login";
        }
        String sessionUserName = sessSvc.getUserName(session.getId()).get();
        User sessionUser = accSvc.getUser(sessionUserName);
        if (!sessionUserName.equals(userName)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorised!");
            return "redirect:/error";
        }
        // Get itinerary
        Optional<List<Itinerary>> optItineraryList = travSvc.getItinerary(sessionUserName);
        if (optItineraryList.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Unauthorised!");
            return "redirect:/error";
        }
        try {
            List<Itinerary> itineraryList = optItineraryList.get();
            model.addAttribute("itinerary", itineraryList.get(iid));
        } catch (IndexOutOfBoundsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Itinerary doesn't exist!");
            return "redirect:/error";
        }
        model.addAttribute("user", sessionUser);
        model.addAttribute("iid", iid);
        return "itineraryView";
    }

    // Add activity to an itinerary for a specific user
    @PostMapping(path = "/itinerary/{userName}/{iid}/add")
    public String postAddActivity(@PathVariable String userName, 
                                @PathVariable Integer iid,
                                @RequestParam("location") String location,
                                @RequestParam("datetime") LocalDateTime dateTime,
                                @RequestParam("image") String image,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        System.out.println(userName + iid);
        if (!sessSvc.isLoggedIn(session.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login first.");
            return "redirect:/login";
        }

        Activity activity = new Activity(location, dateTime, image, image);
        // add activity to list
        travSvc.addActivity(userName, iid, activity);
        return "redirect:/itinerary/" + userName + "/" + iid;
    }
}

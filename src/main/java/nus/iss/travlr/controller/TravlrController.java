package nus.iss.travlr.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
            Itinerary itinerary = itineraryList.get(iid);
            model.addAttribute("itinerary", itinerary);
        } catch (IndexOutOfBoundsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Itinerary doesn't exist!");
            return "redirect:/error";
        }
        model.addAttribute("user", sessionUser);
        model.addAttribute("iid", iid);

        return "itineraryView";
    }

    @GetMapping(path = "/itinerary/{userName}/{iid}/add")
    public String getAddActivity(@PathVariable String userName, 
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
        return "newActivity";
    }

    // Delete itinerary
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

        travSvc.deleteItinerary(sessionUserName, iid);
        return "redirect:/home";
    }

    // Add activity to an itinerary for a specific user
    @PostMapping(path = "/itinerary/{userName}/{iid}/add")
    public String postAddActivity(@PathVariable String userName, 
                                @PathVariable Integer iid,
                                @RequestParam MultiValueMap<String,String> form,
                                @ModelAttribute Activity activity,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        if (!sessSvc.isLoggedIn(session.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login first.");
            return "redirect:/login";
        }

        // TODO Validations for activity form

        // If Edit exisiting activity
        if (form.getFirst("aid") != null) {
            // Debug
            System.out.println("Editing activity");
            String aid = form.getFirst("aid");
            travSvc.updateActivity(userName, iid, aid, activity);
            return "redirect:/itinerary/" + userName + "/" + iid;
        }
        Activity populatedActivity = travSvc.populateNewActivity(activity);
        travSvc.addActivity(userName, iid, populatedActivity);

        return "redirect:/itinerary/" + userName + "/" + iid;
    }

    @GetMapping(path = "/itinerary/{userName}/{iid}/{aid}/edit")
    public String getEditActivity(@PathVariable String userName, 
                                @PathVariable String iid,
                                @PathVariable String aid,
                                HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        System.out.println(userName + iid);
        if (!sessSvc.isLoggedIn(session.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login first.");
            return "redirect:/login";
        }
        String sessionUserName = sessSvc.getUserName(session.getId()).get();
        User sessionUser = accSvc.getUser(sessionUserName);
        model.addAttribute("user", sessionUser);
        model.addAttribute("userName", userName);
        model.addAttribute("iid", iid.toString());
        model.addAttribute("aid", aid);
        // Retrieve activity
        Itinerary itinerary = travSvc.getItinerary(sessionUserName).get().get(Integer.parseInt(iid));
        ArrayList<Activity> activityList = itinerary.getActivityList();
        for (Activity activity : activityList) {
            System.out.println(activity.getId());
            if (activity.getId().equals(aid.toString())) {
                model.addAttribute("activity", activity);
                return "newActivity";
            }
        }

        return "error";
    }
}

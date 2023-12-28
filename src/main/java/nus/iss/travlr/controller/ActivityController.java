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
import nus.iss.travlr.service.ActivityService;
import nus.iss.travlr.service.SessionService;
import nus.iss.travlr.service.ItineraryService;

@Controller
@RequestMapping
public class ActivityController {
    @Autowired
    private SessionService sessSvc;
    @Autowired
    private AccountService accSvc;
    @Autowired
    private ItineraryService itiSvc;
    @Autowired
    private ActivityService actSvc;

    // Get activities from an itinerary
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
        Optional<List<Itinerary>> optItineraryList = itiSvc.getItinerary(sessionUserName);
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
    // Get form for adding new activity
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

    // Get form for editing exisiting activity
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
        Itinerary itinerary = itiSvc.getItinerary(sessionUserName).get().get(Integer.parseInt(iid));
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

    // Adds or Edits activity to an itinerary for a specific user
    @PostMapping(path = "/itinerary/{userName}/{iid}/add")
    public String postAddActivity(@PathVariable String userName, 
                                @PathVariable Integer iid,
                                @RequestParam MultiValueMap<String,String> form,
                                @Valid @ModelAttribute Activity activity, BindingResult result,
                                HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (!sessSvc.isLoggedIn(session.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login first.");
            return "redirect:/login";
        }
        User sessionUser = accSvc.getUser(sessSvc.getUserName(session.getId()).get());
        model.addAttribute("user", sessionUser);
        if (result.hasErrors()) {
            // return back form with error messages
            System.out.println("Has error, returning newActivity page");
            model.addAttribute("submitted", true);
            return "newActivity";
        }
        // If Edit exisiting activity
        if (form.getFirst("aid") != null) {
            // Debug
            System.out.println("Editing activity");
            String aid = form.getFirst("aid");
            actSvc.updateActivity(userName, iid, aid, activity);
            return "redirect:/itinerary/" + userName + "/" + iid;
        }
        Activity populatedActivity = actSvc.populateNewActivity(activity);
        actSvc.addActivity(userName, iid, populatedActivity);
        return "redirect:/itinerary/" + userName + "/" + iid;
    }

    // Delete an activity
    @GetMapping(path = "/itinerary/{userName}/{iid}/{aid}/delete")
    public String getDeleteActivity(@PathVariable String userName, 
                                @PathVariable String iid,
                                @PathVariable String aid,
                                HttpSession session, RedirectAttributes redirectAttributes, Model model) {
        if (!sessSvc.isLoggedIn(session.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please login first.");
            return "redirect:/login";
        }
        User sessionUser = accSvc.getUser(sessSvc.getUserName(session.getId()).get());
        model.addAttribute("user", sessionUser);

        actSvc.deleteActivity(userName, iid, aid);
        return "redirect:/itinerary/" + userName + "/" + iid;
    }
}

package nus.iss.travlr.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import nus.iss.travlr.model.Activity;
import nus.iss.travlr.model.Itinerary;
import nus.iss.travlr.utility.TravlrUtility;

@Repository
public class ActivityRepository {
    @Autowired @Qualifier("redisTemplate")
    private RedisTemplate<String, String> template;
    @Autowired 
    private ItineraryRepository itiRepo;

    public void addActivity(String userName, Integer iid, Activity activity) {
        // get current itinerary
        Optional<List<Itinerary>> optItineraryList = itiRepo.getItinerary(userName);
        List<Itinerary> itineraryList = optItineraryList.get();
        
        // amend activity of itinerary
        itineraryList.get(iid).add(activity);

        JsonArrayBuilder jab = Json.createArrayBuilder();
        // iterate list, create a json array of objects
        for (Itinerary iti : itineraryList) {
            jab.add(iti.toJsonObject());
        }
        JsonArray jarr = jab.build();
        // Debug
        System.out.println("Packed Activity Array: " + jarr);
        template.opsForHash().put("main", userName, jarr.toString());
    }

    public void deleteActivity(String userName, Integer iid, String aid) {
        // Get current itinerary list
        List<Itinerary> itineraryList = itiRepo.getItinerary(userName).get();
        // Get itinerary
        Itinerary itinerary = itineraryList.get(iid);
        // Get activity list
        ArrayList<Activity> activityList = itinerary.getActivityList();
        for (int i = 0; i < activityList.size(); i++) {
            if (activityList.get(i).getId().equals(aid)) {
                activityList.remove(i);
            }
        }
        // Jsonize itinerary list
        JsonArray jarr = TravlrUtility.itiListTJsonArray(itineraryList);
        // put itinerary list back
        template.opsForHash().put("main", userName, jarr.toString());
    }

    public void updateActivity(String userName, Integer iid, String aid, Activity activity) {
        // get iid itinerary
        Optional<List<Itinerary>> optItineraryList = itiRepo.getItinerary(userName);
        List<Itinerary> itineraryList = optItineraryList.get();
        Itinerary itinerary = itineraryList.get(iid);
        // get aid activity
        // amend activity
        ArrayList<Activity> activityList = itinerary.getActivityList();
        for (int i = 0; i < activityList.size(); i++) {
            if (activityList.get(i).getId().equals(aid)) {
                activityList.set(i, activity);
            }
        }
        // Build array of itinerary objects
        JsonArrayBuilder jab = Json.createArrayBuilder();
        // iterate list, create a json array of objects
        for (Itinerary iti : itineraryList) {
            jab.add(iti.toJsonObject());
        }
        JsonArray jarr = jab.build();
        // put back itinerary list hash
        template.opsForHash().put("main", userName, jarr.toString());
    }

    public boolean moveActivity(String userName, Integer iid, String aid, Integer offset) {
        // Get current itinerary list
        List<Itinerary> itineraryList = itiRepo.getItinerary(userName).get();
        // Get itinerary
        Itinerary itinerary = itineraryList.get(iid);
        // Get activity list
        ArrayList<Activity> activityList = itinerary.getActivityList();

        int position = -1;
        for (int i = 0; i < activityList.size(); i++) {
            if (activityList.get(i).getId().equals(aid)) {
                position = i;
            }
        }
        // check within range
        if (position + offset < 0 | position + offset >= activityList.size()) {
            return false;
        }
        // swap item
        int currentIndex = position;
        int newIndex = position + offset;
        Activity temp = activityList.get(currentIndex); // Store current activity temporarily
        activityList.set(currentIndex, activityList.get(newIndex)); // Store new onto old
        activityList.set(newIndex, temp); // Store old onto new
        // Jsonize itinerary list
        JsonArray jarr = TravlrUtility.itiListTJsonArray(itineraryList);
        // put itinerary list back
        template.opsForHash().put("main", userName, jarr.toString());
        return true;
    }
}

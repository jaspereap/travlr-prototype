package nus.iss.travlr.repository;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import nus.iss.travlr.model.Activity;
import nus.iss.travlr.model.Itinerary;
import nus.iss.travlr.utility.TravlrUtility;

@Repository
public class ItineraryRepository {
    @Autowired @Qualifier("redisTemplate")
    private RedisTemplate<String, String> template;

    public void deleteItinerary(String userName, Integer iid) {
        // Get current itinerary list
        List<Itinerary> itineraryList = this.getItinerary(userName).get();
        // delete index iid
        itineraryList.remove((int) iid);
        // Jsonize itinerary list
        JsonArray jarr = TravlrUtility.itiListTJsonArray(itineraryList);
        // put itinerary list back
        template.opsForHash().put("main", userName, jarr.toString());
    }

    // Add a new itinerary with empty activity
    public void addItinerary(String userName, Itinerary itinerary) {
        // get current itinerary
        Optional<List<Itinerary>> optItineraryList = this.getItinerary(userName);
        // if empty, create a new one
        if (optItineraryList.isEmpty()) {
            JsonArray jarr = Json
                .createArrayBuilder()
                .add(itinerary.toJsonObject())
                .build();
            System.out.println("Packed JsonArray: " + jarr.toString());
            template.opsForHash().put("main", userName, jarr.toString());
        }
        else {
            List<Itinerary> itineraryList = optItineraryList.get();
            itineraryList.add(itinerary);
            JsonArray jarr = TravlrUtility.itiListTJsonArray(itineraryList);
            template.opsForHash().put("main", userName, jarr.toString());
        }
    }

    // Retrieve all itineraries from a user
    public Optional<List<Itinerary>> getItinerary(String userName) {
        String retrievedItinerary = (String) template.opsForHash().get("main", userName);
        if (retrievedItinerary == null) {
            return Optional.empty();
        }
        JsonReader jr = Json.createReader(new StringReader(retrievedItinerary));
        JsonArray itineraryArray = jr.readArray();
        List<Itinerary> itineraryList = new ArrayList<>();
        for (JsonValue val : itineraryArray) {
            JsonObject itineraryObject = val.asJsonObject();
            // Itinerary Properties
            String name = itineraryObject.getString("name");
            String country = itineraryObject.getString("country");
            String description = itineraryObject.getString("description");
            JsonArray activityArray = itineraryObject.getJsonArray("activityList");
            ArrayList<Activity> activityList = new ArrayList<>();
                for (JsonValue val2 : activityArray) {
                    // Activity Properties
                    JsonObject activityObject = val2.asJsonObject();
                    String id = activityObject.getString("id", "");
                    String location = activityObject.getString("location");
                    String address = activityObject.getString("address");
                    String dateTime = activityObject.getString("dateTime");
                    String image = activityObject.getString("image", "");
                    String remarks = activityObject.getString("remarks", "");
                    String lat = activityObject.getString("lat", "");
                    String lng = activityObject.getString("lng", "");
                    String formatted_address = activityObject.getString("formatted_address", "");
                    String place_id = activityObject.getString("place_id", "");
                    Activity activity = new Activity(id, location, address, LocalDateTime.parse(dateTime), image, remarks, lat, lng, formatted_address, place_id);
                    activityList.add(activity);
                }
            itineraryList.add(new Itinerary(name, country, description, activityList));
        }
        return Optional.of(itineraryList);
    }
}

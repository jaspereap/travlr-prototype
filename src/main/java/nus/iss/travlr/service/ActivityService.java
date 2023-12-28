package nus.iss.travlr.service;

import java.io.StringReader;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import nus.iss.travlr.model.Activity;
import nus.iss.travlr.repository.ActivityRepository;

@Service
public class ActivityService {
    @Value("${google.api.key}")
    private String API_KEY;
    @Value("${geocode.url}")
    private String GEOCODE_URL;
    @Autowired
    private ActivityRepository actRepo;

    public void addActivity(String userName, Integer iid, Activity activity) {
        actRepo.addActivity(userName, iid, activity);
    }

    public void deleteActivity(String userName, String iid, String aid) {
        actRepo.deleteActivity(userName, Integer.parseInt(iid), aid);
    }

    public void updateActivity(String userName, Integer iid, String aid, Activity activity ) {
        Activity populatedActivity = this.populateExistingActivity(aid, activity);
        actRepo.updateActivity(userName, iid, aid, populatedActivity);
    }

    public boolean moveActivity(String userName, Integer iid, String aid, Integer offset) {
        return actRepo.moveActivity(userName, iid, aid, offset);
    }

    public Activity populateNewActivity(Activity activity) {
        // Add new activity
        activity.initialiseId();
        String address = activity.getAddress();
        // Call API with address
        JsonObject response = this.getAddressDetails(address);
        if (response == null) {
            // Debug
            System.out.println("API call failed");
            return activity;
        }
        String lat = response.getString("lat");
        String lng = response.getString("lng");
        String formatted_address = response.getString("formatted_address");
        String place_id = response.getString("place_id");
        activity.setLat(lat);
        activity.setLng(lng);
        activity.setFormatted_address(formatted_address);
        activity.setPlace_id(place_id);
        return activity;
    }

    public Activity populateExistingActivity(String aid, Activity activity) {
        // Add new activity
        activity.setId(aid);
        String address = activity.getAddress();
        // Call API with address
        JsonObject response = this.getAddressDetails(address);
        if (response == null) {
            return activity;
        }
        String lat = response.getString("lat");
        String lng = response.getString("lng");
        String formatted_address = response.getString("formatted_address");
        String place_id = response.getString("place_id");
        activity.setLat(lat);
        activity.setLng(lng);
        activity.setFormatted_address(formatted_address);
        activity.setPlace_id(place_id);
        return activity;
    }

    public JsonObject getAddressDetails(String address) {
        System.out.println("\t Calling Google API..");
        String url = GEOCODE_URL;
        String endpoint = UriComponentsBuilder
            .fromUriString(url)
            .queryParam("key", API_KEY)
            .queryParam("address", address)
            .build()
            .toString();

        RestTemplate template = new RestTemplate();
        String response = template.getForObject(endpoint, String.class);

        JsonReader reader = Json.createReader(new StringReader(response));
        JsonObject responseObj = reader.readObject();
        String status = responseObj.getString("status");
        // Status Codes -> OK, ZERO_RESULTS, OVER_DAILY_LIMIT, OVER_QUERY_LIMIT, REQUEST_DENIED, INVALID_REQUEST, UNKNOWN_ERROR
        if (!status.equals("OK")) {
            return null;
        }
        JsonObject results = responseObj.getJsonArray("results").getJsonObject(0);
        JsonObject location = results.getJsonObject("geometry").getJsonObject("location");

        String formatted_address = results.getString("formatted_address");
        String place_id = results.getString("place_id");
        String lat = location.getJsonNumber("lat").toString();
        String lng = location.getJsonNumber("lng").toString();
        System.out.printf("Formatted address: %s, \nplace id: %s, \nlat: %s, \nlng: %s\n", formatted_address, place_id, lat, lng);

        JsonObjectBuilder returnBuilder = Json.createObjectBuilder();
        JsonObject compiled = returnBuilder
        .add("lat", lat)
        .add("lng", lng)
        .add("formatted_address", formatted_address)
        .add("place_id", place_id)
        .build();
        return compiled;
    }
}

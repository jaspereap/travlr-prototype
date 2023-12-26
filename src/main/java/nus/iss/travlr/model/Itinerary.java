package nus.iss.travlr.model;

import java.util.ArrayList;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Itinerary {
    private String name;
    private String country;
    private String description;

    private ArrayList<Activity> activityList = new ArrayList<>();

    public Itinerary(String name, String country, String description) {
        this.name = name;
        this.country = country;
        this.description = description;
    }

    public Itinerary(String name, String country, String description, ArrayList<Activity> activityList) {
        this.name = name;
        this.country = country;
        this.description = description;
        this.activityList = activityList;
    }

    // Prototype
    public void add(Activity destination) {
        this.activityList.add(destination);
    }

    public JsonArray serializeActivity() {
        JsonArrayBuilder jab = Json.createArrayBuilder();
        for (Activity activity : activityList) {
            jab.add(activity.toJsonObject());
        }
        return jab.build();
    }

    public JsonObject toJsonObject() {
        JsonArray jar = this.serializeActivity();
        JsonObject job = Json.createObjectBuilder()
        .add("name", name)
        .add("country", country)
        .add("description", description)
        .add("activityList", jar)
        .build();
        return job;
    }
}

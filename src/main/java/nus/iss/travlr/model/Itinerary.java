package nus.iss.travlr.model;

import java.util.ArrayList;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Itinerary {
    private String name;
    private String country;
    private String description;

    private ArrayList<String> list = new ArrayList<>();

    public Itinerary(String name, String country, String description) {
        this.name = name;
        this.country = country;
        this.description = description;
    }

    // Prototype
    public void add(String destination) {
        this.list.add(destination);
    }

    public JsonObject toJsonObject() {
        JsonArray jar = Json.createArrayBuilder(list).build();
        JsonObject job = Json.createObjectBuilder()
        .add("name", name)
        .add("country", country)
        .add("description", description)
        .add("list", jar)
        .build();
        return job;
    }
}

package nus.iss.travlr.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    private String id;
    // From Form
    private String location;
    private String address;
    private LocalDateTime dateTime;
    private String image;
    private String remarks;
    // From API
    private String lat = "";
    private String lng = "";
    private String formatted_address = "";
    private String place_id = "";

    // Constructor for Form
    public Activity(String location, String address, LocalDateTime dateTime, String image, String remarks) {
        this.location = location;
        this.address = address;
        this.dateTime = dateTime;
        this.image = image;
        this.remarks = remarks;
    }

    public void initialiseId() {
        this.id = UUID.randomUUID().toString();
    }

    public JsonObject toJsonObject() {
        JsonObject job = Json.createObjectBuilder()
        .add("id", id)
        // From Form
        .add("location", location)
        .add("address", address)
        .add("dateTime", dateTime.toString())
        .add("image", image)
        .add("remarks", remarks)
        // From API
        .add("lat", lat)
        .add("lng", lng)
        .add("formatted_address", formatted_address)
        .add("place_id", place_id)
        .build();
        return job;
    }
}

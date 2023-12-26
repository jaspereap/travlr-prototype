package nus.iss.travlr.model;

import java.time.LocalDateTime;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    private String location;
    private LocalDateTime dateTime;
    private String image;

    private String geocode;

    public JsonObject toJsonObject() {
        JsonObject job = Json.createObjectBuilder()
        .add("location", location)
        .add("dateTime", dateTime.toString())
        .add("image", image)
        .build();
        return job;
    }
}

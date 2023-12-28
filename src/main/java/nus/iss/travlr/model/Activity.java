package nus.iss.travlr.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.validator.constraints.Length;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Activity {
    private String id;
    // From Form
    @NotBlank(message = "Please provide a valid location name.")
    @Length(min=5, max=30, message="Name must be between 5 - 30 characters.")
    private String location;
    @NotBlank(message = "Please provide a valid address.")
    @Length(min=5, max=60, message="Location must be between 5 - 60 characters.")
    private String address;
    @NotNull(message="Please provide a valid date.")
    @Future(message="Date must be in the future")
    private LocalDateTime dateTime;
    @Length(max=100, message="Limited to 100 characters.")
    private String image;
    @Length(max=100, message="Limited to 100 characters.")
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

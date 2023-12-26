package nus.iss.travlr.model;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String userId;
    @NotBlank(message = "Please input your username!")
    private String userName;
    @NotBlank(message = "Please input your password!")
    private String password;

    public JsonObject toJsonObject() {
        JsonObject job = Json.createObjectBuilder()
        .add("userId", this.userId)
        .add("userName", this.userName)
        .add("password", this.password)
        .build();
        return job;
    }
}

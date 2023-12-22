package nus.iss.travlr.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterUser {
    @NotBlank(message = "Please input your desired username.")
    private String userName;
    @NotBlank(message = "Please input your desired password.")
    private String password;
    @NotBlank(message = "Please type your password again.")
    private String password2;
}

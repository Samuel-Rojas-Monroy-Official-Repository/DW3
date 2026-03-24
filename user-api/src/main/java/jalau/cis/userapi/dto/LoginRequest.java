package jalau.cis.userapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "El login es obligatorio")
    private String login;

    @NotBlank(message = "El password es obligatorio")
    private String password;
}

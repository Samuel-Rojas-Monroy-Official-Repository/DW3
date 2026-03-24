package jalau.cis.userapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateUserRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "El login es obligatorio")
    @Size(min = 3, max = 50, message = "El login debe tener entre 3 y 50 caracteres")
    private String login;

    @NotBlank(message = "El password es obligatorio")
    @Size(min = 6, message = "El password debe tener minimo 6 caracteres")
    private String password;
}

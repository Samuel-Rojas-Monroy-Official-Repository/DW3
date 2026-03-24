package jalau.cis.userapi.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserRequest {
    private String name;

    @Size(min = 6, message = "El password debe tener minimo 6 caracteres")
    private String password;
}

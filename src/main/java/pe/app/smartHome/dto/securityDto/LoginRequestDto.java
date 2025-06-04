package pe.app.smartHome.dto.securityDto;

import lombok.Data;

@Data
public class LoginRequestDto {

    private String username;
    private String password;
}
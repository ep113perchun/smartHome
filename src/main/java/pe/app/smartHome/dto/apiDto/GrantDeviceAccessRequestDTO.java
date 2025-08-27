package pe.app.smartHome.dto.apiDto;

import lombok.Data;
import java.util.List;

@Data
public class GrantDeviceAccessRequestDTO {
    private String deviceId;
    private List<String> usernames;
} 
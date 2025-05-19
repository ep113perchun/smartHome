package pe.app.smartHome.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class ScenarioDTO {
    private String id;
    private String name;
    private String description;
    private String color;

    @JsonProperty("is_active")
    private boolean isActive;

    private List<DeviceDTO> devices;
}

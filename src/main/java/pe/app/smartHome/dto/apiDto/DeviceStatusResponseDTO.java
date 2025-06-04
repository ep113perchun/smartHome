package pe.app.smartHome.dto.apiDto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class DeviceStatusResponseDTO {
    private String id;
    private boolean status;

    @JsonProperty("last_update")
    private ZonedDateTime lastUpdate;
}

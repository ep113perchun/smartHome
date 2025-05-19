package pe.app.smartHome.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
public class DeviceDTO {
    private String id;
    private String name;
    private boolean status;
    private String type;

    @JsonProperty("last_update")
    private ZonedDateTime lastUpdate;

    @JsonProperty("room_id")
    private String roomId;
}


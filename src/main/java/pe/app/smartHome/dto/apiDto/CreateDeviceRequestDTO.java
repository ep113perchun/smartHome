package pe.app.smartHome.dto.apiDto;

import lombok.Data;

@Data
public class CreateDeviceRequestDTO {
    private String name;
    private String type;
    private Integer relayId;
    private String mac;
} 
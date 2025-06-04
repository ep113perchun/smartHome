package pe.app.smartHome.dto.apiDto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RoomDTO {
    private String id;
    private String name;
    private String color;
    private List<DeviceDTO> devices = new ArrayList<>();
}


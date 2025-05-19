package pe.app.smartHome.dto;

import lombok.Data;

import java.util.List;

@Data
public class UpdateRoomRequestDTO {
    private String name;
    private String color;
    private List<String> deviceIds;
}

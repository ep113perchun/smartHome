package pe.app.smartHome.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateRoomRequestDTO {
    private String name;
    private String color;
    private List<String> deviceIds;
}
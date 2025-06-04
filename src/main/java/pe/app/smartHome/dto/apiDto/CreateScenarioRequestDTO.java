package pe.app.smartHome.dto.apiDto;

import lombok.Data;

import java.util.List;

@Data
public class CreateScenarioRequestDTO {
    private String name;
    private String description;
    private String color;
    private List<String> deviceIds;
}


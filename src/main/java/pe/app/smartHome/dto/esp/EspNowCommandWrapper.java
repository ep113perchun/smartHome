package pe.app.smartHome.dto.esp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class EspNowCommandWrapper {
    @JsonProperty("commands")
    private List<EspNowCommand> commands;
}
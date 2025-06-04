package pe.app.smartHome.dto.esp;

import lombok.Data;

import java.util.List;

@Data
public class EspNowCommandWrapper {
    private List<EspNowCommand> commands;
}
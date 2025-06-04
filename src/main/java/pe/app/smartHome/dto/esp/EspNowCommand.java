package pe.app.smartHome.dto.esp;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EspNowCommand {
    private String mac;
    private List<Integer> relayIds;
    private List<Boolean> states;

}
package pe.app.smartHome.dto.esp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class EspNowCommand {
    @JsonProperty("mac")
    private String mac;
    
    @JsonProperty("relayIds")
    private List<Integer> relayIds;
    
    @JsonProperty("states")
    private List<Boolean> states;
}
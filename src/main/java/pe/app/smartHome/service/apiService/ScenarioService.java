package pe.app.smartHome.service.apiService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.app.smartHome.dto.apiDto.CreateScenarioRequestDTO;
import pe.app.smartHome.dto.apiDto.DeviceDTO;
import pe.app.smartHome.dto.apiDto.ScenarioDTO;
import pe.app.smartHome.dto.apiDto.UpdateScenarioRequestDTO;
import pe.app.smartHome.repository.apiRepository.ScenarioRepository;
import pe.app.smartHome.repository.securityRepository.UserRepository;
import pe.app.smartHome.entity.User;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScenarioService {
    private final ScenarioRepository scenarioRepository;
    private final DeviceService deviceService;
    private final UserRepository userRepository;

    public ScenarioService(ScenarioRepository scenarioRepository, DeviceService deviceService, UserRepository userRepository) {
        this.scenarioRepository = scenarioRepository;
        this.deviceService = deviceService;
        this.userRepository = userRepository;
    }

    public List<ScenarioDTO> getScenariosByUsername(String username) {
        return scenarioRepository.findByUser(username);
    }

    public ScenarioDTO getScenarioById(String id) {
        return scenarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сценарий не найден"));
    }

    @Transactional
    public ScenarioDTO createScenario(CreateScenarioRequestDTO request, String username) {
        // Получаем пользователя
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        ScenarioDTO scenario = new ScenarioDTO();
        scenario.setId(UUID.randomUUID().toString());
        scenario.setName(request.getName());
        scenario.setDescription(request.getDescription());
        scenario.setColor(request.getColor());
        scenario.setActive(true);

        // Получаем устройства по их ID
        if (request.getDeviceIds() != null) {
            List<DeviceDTO> devices = request.getDeviceIds().stream()
                    .map(deviceService::getDeviceById)
                    .collect(Collectors.toList());
            scenario.setDevices(devices);
        }

        return scenarioRepository.create(scenario, user.getId());
    }

    public ScenarioDTO updateScenario(String id, UpdateScenarioRequestDTO request) {
        ScenarioDTO scenario = getScenarioById(id);
        scenario.setName(request.getName());
        scenario.setDescription(request.getDescription());
        scenario.setColor(request.getColor());

        // Получаем устройства по их ID
        if (request.getDeviceIds() != null) {
            List<DeviceDTO> devices = request.getDeviceIds().stream()
                    .map(deviceService::getDeviceById)
                    .collect(Collectors.toList());
            scenario.setDevices(devices);
        }

        scenarioRepository.update(id, scenario);
        return scenario;
    }

    public void deleteScenario(String id) {
        scenarioRepository.delete(id);
    }
}
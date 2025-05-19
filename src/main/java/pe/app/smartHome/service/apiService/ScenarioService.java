package pe.app.smartHome.service.apiService;

import org.springframework.stereotype.Service;
import pe.app.smartHome.dto.*;
import pe.app.smartHome.repository.apiRepository.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScenarioService {
    private final ScenarioRepository scenarioRepository;
    private final DeviceService deviceService;

    public ScenarioService(ScenarioRepository scenarioRepository, DeviceService deviceService) {
        this.scenarioRepository = scenarioRepository;
        this.deviceService = deviceService;
    }

    public List<ScenarioDTO> getAllScenarios() {
        return scenarioRepository.findAll();
    }

    public ScenarioDTO getScenarioById(String id) {
        return scenarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сценарий не найден"));
    }

    public ScenarioDTO createScenario(CreateScenarioRequestDTO request) {
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

        return scenarioRepository.create(scenario);
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
package pe.app.smartHome.repository.apiRepository;

import pe.app.smartHome.dto.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import pe.app.smartHome.service.apiService.DeviceService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ScenarioRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<ScenarioDTO> scenarioRowMapper;
    private final DeviceService deviceService;

    public ScenarioRepository(JdbcTemplate jdbcTemplate, DeviceService deviceService) {
        this.jdbcTemplate = jdbcTemplate;
        this.deviceService = deviceService;
        this.scenarioRowMapper = (rs, rowNum) -> {
            ScenarioDTO scenario = new ScenarioDTO();
            scenario.setId(rs.getString("id"));
            scenario.setName(rs.getString("name"));
            scenario.setDescription(rs.getString("description"));
            scenario.setColor(rs.getString("color"));
            scenario.setActive(rs.getBoolean("is_active"));

            // Получаем ID устройств
            List<String> deviceIds = getScenarioDevices(scenario.getId());
            // Преобразуем ID в объекты DeviceDTO
            List<DeviceDTO> devices = deviceIds.stream()
                    .map(deviceService::getDeviceById)
                    .collect(Collectors.toList());
            scenario.setDevices(devices);

            return scenario;
        };
    }

    public List<ScenarioDTO> findAll() {
        return jdbcTemplate.query("SELECT * FROM scenarios", scenarioRowMapper);
    }

    public Optional<ScenarioDTO> findById(String id) {
        List<ScenarioDTO> scenarios = jdbcTemplate.query(
                "SELECT * FROM scenarios WHERE id = ?",
                scenarioRowMapper,
                id
        );
        return scenarios.isEmpty() ? Optional.empty() : Optional.of(scenarios.get(0));
    }

    public ScenarioDTO create(ScenarioDTO scenario) {
        jdbcTemplate.update(
                "INSERT INTO scenarios (id, name, description, color, is_active) VALUES (?, ?, ?, ?, ?)",
                scenario.getId(), scenario.getName(), scenario.getDescription(),
                scenario.getColor(), scenario.isActive()
        );

        // Сохраняем связи с устройствами
        if (scenario.getDevices() != null) {
            for (DeviceDTO device : scenario.getDevices()) {
                jdbcTemplate.update(
                        "INSERT INTO scenario_devices (scenario_id, device_id) VALUES (?, ?)",
                        scenario.getId(), device.getId()
                );
            }
        }

        return scenario;
    }

    public void update(String id, ScenarioDTO scenario) {
        jdbcTemplate.update(
                "UPDATE scenarios SET name = ?, description = ?, color = ? WHERE id = ?",
                scenario.getName(), scenario.getDescription(), scenario.getColor(), id
        );

        // Обновляем связи с устройствами
        jdbcTemplate.update("DELETE FROM scenario_devices WHERE scenario_id = ?", id);
        if (scenario.getDevices() != null) {
            for (DeviceDTO device : scenario.getDevices()) {
                jdbcTemplate.update(
                        "INSERT INTO scenario_devices (scenario_id, device_id) VALUES (?, ?)",
                        id, device.getId()
                );
            }
        }
    }

    public void delete(String id) {
        jdbcTemplate.update("DELETE FROM scenarios WHERE id = ?", id);
    }

    private List<String> getScenarioDevices(String scenarioId) {
        return jdbcTemplate.queryForList(
                "SELECT device_id FROM scenario_devices WHERE scenario_id = ?",
                String.class,
                scenarioId
        );
    }
}
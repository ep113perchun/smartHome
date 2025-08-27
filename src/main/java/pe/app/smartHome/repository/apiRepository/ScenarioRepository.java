package pe.app.smartHome.repository.apiRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.app.smartHome.dto.apiDto.DeviceDTO;
import pe.app.smartHome.dto.apiDto.ScenarioDTO;
import pe.app.smartHome.service.apiService.DeviceService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ScenarioRepository {
    private static final Logger logger = LoggerFactory.getLogger(ScenarioRepository.class);
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<ScenarioDTO> scenarioRowMapper;

    public ScenarioRepository(JdbcTemplate jdbcTemplate, DeviceService deviceService) {
        this.jdbcTemplate = jdbcTemplate;
        this.scenarioRowMapper = (rs, rowNum) -> {
            ScenarioDTO scenario = new ScenarioDTO();
            scenario.setId(rs.getString("id"));
            scenario.setName(rs.getString("name"));
            scenario.setDescription(rs.getString("description"));
            scenario.setColor(rs.getString("color"));
            scenario.setActive(rs.getBoolean("is_active"));

            List<String> deviceIds = getScenarioDevices(scenario.getId());
            List<DeviceDTO> devices = deviceIds.stream()
                    .map(deviceService::getDeviceById)
                    .collect(Collectors.toList());
            scenario.setDevices(devices);

            return scenario;
        };
    }

    public List<ScenarioDTO> findByUser(String username) {
        logger.info("Поиск сценариев пользователя: {}", username);
        String sql = """
            SELECT s.* FROM scenarios s
            INNER JOIN users u ON s.user_id = u.id
            WHERE u.username = ?
            """;
            
        List<ScenarioDTO> scenarios = jdbcTemplate.query(sql, scenarioRowMapper, username);
        logger.info("Найдено сценариев у пользователя {}: {}", username, scenarios.size());
        return scenarios;
    }

    public Optional<ScenarioDTO> findById(String id) {
        List<ScenarioDTO> scenarios = jdbcTemplate.query(
                "SELECT * FROM scenarios WHERE id = ?",
                scenarioRowMapper,
                id
        );
        return scenarios.isEmpty() ? Optional.empty() : Optional.of(scenarios.get(0));
    }

    public ScenarioDTO create(ScenarioDTO scenario, Long userId) {
        logger.info("Создание сценария для пользователя {}", userId);
        jdbcTemplate.update(
                "INSERT INTO scenarios (id, name, description, color, is_active, user_id) VALUES (?, ?, ?, ?, ?, ?)",
                scenario.getId(),
                scenario.getName(),
                scenario.getDescription(),
                scenario.getColor(),
                scenario.isActive(),
                userId
        );
        logger.info("Сценарий успешно создан");

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
        logger.info("Обновление сценария {}", id);
        jdbcTemplate.update(
                "UPDATE scenarios SET name = ?, description = ?, color = ?, is_active = ? WHERE id = ?",
                scenario.getName(),
                scenario.getDescription(),
                scenario.getColor(),
                scenario.isActive(),
                id
        );
        logger.info("Сценарий успешно обновлен");

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
        logger.info("Удаление сценария {}", id);
        jdbcTemplate.update("DELETE FROM scenarios WHERE id = ?", id);
        logger.info("Сценарий успешно удален");
    }

    private List<String> getScenarioDevices(String scenarioId) {
        return jdbcTemplate.queryForList(
                "SELECT device_id FROM scenario_devices WHERE scenario_id = ?",
                String.class,
                scenarioId
        );
    }
}
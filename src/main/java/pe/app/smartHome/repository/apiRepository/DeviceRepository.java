package pe.app.smartHome.repository.apiRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.app.smartHome.dto.apiDto.DeviceDTO;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Repository
public class DeviceRepository {
    private static final Logger logger = LoggerFactory.getLogger(DeviceRepository.class);
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<DeviceDTO> deviceRowMapper;

    public DeviceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.deviceRowMapper = (rs, rowNum) -> {
            DeviceDTO device = new DeviceDTO();
            device.setId(rs.getString("id"));
            device.setName(rs.getString("name"));
            device.setStatus(rs.getBoolean("status"));
            device.setType(rs.getString("type"));

            java.sql.Timestamp timestamp = rs.getTimestamp("last_update");
            if (timestamp != null) {
                Instant instant = timestamp.toInstant();
                device.setLastUpdate(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
            }

            device.setRoomId(rs.getString("room_id"));
            logger.debug("Создан объект устройства: {}", device);
            return device;
        };
    }

    public List<DeviceDTO> findAll() {
        List<DeviceDTO> devices = jdbcTemplate.query("SELECT * FROM devices", deviceRowMapper);
        return devices;
    }

    public List<DeviceDTO> findByUser(String username) {
        logger.info("Поиск устройств пользователя: {}", username);
        String sql = """
            SELECT d.* FROM devices d
            INNER JOIN user_devices ud ON d.id = ud.device_id
            INNER JOIN users u ON ud.user_id = u.id
            WHERE u.username = ?
            """;
            
        List<DeviceDTO> devices = jdbcTemplate.query(sql, deviceRowMapper, username);
        logger.info("Найдено устройств у пользователя {}: {}", username, devices.size());
        return devices;
    }

    public Optional<DeviceDTO> findById(String id) {
        List<DeviceDTO> devices = jdbcTemplate.query(
                "SELECT * FROM devices WHERE id = ?",
                deviceRowMapper,
                id
        );
        Optional<DeviceDTO> result = devices.isEmpty() ? Optional.empty() : Optional.of(devices.get(0));
        return result;
    }

    public void updateStatus(String id, boolean status) {
        logger.info("Обновление статуса устройства {} на {}", id, status);
        int updated = jdbcTemplate.update(
                "UPDATE devices SET status = ?, last_update = CURRENT_TIMESTAMP WHERE id = ?",
                status, id
        );
        logger.info("Обновлено строк: {}", updated);
    }

    public List<DeviceDTO> findByRoomId(String roomId) {
        logger.info("Поиск устройств в комнате: {}", roomId);
        List<DeviceDTO> devices = jdbcTemplate.query(
                "SELECT * FROM devices WHERE room_id = ?",
                deviceRowMapper,
                roomId
        );
        logger.info("Найдено устройств в комнате {}: {}", roomId, devices.size());
        return devices;
    }

    public void updateDevicesRoomId(List<String> deviceIds, String roomId) {
        logger.info("Обновление room_id для устройств: {} -> {}", deviceIds, roomId);
        String sql = "UPDATE devices SET room_id = ? WHERE id = ?";

        for (String deviceId : deviceIds) {
            jdbcTemplate.update(sql, roomId, deviceId);
            logger.info("Обновлено room_id для устройства: {}", deviceId);
        }
    }

    public void clearRoomIdForDevices(String roomId) {
        logger.info("Очистка room_id для устройств комнаты: {}", roomId);
        jdbcTemplate.update(
                "UPDATE devices SET room_id = NULL WHERE room_id = ?",
                roomId
        );
        logger.info("room_id очищен для всех устройств комнаты");
    }

    public void addDeviceToUser(String deviceId, Long userId) {
        logger.info("Привязка устройства {} к пользователю {}", deviceId, userId);
        jdbcTemplate.update(
                "INSERT INTO user_devices (user_id, device_id) VALUES (?, ?)",
                userId, deviceId
        );
        logger.info("Устройство успешно привязано к пользователю");
    }

    public void save(DeviceDTO device) {
        logger.info("Сохранение устройства: {}", device.getId());
        jdbcTemplate.update(
                "INSERT INTO devices (id, name, status, type, last_update, relay_id, mac) VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, ?, ?)",
                device.getId(),
                device.getName(),
                device.isStatus(),
                device.getType(),
                device.getRelayId(),
                device.getMac()
        );
        logger.info("Устройство успешно сохранено");
    }

    public void delete(String id) {
        logger.info("Удаление устройства: {}", id);
        // Сначала удаляем связи с пользователями
        jdbcTemplate.update("DELETE FROM user_devices WHERE device_id = ?", id);
        // Затем удаляем само устройство
        jdbcTemplate.update("DELETE FROM devices WHERE id = ?", id);
        logger.info("Устройство успешно удалено");
    }

    public List<String> getAllUsernames() {
        logger.info("Получение списка всех пользователей");
        return jdbcTemplate.queryForList("SELECT username FROM users", String.class);
    }

    public void grantAccessToUsers(String deviceId, List<String> usernames) {
        logger.info("Выдача доступа к устройству {} пользователям: {}", deviceId, usernames);
        
        // Получаем ID пользователей по их username
        String sql = "SELECT id FROM users WHERE username = ?";
        
        for (String username : usernames) {
            List<Long> userIds = jdbcTemplate.queryForList(sql, Long.class, username);
            if (!userIds.isEmpty()) {
                Long userId = userIds.get(0);
                try {
                    // Проверяем, существует ли уже такая связь
                    List<String> existingDevices = jdbcTemplate.queryForList(
                            "SELECT device_id FROM user_devices WHERE user_id = ? AND device_id = ?",
                            String.class,
                            userId,
                            deviceId
                    );
                    
                    if (existingDevices.isEmpty()) {
                        jdbcTemplate.update(
                                "INSERT INTO user_devices (user_id, device_id) VALUES (?, ?)",
                                userId, deviceId
                        );
                        logger.info("Доступ выдан пользователю: {}", username);
                    } else {
                        logger.info("Пользователь {} уже имеет доступ к устройству {}", username, deviceId);
                    }
                } catch (Exception e) {
                    logger.warn("Не удалось выдать доступ пользователю {}: {}", username, e.getMessage());
                }
            } else {
                logger.warn("Пользователь не найден: {}", username);
            }
        }
    }

    public Map<String, List<String>> getUserDeviceAccess() {
        logger.info("Получение списка доступа пользователей к устройствам");
        String sql = """
            SELECT u.username, d.id as device_id
            FROM users u
            INNER JOIN user_devices ud ON u.id = ud.user_id
            INNER JOIN devices d ON ud.device_id = d.id
            ORDER BY u.username
            """;
            
        Map<String, List<String>> result = new HashMap<>();
        
        jdbcTemplate.query(sql, rs -> {
            String username = rs.getString("username");
            String deviceId = rs.getString("device_id");
            
            result.computeIfAbsent(username, k -> new ArrayList<>()).add(deviceId);
        });
        
        logger.info("Найдено {} пользователей с доступом к устройствам", result.size());
        return result;
    }
}
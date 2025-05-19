package pe.app.smartHome.repository.apiRepository;

import pe.app.smartHome.dto.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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

            // Конвертируем timestamp в ZonedDateTime
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
        logger.info("Выполнение запроса: SELECT * FROM devices");
        List<DeviceDTO> devices = jdbcTemplate.query("SELECT * FROM devices", deviceRowMapper);
        logger.info("Найдено устройств в базе данных: {}", devices.size());
        return devices;
    }

    public Optional<DeviceDTO> findById(String id) {
        logger.info("Поиск устройства по ID: {}", id);
        List<DeviceDTO> devices = jdbcTemplate.query(
                "SELECT * FROM devices WHERE id = ?",
                deviceRowMapper,
                id
        );
        Optional<DeviceDTO> result = devices.isEmpty() ? Optional.empty() : Optional.of(devices.get(0));
        logger.info("Устройство {} {}", id, result.isPresent() ? "найдено" : "не найдено");
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

    public void updateDeviceStatus(String deviceId, boolean status) {
        logger.info("Обновление статуса устройства {} на {}", deviceId, status);
        jdbcTemplate.update(
                "UPDATE devices SET status = ?, last_update = CURRENT_TIMESTAMP WHERE id = ?",
                status, deviceId
        );
        logger.info("Статус устройства обновлен");
    }
}
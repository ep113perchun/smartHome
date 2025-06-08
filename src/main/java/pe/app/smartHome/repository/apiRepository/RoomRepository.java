package pe.app.smartHome.repository.apiRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.app.smartHome.dto.apiDto.DeviceDTO;
import pe.app.smartHome.dto.apiDto.RoomDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RoomRepository {
    private static final Logger logger = LoggerFactory.getLogger(RoomRepository.class);
    private final JdbcTemplate jdbcTemplate;
    private final DeviceRepository deviceRepository;
    private final RowMapper<RoomDTO> roomRowMapper;

    public RoomRepository(JdbcTemplate jdbcTemplate, DeviceRepository deviceRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.deviceRepository = deviceRepository;
        this.roomRowMapper = (rs, rowNum) -> {
            RoomDTO room = new RoomDTO();
            room.setId(rs.getString("id"));
            room.setName(rs.getString("name"));
            room.setColor(rs.getString("color"));
            return room;
        };
    }

    public List<RoomDTO> findByUser(String username) {
        logger.info("Поиск комнат пользователя: {}", username);
        String sql = """
            SELECT r.* FROM rooms r
            INNER JOIN users u ON r.user_id = u.id
            WHERE u.username = ?
            """;
            
        List<RoomDTO> rooms = jdbcTemplate.query(sql, roomRowMapper, username);
        logger.info("Найдено комнат у пользователя {}: {}", username, rooms.size());
        return rooms;
    }

    public Optional<RoomDTO> findById(String id) {
        logger.info("Поиск комнаты по ID: {}", id);
        List<RoomDTO> rooms = jdbcTemplate.query(
                "SELECT * FROM rooms WHERE id = ?",
                roomRowMapper,
                id
        );

        if (!rooms.isEmpty()) {
            RoomDTO room = rooms.get(0);
            room.setDevices(deviceRepository.findByRoomId(room.getId()));
            logger.info("Комната найдена: {}", room);
            return Optional.of(room);
        }
        logger.info("Комната не найдена");
        return Optional.empty();
    }

    public RoomDTO create(RoomDTO room, Long userId) {
        logger.info("Создание комнаты для пользователя {}", userId);
        String id = UUID.randomUUID().toString();
        jdbcTemplate.update(
                "INSERT INTO rooms (id, name, color, user_id) VALUES (?, ?, ?, ?)",
                id,
                room.getName(),
                room.getColor(),
                userId
        );
        room.setId(id);
        logger.info("Комната создана с ID: {}", id);
        return room;
    }

    public RoomDTO update(String id, RoomDTO room) {
        logger.info("Обновление комнаты {}: {}", id, room);

        // Обновляем основные данные комнаты
        jdbcTemplate.update(
                "UPDATE rooms SET name = ?, color = ? WHERE id = ?",
                room.getName(), room.getColor(), id
        );

        // Обновляем список устройств
        if (room.getDevices() != null) {
            // Сначала очищаем текущие связи
            jdbcTemplate.update(
                    "UPDATE devices SET room_id = NULL WHERE room_id = ?",
                    id
            );

            // Затем устанавливаем новые связи
            if (!room.getDevices().isEmpty()) {
                String sql = "UPDATE devices SET room_id = ? WHERE id = ?";
                for (DeviceDTO device : room.getDevices()) {
                    jdbcTemplate.update(sql, id, device.getId());
                }
            }
        }

        room.setId(id);
        logger.info("Комната обновлена");
        return room;
    }

    public void delete(String id) {
        logger.info("Удаление комнаты: {}", id);
        jdbcTemplate.update("DELETE FROM rooms WHERE id = ?", id);
        logger.info("Комната удалена");
    }
}
package pe.app.smartHome.service.apiService;

import org.springframework.transaction.annotation.Transactional;
import pe.app.smartHome.dto.apiDto.CreateRoomRequestDTO;
import pe.app.smartHome.dto.apiDto.RoomDTO;
import pe.app.smartHome.dto.apiDto.UpdateRoomRequestDTO;
import pe.app.smartHome.repository.apiRepository.RoomRepository;
import pe.app.smartHome.repository.apiRepository.DeviceRepository;
import pe.app.smartHome.repository.securityRepository.UserRepository;
import pe.app.smartHome.entity.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public RoomService(RoomRepository roomRepository, DeviceRepository deviceRepository, UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    public List<RoomDTO> getRoomsByUsername(String username) {
        List<RoomDTO> rooms = roomRepository.findByUser(username);
        // Получаем устройства для каждой комнаты
        for (RoomDTO room : rooms) {
            room.setDevices(deviceRepository.findByRoomId(room.getId()));
        }
        return rooms;
    }

    public RoomDTO getRoomById(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Комната не найдена"));
    }

    @Transactional
    public RoomDTO createRoom(CreateRoomRequestDTO request, String username) {
        // Получаем пользователя
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        RoomDTO room = new RoomDTO();
        room.setId(UUID.randomUUID().toString());
        room.setName(request.getName());
        room.setColor(request.getColor());

        // Создаем комнату
        RoomDTO createdRoom = roomRepository.create(room, user.getId());

        // Обновляем room_id для выбранных устройств
        if (request.getDeviceIds() != null && !request.getDeviceIds().isEmpty()) {
            deviceRepository.updateDevicesRoomId(request.getDeviceIds(), createdRoom.getId());
            // Получаем обновленные устройства для комнаты
            createdRoom.setDevices(deviceRepository.findByRoomId(createdRoom.getId()));
        } else {
            createdRoom.setDevices(new ArrayList<>());
        }

        return createdRoom;
    }

    @Transactional
    public RoomDTO updateRoom(String id, UpdateRoomRequestDTO request) {
        RoomDTO room = getRoomById(id);
        room.setName(request.getName());
        room.setColor(request.getColor());

        // Обновляем комнату
        roomRepository.update(id, room);

        // Обновляем устройства комнаты
        if (request.getDeviceIds() != null) {
            // Сначала очищаем room_id для всех устройств комнаты
            deviceRepository.clearRoomIdForDevices(id);

            // Затем устанавливаем новый room_id для выбранных устройств
            if (!request.getDeviceIds().isEmpty()) {
                deviceRepository.updateDevicesRoomId(request.getDeviceIds(), id);
            }

            // Получаем обновленный список устройств
            room.setDevices(deviceRepository.findByRoomId(id));
        }

        return room;
    }

    @Transactional
    public void deleteRoom(String id) {
        // Сначала обнуляем room_id для всех устройств комнаты
        deviceRepository.clearRoomIdForDevices(id);
        // Затем удаляем саму комнату
        roomRepository.delete(id);
    }
}
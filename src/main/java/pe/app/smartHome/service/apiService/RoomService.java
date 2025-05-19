package pe.app.smartHome.service.apiService;

import org.springframework.transaction.annotation.Transactional;
import pe.app.smartHome.dto.*;
import pe.app.smartHome.repository.apiRepository.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final DeviceRepository deviceRepository;

    public RoomService(RoomRepository roomRepository, DeviceRepository deviceRepository) {
        this.roomRepository = roomRepository;
        this.deviceRepository = deviceRepository;
    }

    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll();
    }

    public RoomDTO getRoomById(String id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Комната не найдена"));
    }

    @Transactional
    public RoomDTO createRoom(CreateRoomRequestDTO request) {
        RoomDTO room = new RoomDTO();
        room.setId(UUID.randomUUID().toString());
        room.setName(request.getName());
        room.setColor(request.getColor());

        // Создаем комнату
        RoomDTO createdRoom = roomRepository.create(room);

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
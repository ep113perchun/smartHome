package pe.app.smartHome.service.apiService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.app.smartHome.dto.apiDto.DeviceDTO;
import pe.app.smartHome.dto.apiDto.DeviceStatusRequestDTO;
import pe.app.smartHome.dto.apiDto.DeviceStatusResponseDTO;
import pe.app.smartHome.dto.apiDto.CreateDeviceRequestDTO;
import pe.app.smartHome.repository.apiRepository.DeviceRepository;
import pe.app.smartHome.repository.securityRepository.UserRepository;
import pe.app.smartHome.entity.User;

import java.util.List;
import java.util.UUID;
import java.util.Map;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;
    private final UserRepository userRepository;

    public DeviceService(DeviceRepository deviceRepository, UserRepository userRepository) {
        this.deviceRepository = deviceRepository;
        this.userRepository = userRepository;
    }

    public List<DeviceDTO> getAllDevices() {
        return deviceRepository.findAll();
    }

    public List<DeviceDTO> getDevicesByUsername(String username) {
        return deviceRepository.findByUser(username);
    }

    public DeviceDTO getDeviceById(String id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Устройство не найдено"));
    }

    @Transactional
    public DeviceDTO createDevice(CreateDeviceRequestDTO request, String username) {
        // Получаем пользователя
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        // Создаем устройство
        DeviceDTO device = new DeviceDTO();
        device.setId(UUID.randomUUID().toString());
        device.setName(request.getName());
        device.setType(request.getType());
        device.setStatus(false); // По умолчанию выключено
        device.setRelayId(request.getRelayId());
        device.setMac(request.getMac());

        // Сохраняем устройство
        deviceRepository.save(device);

        // Привязываем устройство к пользователю
        deviceRepository.addDeviceToUser(device.getId(), user.getId());

        return device;
    }

    @Transactional
    public void deleteDevice(String id) {
        deviceRepository.delete(id);
    }

    public List<String> getAllUsernames() {
        return deviceRepository.getAllUsernames();
    }

    @Transactional
    public void grantAccessToUsers(String deviceId, List<String> usernames) {
        // Проверяем существование устройства
        if (!deviceRepository.findById(deviceId).isPresent()) {
            throw new RuntimeException("Устройство не найдено");
        }
        deviceRepository.grantAccessToUsers(deviceId, usernames);
    }

    public DeviceStatusResponseDTO updateDeviceStatus(String deviceId, DeviceStatusRequestDTO request) {
        deviceRepository.updateStatus(deviceId, request.isStatus());
        DeviceDTO device = getDeviceById(deviceId);
        DeviceStatusResponseDTO response = new DeviceStatusResponseDTO();
        response.setId(device.getId());
        response.setStatus(device.isStatus());
        response.setLastUpdate(device.getLastUpdate());
        return response;
    }

    public Map<String, List<String>> getUserDeviceAccess() {
        return deviceRepository.getUserDeviceAccess();
    }
}
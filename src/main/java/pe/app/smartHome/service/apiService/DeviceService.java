package pe.app.smartHome.service.apiService;

import pe.app.smartHome.dto.DeviceDTO;
import pe.app.smartHome.dto.DeviceStatusRequestDTO;
import pe.app.smartHome.dto.DeviceStatusResponseDTO;
import pe.app.smartHome.repository.apiRepository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<DeviceDTO> getAllDevices() {
        return deviceRepository.findAll();
    }

    public DeviceDTO getDeviceById(String id) {
        return deviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Устройство не найдено"));
    }

    public DeviceStatusResponseDTO updateDeviceStatus(String id, DeviceStatusRequestDTO request) {
        deviceRepository.updateStatus(id, request.isStatus());

        DeviceDTO device = getDeviceById(id);
        DeviceStatusResponseDTO response = new DeviceStatusResponseDTO();
        response.setId(device.getId());
        response.setStatus(device.isStatus());
        response.setLastUpdate(device.getLastUpdate());

        return response;
    }
}
package pe.app.smartHome.service.esp;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pe.app.smartHome.dto.apiDto.DeviceDTO;
import pe.app.smartHome.dto.esp.EspNowCommand;
import pe.app.smartHome.dto.esp.EspNowCommandWrapper;
import pe.app.smartHome.repository.apiRepository.DeviceRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EspService {

    private final DeviceRepository deviceRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendControlCommand(String deviceId, boolean status) {
//        List<DeviceDTO> allDevices = deviceRepository.findAll();
//
//        Map<String, EspNowCommand> grouped = new HashMap<>();
//
//        for (DeviceDTO device : allDevices) {
//            String mac = device.getMac();
//            grouped.putIfAbsent(mac, new EspNowCommand(mac, new ArrayList<>(), new ArrayList<>()));
//            EspNowCommand cmd = grouped.get(mac);
//            cmd.getRelayIds().add(device.getRelayId());
//            cmd.getStates().add(device.isStatus());
//        }
//
//        EspNowCommandWrapper wrapper = new EspNowCommandWrapper();
//        wrapper.setCommands(new ArrayList<>(grouped.values()));
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<EspNowCommandWrapper> request = new HttpEntity<>(wrapper, headers);
//
//        try {
//            restTemplate.postForEntity("http://192.168.0.16/control", request, String.class);
//        } catch (Exception e) {
//            System.err.println("Ошибка отправки команды ESP32: " + e.getMessage());
//        }
    }
}
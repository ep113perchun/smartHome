package pe.app.smartHome.service.esp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pe.app.smartHome.repository.apiRepository.DeviceRepository;
import pe.app.smartHome.dto.esp.EspNowCommand;
import pe.app.smartHome.dto.esp.EspNowCommandWrapper;
import pe.app.smartHome.dto.apiDto.DeviceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EspService {
    private static final Logger logger = LoggerFactory.getLogger(EspService.class);
    private static final String ESP_CONTROL_URL = "http://192.168.0.15/control";

    private final DeviceRepository deviceRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(SerializationFeature.INDENT_OUTPUT, true);

    public void sendControlCommand(String deviceId, boolean status) {
        logger.info("Отправка команды управления для устройства {}: статус {}", deviceId, status);
        
        // Получаем устройство из БД
        DeviceDTO device = deviceRepository.findById(deviceId)
                .orElseThrow(() -> new RuntimeException("Устройство не найдено: " + deviceId));

        // Проверяем наличие необходимых данных
        if (device.getMac() == null) {
            throw new RuntimeException("MAC-адрес устройства не указан: " + deviceId);
        }
        if (device.getRelayId() == null) {
            throw new RuntimeException("ID реле устройства не указан: " + deviceId);
        }

        // Получаем все устройства с таким же MAC-адресом
        List<DeviceDTO> devicesWithSameMac = deviceRepository.findByMac(device.getMac());
        
        // Сортируем устройства по relayId для правильного порядка
        devicesWithSameMac.sort((d1, d2) -> {
            if (d1.getRelayId() == null) return 1;
            if (d2.getRelayId() == null) return -1;
            return d1.getRelayId().compareTo(d2.getRelayId());
        });

        // Собираем relayIds и states
        List<Integer> relayIds = new ArrayList<>();
        List<Boolean> states = new ArrayList<>();
        
        for (DeviceDTO d : devicesWithSameMac) {
            if (d.getRelayId() != null) {
                relayIds.add(d.getRelayId());
                // Если это наше устройство, используем новый статус, иначе текущий
                states.add(d.getId().equals(deviceId) ? status : d.isStatus());
            }
        }

        // Создаем команду для устройства
        EspNowCommand command = new EspNowCommand(
            device.getMac(),
            relayIds,
            states
        );

        // Создаем обертку с командой
        EspNowCommandWrapper wrapper = new EspNowCommandWrapper();
        wrapper.setCommands(List.of(command));

        try {
            // Логируем отправляемый JSON для отладки
            String json = objectMapper.writeValueAsString(wrapper);
            logger.info("Отправляемый JSON: {}", json);
            
            // Создаем заголовки
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Создаем HTTP-сущность с JSON и заголовками
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            
            // Отправляем команду на ESP
            restTemplate.postForEntity(ESP_CONTROL_URL, entity, String.class);
            logger.info("Команда успешно отправлена на ESP для устройства {}", deviceId);
        } catch (Exception e) {
            logger.error("Ошибка при отправке команды на ESP для устройства {}: {}", deviceId, e.getMessage());
            throw new RuntimeException("Не удалось отправить команду на ESP", e);
        }
    }
}
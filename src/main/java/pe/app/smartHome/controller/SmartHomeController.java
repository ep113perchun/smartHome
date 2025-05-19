package pe.app.smartHome.controller;

import pe.app.smartHome.dto.*;
import pe.app.smartHome.service.apiService.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // В продакшене заменить на конкретный домен
public class SmartHomeController {
    private final DeviceService deviceService;
    private final RoomService roomService;
    private final ScenarioService scenarioService;
    private static final Logger logger = LoggerFactory.getLogger(SmartHomeController.class);

    public SmartHomeController(
            DeviceService deviceService,
            RoomService roomService,
            ScenarioService scenarioService) {
        this.deviceService = deviceService;
        this.roomService = roomService;
        this.scenarioService = scenarioService;
    }

    /**
     * Запрос всех устройств
     *
     * @return 200
     */
    @GetMapping("/devices")
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        logger.info("Получение списка всех устройств");
        List<DeviceDTO> devices = deviceService.getAllDevices();
        logger.info("Найдено устройств: {}", devices.size());
        return ResponseEntity.ok(devices);
    }

    /**
     * Запрос устройства по id
     *
     * @param deviceId
     * @return 200
     */
    @GetMapping("/devices/{deviceId}")
    public ResponseEntity<DeviceDTO> getDeviceById(@PathVariable String deviceId) {
        logger.info("Получение устройства по ID: {}", deviceId);
        return ResponseEntity.ok(deviceService.getDeviceById(deviceId));
    }

    /**
     * Обновление статуса устройства
     *
     * @param deviceId
     * @param request
     * @return 200
     */
    @PutMapping("/devices/{deviceId}/status")
    public ResponseEntity<DeviceStatusResponseDTO> updateDeviceStatus(
            @PathVariable String deviceId,
            @RequestBody DeviceStatusRequestDTO request) {
        logger.info("Обновление статуса устройства {} на {}", deviceId, request.isStatus());
        return ResponseEntity.ok(deviceService.updateDeviceStatus(deviceId, request));
    }

    /**
     *   // КОМНАТЫ //
     */

    /**
     * Запрос всех комнат
     *
     * @return 200
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDTO>> getRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    /**
     * Запрос комнаты по id
     *
     * @param roomId
     * @return 200
     */
    @GetMapping("/rooms/{roomId}")
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable String roomId) {
        return ResponseEntity.ok(roomService.getRoomById(roomId));
    }

    /**
     * Создание новой комнаты
     *
     * @param roomData
     * @return 200
     */
    @PostMapping("/rooms")
    public ResponseEntity<RoomDTO> createRoom(@RequestBody CreateRoomRequestDTO roomData) {
        return ResponseEntity.ok(roomService.createRoom(roomData));
    }

    /**
     * Обновление данных комнаты
     *
     * @param roomId
     * @param roomData
     * @return 200
     */
    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<RoomDTO> updateRoom(
            @PathVariable String roomId,
            @RequestBody UpdateRoomRequestDTO roomData) {
        return ResponseEntity.ok(roomService.updateRoom(roomId, roomData));
    }

    /**
     * Удаление комнаты
     *
     * @param roomId
     * @return 200
     */
    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<DeleteResponseDTO> deleteRoom(@PathVariable String roomId) {
        roomService.deleteRoom(roomId);
        DeleteResponseDTO response = new DeleteResponseDTO();
        response.setSuccess(true);
        response.setMessage("Комната успешно удалена");
        return ResponseEntity.ok(response);
    }

    // Сценарии

    /**
     * @return
     */
    @GetMapping("/scenarios")
    public ResponseEntity<List<ScenarioDTO>> getScenarios() {
        return ResponseEntity.ok(scenarioService.getAllScenarios());
    }

    @GetMapping("/scenarios/{scenarioId}")
    public ResponseEntity<ScenarioDTO> getScenarioById(@PathVariable String scenarioId) {
        return ResponseEntity.ok(scenarioService.getScenarioById(scenarioId));
    }

    @PostMapping("/scenarios")
    public ResponseEntity<ScenarioDTO> createScenario(@RequestBody CreateScenarioRequestDTO scenarioData) {
        return ResponseEntity.ok(scenarioService.createScenario(scenarioData));
    }

    @PutMapping("/scenarios/{scenarioId}")
    public ResponseEntity<ScenarioDTO> updateScenario(
            @PathVariable String scenarioId,
            @RequestBody UpdateScenarioRequestDTO scenarioData) {
        return ResponseEntity.ok(scenarioService.updateScenario(scenarioId, scenarioData));
    }

    @DeleteMapping("/scenarios/{scenarioId}")
    public ResponseEntity<DeleteResponseDTO> deleteScenario(@PathVariable String scenarioId) {
        scenarioService.deleteScenario(scenarioId);
        DeleteResponseDTO response = new DeleteResponseDTO();
        response.setSuccess(true);
        response.setMessage("Сценарий успешно удален");
        return ResponseEntity.ok(response);
    }
}
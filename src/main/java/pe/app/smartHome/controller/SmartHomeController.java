package pe.app.smartHome.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import pe.app.smartHome.dto.apiDto.*;
import pe.app.smartHome.service.apiService.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import pe.app.smartHome.service.esp.EspService;
import pe.app.smartHome.entity.User;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SmartHomeController {
    private final DeviceService deviceService;
    private final RoomService roomService;
    private final ScenarioService scenarioService;
    private final EspService espService;
    private static final Logger logger = LoggerFactory.getLogger(SmartHomeController.class);

    public SmartHomeController(
            DeviceService deviceService,
            RoomService roomService,
            ScenarioService scenarioService,
            EspService espService) {
        this.deviceService = deviceService;
        this.roomService = roomService;
        this.scenarioService = scenarioService;
        this.espService = espService;
    }

    //// ---- DEVICE ---- ////

    /**
     * Запрос всех устройств
     *
     * @return 200
     */
    @GetMapping("/devices")
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Получение списка устройств для пользователя: {}", username);
        List<DeviceDTO> devices = deviceService.getDevicesByUsername(username);
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
        espService.sendControlCommand(deviceId, request.isStatus());
        // вызвать метод статуса deviceUpdate(deviceId, request.isStatus())
        // request.isStatus() - true or false
        return ResponseEntity.ok(deviceService.updateDeviceStatus(deviceId, request));
    }


    //// ---- ROOM ---- ////

    /**
     * Запрос всех комнат
     *
     * @return 200
     */
    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDTO>> getRooms() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Получение списка комнат для пользователя: {}", username);
        List<RoomDTO> rooms = roomService.getRoomsByUsername(username);
        logger.info("Найдено комнат: {}", rooms.size());
        return ResponseEntity.ok(rooms);
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Создание комнаты для пользователя: {}", username);
        return ResponseEntity.ok(roomService.createRoom(roomData, username));
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

    //// ---- SCENARIOS ---- ////

    /**
     * Запрос всех сценариев
     *
     * @return 200
     */
    @GetMapping("/scenarios")
    public ResponseEntity<List<ScenarioDTO>> getScenarios() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Получение списка сценариев для пользователя: {}", username);
        List<ScenarioDTO> scenarios = scenarioService.getScenariosByUsername(username);
        logger.info("Найдено сценариев: {}", scenarios.size());
        return ResponseEntity.ok(scenarios);
    }

    /**
     * Запрос сценария по id
     *
     * @param scenarioId
     * @return 200
     */
    @GetMapping("/scenarios/{scenarioId}")
    public ResponseEntity<ScenarioDTO> getScenarioById(@PathVariable String scenarioId) {
        return ResponseEntity.ok(scenarioService.getScenarioById(scenarioId));
    }

    /**
     * Cоздание сценария
     *
     * @param scenarioData
     * @return 200
     */
    @PostMapping("/scenarios")
    public ResponseEntity<ScenarioDTO> createScenario(@RequestBody CreateScenarioRequestDTO scenarioData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Создание сценария для пользователя: {}", username);
        return ResponseEntity.ok(scenarioService.createScenario(scenarioData, username));
    }

    /**
     * Обновление сценария
     *
     * @param scenarioId
     * @param scenarioData
     * @return 200
     */
    @PutMapping("/scenarios/{scenarioId}")
    public ResponseEntity<ScenarioDTO> updateScenario(
            @PathVariable String scenarioId,
            @RequestBody UpdateScenarioRequestDTO scenarioData) {
        return ResponseEntity.ok(scenarioService.updateScenario(scenarioId, scenarioData));
    }

    /**
     * Удаление сценария
     *
     * @param scenarioId
     * @return 200
     */
    @DeleteMapping("/scenarios/{scenarioId}")
    public ResponseEntity<DeleteResponseDTO> deleteScenario(@PathVariable String scenarioId) {
        scenarioService.deleteScenario(scenarioId);
        DeleteResponseDTO response = new DeleteResponseDTO();
        response.setSuccess(true);
        response.setMessage("Сценарий успешно удален");
        return ResponseEntity.ok(response);
    }

    //// ---- ADMIN ---- ////

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/devices")
    public ResponseEntity<DeviceDTO> createDevice(@RequestBody CreateDeviceRequestDTO deviceData) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.info("Создание устройства для пользователя: {}", username);
        return ResponseEntity.ok(deviceService.createDevice(deviceData, username));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/devices/{deviceId}")
    public ResponseEntity<DeleteResponseDTO> deleteDevice(@PathVariable String deviceId) {
        logger.info("Удаление устройства: {}", deviceId);
        deviceService.deleteDevice(deviceId);
        
        DeleteResponseDTO response = new DeleteResponseDTO();
        response.setSuccess(true);
        response.setMessage("Устройство успешно удалено");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/users")
    public ResponseEntity<List<String>> getAllUsernames() {
        logger.info("Получение списка всех пользователей");
        return ResponseEntity.ok(deviceService.getAllUsernames());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/devices/access")
    public ResponseEntity<DeleteResponseDTO> grantDeviceAccess(@RequestBody GrantDeviceAccessRequestDTO request) {
        logger.info("Выдача доступа к устройству {} пользователям: {}", request.getDeviceId(), request.getUsernames());
        deviceService.grantAccessToUsers(request.getDeviceId(), request.getUsernames());
        
        DeleteResponseDTO response = new DeleteResponseDTO();
        response.setSuccess(true);
        response.setMessage("Доступ к устройству успешно выдан");
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/devices/access")
    public ResponseEntity<Map<String, List<String>>> getUserDeviceAccess() {
        logger.info("Получение списка доступа пользователей к устройствам");
        return ResponseEntity.ok(deviceService.getUserDeviceAccess());
    }
}
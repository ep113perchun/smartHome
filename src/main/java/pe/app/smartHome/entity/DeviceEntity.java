package pe.app.smartHome.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Table(name = "devices")
@Data
public class DeviceEntity {

    @Id
    private String id;

    private String name;

    private boolean status;

    private String type;

    @Column(name = "last_update")
    private OffsetDateTime lastUpdate;

    @Column(name = "room_id")
    private String roomId;

    @Column(name = "relay_id")
    private Integer relayId;

    private String mac;
}
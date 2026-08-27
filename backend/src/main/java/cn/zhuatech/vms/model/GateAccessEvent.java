/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vms_gate_access_event", indexes = {
    @Index(name = "idx_vms_access_appointment_time", columnList = "appointmentNo,occurredAt"),
    @Index(name = "idx_vms_access_gate_time", columnList = "gateCode,occurredAt")
})
public class GateAccessEvent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 40)
    private String eventNo;
    @Column(nullable = false, length = 40)
    private String appointmentNo;
    @Column(nullable = false, length = 40)
    private String gateCode;
    @Column(nullable = false, length = 8)
    private String direction;
    @Column(nullable = false, length = 12)
    private String result;
    @Column(nullable = false, length = 160)
    private String reason;
    @Column(nullable = false, length = 40)
    private String operatorName;
    @Column(nullable = false)
    private LocalDateTime occurredAt;

    protected GateAccessEvent() {}
    public GateAccessEvent(String eventNo, String appointmentNo, String gateCode, String direction,
                           String result, String reason, String operatorName) {
        this.eventNo = eventNo; this.appointmentNo = appointmentNo; this.gateCode = gateCode;
        this.direction = direction; this.result = result; this.reason = reason;
        this.operatorName = operatorName; this.occurredAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public String getEventNo() { return eventNo; }
    public String getAppointmentNo() { return appointmentNo; }
    public String getGateCode() { return gateCode; }
    public String getDirection() { return direction; }
    public String getResult() { return result; }
    public String getReason() { return reason; }
    public String getOperatorName() { return operatorName; }
    public LocalDateTime getOccurredAt() { return occurredAt; }
}

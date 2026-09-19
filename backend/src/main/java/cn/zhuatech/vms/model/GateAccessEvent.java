/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
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

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected GateAccessEvent() {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public GateAccessEvent(String eventNo, String appointmentNo, String gateCode, String direction,
                           String result, String reason, String operatorName) {
        this.eventNo = eventNo; this.appointmentNo = appointmentNo; this.gateCode = gateCode;
        this.direction = direction; this.result = result; this.reason = reason;
        this.operatorName = operatorName; this.occurredAt = LocalDateTime.now();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Long getId() { return id; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getEventNo() { return eventNo; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getAppointmentNo() { return appointmentNo; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getGateCode() { return gateCode; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getDirection() { return direction; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getResult() { return result; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getReason() { return reason; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getOperatorName() { return operatorName; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getOccurredAt() { return occurredAt; }
}

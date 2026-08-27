/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vms_visitor_badge", indexes = {
    @Index(name = "idx_vms_badge_status", columnList = "status"),
    @Index(name = "idx_vms_badge_appointment", columnList = "appointmentNo")
})
public class VisitorBadge {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version private Long version;
    @Column(nullable = false, unique = true, length = 32)
    private String badgeNo;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(length = 40)
    private String appointmentNo;
    @Column(length = 40)
    private String holderName;
    private LocalDateTime issuedAt;
    private LocalDateTime returnedAt;
    @Column(length = 200)
    private String remark;

    protected VisitorBadge() {}
    public VisitorBadge(String badgeNo) { this.badgeNo = badgeNo; this.status = "可用"; }
    public void issue(String appointmentNo, String holderName) {
        this.status = "已发放"; this.appointmentNo = appointmentNo; this.holderName = holderName;
        this.issuedAt = LocalDateTime.now(); this.returnedAt = null; this.remark = null;
    }
    public void returnBadge(String remark) {
        this.status = "可用"; this.returnedAt = LocalDateTime.now(); this.remark = remark;
        this.appointmentNo = null; this.holderName = null;
    }
    public void reportLost(String remark) { this.status = "挂失"; this.remark = remark; }
    public Long getId() { return id; }
    public Long getVersion() { return version; }
    public String getBadgeNo() { return badgeNo; }
    public String getStatus() { return status; }
    public String getAppointmentNo() { return appointmentNo; }
    public String getHolderName() { return holderName; }
    public LocalDateTime getIssuedAt() { return issuedAt; }
    public LocalDateTime getReturnedAt() { return returnedAt; }
    public String getRemark() { return remark; }
}

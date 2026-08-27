/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vms_enterprise_site")
public class EnterpriseSite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Version private Long version;
    @Column(nullable = false, unique = true, length = 32)
    private String siteCode;
    @Column(nullable = false, length = 80)
    private String siteName;
    @Column(nullable = false, length = 160)
    private String address;
    @Column(nullable = false, length = 40)
    private String timezone;
    @Column(nullable = false)
    private int slotCapacity;
    @Column(nullable = false, length = 80)
    private String assemblyPoint;
    @Column(nullable = false, length = 20)
    private String status;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected EnterpriseSite() {}
    public EnterpriseSite(String siteCode, String siteName, String address, String timezone,
                          int slotCapacity, String assemblyPoint, String status) {
        this.siteCode = siteCode; this.siteName = siteName; this.address = address;
        this.timezone = timezone; this.slotCapacity = slotCapacity;
        this.assemblyPoint = assemblyPoint; this.status = status; this.updatedAt = LocalDateTime.now();
    }
    public void update(String siteName, String address, String timezone, int slotCapacity,
                       String assemblyPoint, String status) {
        this.siteName = siteName; this.address = address; this.timezone = timezone;
        this.slotCapacity = slotCapacity; this.assemblyPoint = assemblyPoint;
        this.status = status; this.updatedAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public Long getVersion() { return version; }
    public String getSiteCode() { return siteCode; }
    public String getSiteName() { return siteName; }
    public String getAddress() { return address; }
    public String getTimezone() { return timezone; }
    public int getSlotCapacity() { return slotCapacity; }
    public String getAssemblyPoint() { return assemblyPoint; }
    public String getStatus() { return status; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

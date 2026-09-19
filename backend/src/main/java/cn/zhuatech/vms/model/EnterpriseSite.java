/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
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

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected EnterpriseSite() {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public EnterpriseSite(String siteCode, String siteName, String address, String timezone,
                          int slotCapacity, String assemblyPoint, String status) {
        this.siteCode = siteCode; this.siteName = siteName; this.address = address;
        this.timezone = timezone; this.slotCapacity = slotCapacity;
        this.assemblyPoint = assemblyPoint; this.status = status; this.updatedAt = LocalDateTime.now();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void update(String siteName, String address, String timezone, int slotCapacity,
                       String assemblyPoint, String status) {
        this.siteName = siteName; this.address = address; this.timezone = timezone;
        this.slotCapacity = slotCapacity; this.assemblyPoint = assemblyPoint;
        this.status = status; this.updatedAt = LocalDateTime.now();
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Long getId() { return id; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Long getVersion() { return version; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getSiteCode() { return siteCode; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getSiteName() { return siteName; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getAddress() { return address; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getTimezone() { return timezone; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public int getSlotCapacity() { return slotCapacity; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getAssemblyPoint() { return assemblyPoint; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getStatus() { return status; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

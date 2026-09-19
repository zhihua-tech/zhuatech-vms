/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@Entity
@Table(name = "vms_visitor_profile")
public class VisitorProfile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 40)
    private String visitorName;
    @Column(nullable = false, length = 80)
    private String company;
    @Column(nullable = false, unique = true, length = 30)
    private String phone;
    @Column(nullable = false)
    private boolean identityVerified;
    @Column(nullable = false)
    private boolean blacklisted;
    @Column(nullable = false)
    private int visitCount;
    private LocalDateTime lastVisitAt;
    @Column(length = 200)
    private String note;

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    protected VisitorProfile() {}
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public VisitorProfile(String visitorName, String company, String phone, boolean identityVerified,
                          boolean blacklisted, int visitCount, LocalDateTime lastVisitAt, String note) {
        this.visitorName = visitorName;
        this.company = company;
        this.phone = phone;
        this.identityVerified = identityVerified;
        this.blacklisted = blacklisted;
        this.visitCount = visitCount;
        this.lastVisitAt = lastVisitAt;
        this.note = note;
    }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void setBlacklisted(boolean blacklisted, String note) { this.blacklisted = blacklisted; this.note = note; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void verifyIdentity(boolean verified, String note) { this.identityVerified = verified; this.note = note; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public void recordVisit() { this.visitCount++; this.lastVisitAt = LocalDateTime.now(); }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public Long getId() { return id; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getVisitorName() { return visitorName; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getCompany() { return company; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getPhone() { return phone; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public boolean isIdentityVerified() { return identityVerified; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public boolean isBlacklisted() { return blacklisted; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public int getVisitCount() { return visitCount; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public LocalDateTime getLastVisitAt() { return lastVisitAt; }
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public String getNote() { return note; }
}

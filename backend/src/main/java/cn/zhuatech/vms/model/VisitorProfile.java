/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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

    protected VisitorProfile() {}
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
    public void setBlacklisted(boolean blacklisted, String note) { this.blacklisted = blacklisted; this.note = note; }
    public Long getId() { return id; }
    public String getVisitorName() { return visitorName; }
    public String getCompany() { return company; }
    public String getPhone() { return phone; }
    public boolean isIdentityVerified() { return identityVerified; }
    public boolean isBlacklisted() { return blacklisted; }
    public int getVisitCount() { return visitCount; }
    public LocalDateTime getLastVisitAt() { return lastVisitAt; }
    public String getNote() { return note; }
}

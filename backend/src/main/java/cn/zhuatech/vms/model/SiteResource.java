/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;

@Entity
@Table(name = "vms_site_resource")
public class SiteResource {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 20)
    private String type;
    @Column(nullable = false, unique = true, length = 40)
    private String code;
    @Column(nullable = false, length = 80)
    private String name;
    @Column(nullable = false, length = 80)
    private String department;
    @Column(nullable = false, length = 20)
    private String status;

    protected SiteResource() {}
    public SiteResource(String type, String code, String name, String department, String status) {
        this.type = type; this.code = code; this.name = name; this.department = department; this.status = status;
    }
    public void update(String type, String name, String department, String status) {
        this.type = type; this.name = name; this.department = department; this.status = status;
    }
    public Long getId() { return id; }
    public String getType() { return type; }
    public String getCode() { return code; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getStatus() { return status; }
}

/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.vms.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "work_item")
public class WorkItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 40)
    private String recordNo;
    @Column(nullable = false, length = 120)
    private String title;
    @Column(nullable = false, length = 24)
    private String status;
    @Column(nullable = false, length = 40)
    private String owner;
    @Column(nullable = false, length = 16)
    private String priority;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    protected WorkItem() {}
    public WorkItem(String recordNo, String title, String status, String owner, String priority) {
        this.recordNo = recordNo; this.title = title; this.status = status; this.owner = owner;
        this.priority = priority; this.updatedAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public String getRecordNo() { return recordNo; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public String getOwner() { return owner; }
    public String getPriority() { return priority; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

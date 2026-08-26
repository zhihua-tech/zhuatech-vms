/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vms_system_setting")
public class SystemSetting {
    @Id
    @Column(name = "setting_key", length = 60)
    private String key;
    @Column(name = "setting_value", nullable = false, length = 300)
    private String value;

    protected SystemSetting() {}
    public SystemSetting(String key, String value) { this.key = key; this.value = value; }
    public void changeValue(String value) { this.value = value; }
    public String getKey() { return key; }
    public String getValue() { return value; }
}

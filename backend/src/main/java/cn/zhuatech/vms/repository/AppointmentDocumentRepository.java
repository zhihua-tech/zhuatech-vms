/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;
import cn.zhuatech.vms.model.AppointmentDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public interface AppointmentDocumentRepository extends JpaRepository<AppointmentDocument, Long> {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    List<AppointmentDocument> findByAppointmentNoOrderBySubmittedAtAsc(String appointmentNo);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    Optional<AppointmentDocument> findByAppointmentNoAndDocumentType(String appointmentNo, String documentType);
}

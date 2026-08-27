/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;
import cn.zhuatech.vms.model.AppointmentDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface AppointmentDocumentRepository extends JpaRepository<AppointmentDocument, Long> {
    List<AppointmentDocument> findByAppointmentNoOrderBySubmittedAtAsc(String appointmentNo);
    Optional<AppointmentDocument> findByAppointmentNoAndDocumentType(String appointmentNo, String documentType);
}

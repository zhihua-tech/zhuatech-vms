/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;
import cn.zhuatech.vms.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByOrderByUpdatedAtDesc();
    Optional<Appointment> findByAppointmentNo(String appointmentNo);
    Optional<Appointment> findByPassCode(String passCode);
    long countByStatus(String status);
}

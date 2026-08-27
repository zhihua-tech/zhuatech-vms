/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;
import cn.zhuatech.vms.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findAllByOrderByUpdatedAtDesc();
    Optional<Appointment> findByAppointmentNo(String appointmentNo);
    Optional<Appointment> findByPassCode(String passCode);
    Optional<Appointment> findByClientRequestId(String clientRequestId);
    List<Appointment> findByStatus(String status);
    List<Appointment> findBySiteCodeAndStatusOrderByCheckedInAtAsc(String siteCode, String status);
    List<Appointment> findBySiteCodeAndVisitDateBetweenOrderByVisitDateAsc(String siteCode, LocalDate from, LocalDate to);
    long countByStatus(String status);
    @Query("select coalesce(sum(a.visitorCount), 0) from Appointment a where a.siteCode = :siteCode and a.visitDate = :visitDate " +
        "and a.timeSlot = :timeSlot and a.status in :statuses")
    long activeVisitorCount(@Param("siteCode") String siteCode, @Param("visitDate") LocalDate visitDate, @Param("timeSlot") String timeSlot,
                            @Param("statuses") List<String> statuses);
}

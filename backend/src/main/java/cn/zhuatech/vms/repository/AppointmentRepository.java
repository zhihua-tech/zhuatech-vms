/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.repository;
import cn.zhuatech.vms.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    List<Appointment> findAllByOrderByUpdatedAtDesc();
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    Optional<Appointment> findByAppointmentNo(String appointmentNo);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    Optional<Appointment> findByPassCode(String passCode);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    Optional<Appointment> findByClientRequestId(String clientRequestId);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    List<Appointment> findByStatus(String status);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    List<Appointment> findBySiteCodeAndStatusOrderByCheckedInAtAsc(String siteCode, String status);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    List<Appointment> findBySiteCodeAndVisitDateBetweenOrderByVisitDateAsc(String siteCode, LocalDate from, LocalDate to);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    long countByStatus(String status);
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @Query("select coalesce(sum(a.visitorCount), 0) from Appointment a where a.siteCode = :siteCode and a.visitDate = :visitDate " +
        "and a.timeSlot = :timeSlot and a.status in :statuses")
    long activeVisitorCount(@Param("siteCode") String siteCode, @Param("visitDate") LocalDate visitDate, @Param("timeSlot") String timeSlot,
                            @Param("statuses") List<String> statuses);
}

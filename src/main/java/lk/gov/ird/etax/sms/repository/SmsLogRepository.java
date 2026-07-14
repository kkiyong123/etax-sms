package lk.gov.ird.etax.sms.repository;

import lk.gov.ird.etax.sms.entity.SmsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SmsLogRepository extends JpaRepository<SmsLog, Long> {
    List<SmsLog> findByTin(String tin);
    List<SmsLog> findByStatus(String status);
    List<SmsLog> findByTinOrderBySentAtDesc(String tin);
}

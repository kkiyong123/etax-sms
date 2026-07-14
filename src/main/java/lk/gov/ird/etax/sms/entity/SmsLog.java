package lk.gov.ird.etax.sms.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "sms_logs")
@Data
public class SmsLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tin;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "message_content", length = 500)
    private String messageContent;

    @Column(name = "status")
    private String status;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "error_message")
    private String errorMessage;

    @PrePersist
    protected void onCreate() {
        sentAt = LocalDateTime.now();
        if (status == null) status = "PENDING";
    }
}

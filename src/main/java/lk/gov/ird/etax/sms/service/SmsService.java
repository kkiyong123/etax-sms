package lk.gov.ird.etax.sms.service;

import lk.gov.ird.etax.sms.dto.SmsDTO;
import lk.gov.ird.etax.sms.entity.SmsLog;
import lk.gov.ird.etax.sms.repository.SmsLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SmsService {

    @Autowired
    private SmsLogRepository smsLogRepository;

    @Value("${sms.sender.id}")
    private String senderId;

    public SmsLog sendSms(SmsDTO dto) {
        SmsLog log = new SmsLog();
        log.setTin(dto.getTin());
        log.setMobileNumber(dto.getMobileNumber());
        log.setMessageType(dto.getMessageType());
        log.setMessageContent(dto.getMessageContent());
        log.setStatus("SENT");
        log.setDeliveredAt(LocalDateTime.now());
        return smsLogRepository.save(log);
    }

    public SmsLog sendPaymentReminder(String tin, String mobile, String taxType, String amount, String dueDate) {
        SmsDTO dto = new SmsDTO();
        dto.setTin(tin);
        dto.setMobileNumber(mobile);
        dto.setMessageType("PAYMENT_REMINDER");
        dto.setMessageContent(
            "[" + senderId + "] Dear Taxpayer (TIN: " + tin + "), " +
            "Your " + taxType + " of LKR " + amount +
            " is due on " + dueDate + ". Please pay on time to avoid penalties."
        );
        return sendSms(dto);
    }

    public SmsLog sendPaymentConfirmation(String tin, String mobile, String taxType, String amount, String refNo) {
        SmsDTO dto = new SmsDTO();
        dto.setTin(tin);
        dto.setMobileNumber(mobile);
        dto.setMessageType("PAYMENT_CONFIRMATION");
        dto.setMessageContent(
            "[" + senderId + "] Payment Confirmed! " +
            "TIN: " + tin + ", Tax: " + taxType +
            ", Amount: LKR " + amount + ", Ref: " + refNo + ". Thank you."
        );
        return sendSms(dto);
    }

    public SmsLog sendFilingAlert(String tin, String mobile, String taxType, String deadline) {
        SmsDTO dto = new SmsDTO();
        dto.setTin(tin);
        dto.setMobileNumber(mobile);
        dto.setMessageType("FILING_ALERT");
        dto.setMessageContent(
            "[" + senderId + "] ALERT: Your " + taxType +
            " filing is overdue. Deadline was " + deadline +
            ". Please file immediately to avoid penalties."
        );
        return sendSms(dto);
    }

    public List<SmsLog> getSmsLogsByTin(String tin) {
        return smsLogRepository.findByTinOrderBySentAtDesc(tin);
    }
}

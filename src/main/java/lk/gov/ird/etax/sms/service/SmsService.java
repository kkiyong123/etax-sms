package lk.gov.ird.etax.sms.service;
import lk.gov.ird.etax.sms.dto.SmsDTO;
import lk.gov.ird.etax.sms.entity.SmsLog;
import lk.gov.ird.etax.sms.repository.SmsLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;
@Service
public class SmsService {
    private static final Logger logger = Logger.getLogger(SmsService.class.getName());
    @Autowired
    private SmsLogRepository smsLogRepository;
    @Value("${sms.sender.id:IRD-ETAX}")
    private String senderId;
    @Value("${TWILIO_ACCOUNT_SID:}")
    private String twilioAccountSid;
    @Value("${TWILIO_AUTH_TOKEN:}")
    private String twilioAuthToken;
    @Value("${TWILIO_FROM_NUMBER:}")
    private String twilioFromNumber;
    public SmsLog sendSms(SmsDTO dto) {
        SmsLog log = new SmsLog();
        log.setTin(dto.getTin());
        log.setMobileNumber(dto.getMobileNumber());
        log.setMessageType(dto.getMessageType());
        log.setMessageContent(dto.getMessageContent());
        String status = "SENT";
        if (twilioAccountSid != null && !twilioAccountSid.isEmpty()) {
            try {
                String result = sendViaTwilio(dto.getMobileNumber(), dto.getMessageContent());
                logger.info("Twilio sent: " + result);
            } catch (Exception e) {
                logger.warning("Twilio error: " + e.getMessage());
                status = "FAILED";
            }
        }
        log.setStatus(status);
        log.setDeliveredAt(LocalDateTime.now());
        return smsLogRepository.save(log);
    }
    private String sendViaTwilio(String to, String body) throws Exception {
        String urlStr = "https://api.twilio.com/2010-04-01/Accounts/" + twilioAccountSid + "/Messages.json";
        String params = "To=" + URLEncoder.encode(to, StandardCharsets.UTF_8)
            + "&From=" + URLEncoder.encode(twilioFromNumber, StandardCharsets.UTF_8)
            + "&Body=" + URLEncoder.encode(body, StandardCharsets.UTF_8);
        URL url = new URL(urlStr);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        String auth = Base64.getEncoder().encodeToString((twilioAccountSid + ":" + twilioAuthToken).getBytes());
        con.setRequestProperty("Authorization", "Basic " + auth);
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.getOutputStream().write(params.getBytes(StandardCharsets.UTF_8));
        int code = con.getResponseCode();
        if (code >= 400) throw new Exception("Twilio HTTP " + code);
        return "OK:" + code;
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

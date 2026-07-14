package lk.gov.ird.etax.sms.dto;

import lombok.Data;

@Data
public class SmsDTO {
    private String tin;
    private String mobileNumber;
    private String messageType;
    private String messageContent;
}

package lk.gov.ird.etax.sms.controller;

import lk.gov.ird.etax.sms.dto.SmsDTO;
import lk.gov.ird.etax.sms.entity.SmsLog;
import lk.gov.ird.etax.sms.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sms")
@CrossOrigin(origins = "*")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "etax-sms",
            "version", "1.0.0"
        ));
    }

    @PostMapping("/send")
    public ResponseEntity<SmsLog> send(@RequestBody SmsDTO dto) {
        return ResponseEntity.ok(smsService.sendSms(dto));
    }

    @PostMapping("/send/reminder")
    public ResponseEntity<SmsLog> sendReminder(
            @RequestParam String tin,
            @RequestParam String mobile,
            @RequestParam String taxType,
            @RequestParam String amount,
            @RequestParam String dueDate) {
        return ResponseEntity.ok(smsService.sendPaymentReminder(tin, mobile, taxType, amount, dueDate));
    }

    @PostMapping("/send/confirmation")
    public ResponseEntity<SmsLog> sendConfirmation(
            @RequestParam String tin,
            @RequestParam String mobile,
            @RequestParam String taxType,
            @RequestParam String amount,
            @RequestParam String refNo) {
        return ResponseEntity.ok(smsService.sendPaymentConfirmation(tin, mobile, taxType, amount, refNo));
    }

    @PostMapping("/send/alert")
    public ResponseEntity<SmsLog> sendAlert(
            @RequestParam String tin,
            @RequestParam String mobile,
            @RequestParam String taxType,
            @RequestParam String deadline) {
        return ResponseEntity.ok(smsService.sendFilingAlert(tin, mobile, taxType, deadline));
    }

    @GetMapping("/logs/{tin}")
    public ResponseEntity<List<SmsLog>> getLogs(@PathVariable String tin) {
        return ResponseEntity.ok(smsService.getSmsLogsByTin(tin));
    }
}

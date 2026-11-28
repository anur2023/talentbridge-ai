package com.talentbridge.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Thin wrapper so other services don't depend on JavaMailSender directly.
 * Also records a send-log entry for duplicate-send protection and auditing.
 */
@Service
public class MailService {

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private final JavaMailSender mailSender;
    private final String from;
    private final NotificationLogRepository logRepository;

    public MailService(JavaMailSender mailSender, @Value("${talentbridge.mail.from}") String from,
                        NotificationLogRepository logRepository) {
        this.mailSender = mailSender;
        this.from = from;
        this.logRepository = logRepository;
    }

    public void send(String to, String templateKey, String subject, String body, String referenceId) {
        if (logRepository.existsByTemplateKeyAndToAndReferenceId(templateKey, to, referenceId)) {
            log.info("Skipping duplicate send of {} to {} for reference {}", templateKey, to, referenceId);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send mail template={} to={}", templateKey, to, e);
        } finally {
            logRepository.save(new NotificationLog(templateKey, to, referenceId));
        }
    }
}

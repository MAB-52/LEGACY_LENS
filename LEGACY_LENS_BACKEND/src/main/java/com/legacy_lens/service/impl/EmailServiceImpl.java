package com.legacy_lens.service.impl;

import com.legacy_lens.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @Autowired
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:LegacyLens}")
    private String appName;

    @Async   // fire-and-forget so the register API returns immediately
    @Override
    public void sendOtpEmail(String toEmail, String name, String otpCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject(appName + " — Verify your email address");
            helper.setText(buildOtpEmailHtml(name, otpCode), true);  // true = HTML

            mailSender.send(message);
            log.info("OTP email sent to {}", toEmail);

        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}: {}", toEmail, e.getMessage());
            // Do NOT re-throw — registration already succeeded; user can use resend.
        }
    }

    // HTML template

    private String buildOtpEmailHtml(String name, String otp) {
        // Split OTP into individual digit spans for the box-style rendering
        StringBuilder digitBoxes = new StringBuilder();
        for (char c : otp.toCharArray()) {
            digitBoxes.append(
                "<span style=\"display:inline-block;width:48px;height:56px;" +
                "line-height:56px;text-align:center;font-size:28px;font-weight:700;" +
                "border:2px solid #4F46E5;border-radius:8px;margin:0 4px;" +
                "color:#1e1b4b;background:#f5f3ff;\">")
                .append(c)
                .append("</span>");
        }

        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8"/>
              <meta name="viewport" content="width=device-width,initial-scale=1.0"/>
              <title>Verify Your Email</title>
            </head>
            <body style="margin:0;padding:0;background:#f9fafb;font-family:'Segoe UI',Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f9fafb;padding:40px 0;">
                <tr><td align="center">
                  <table width="560" cellpadding="0" cellspacing="0"
                         style="background:#ffffff;border-radius:16px;
                                box-shadow:0 4px 24px rgba(0,0,0,0.07);overflow:hidden;">

                    <!-- Header -->
                    <tr>
                      <td style="background:linear-gradient(135deg,#4F46E5 0%%,#7C3AED 100%%);
                                 padding:36px 40px;text-align:center;">
                        <h1 style="margin:0;color:#ffffff;font-size:26px;font-weight:700;
                                   letter-spacing:-0.5px;">%s</h1>
                        <p style="margin:6px 0 0;color:#c4b5fd;font-size:14px;">
                          Legacy Code Intelligence Platform
                        </p>
                      </td>
                    </tr>

                    <!-- Body -->
                    <tr>
                      <td style="padding:40px 48px;">
                        <h2 style="margin:0 0 8px;color:#111827;font-size:22px;font-weight:600;">
                          Verify your email address
                        </h2>
                        <p style="margin:0 0 24px;color:#6b7280;font-size:15px;line-height:1.6;">
                          Hi <strong style="color:#111827;">%s</strong>, thanks for signing up!<br/>
                          Use the one-time code below to complete your registration.
                        </p>

                        <!-- OTP box -->
                        <div style="background:#f5f3ff;border:1px solid #e0e7ff;
                                    border-radius:12px;padding:28px 20px;
                                    text-align:center;margin-bottom:28px;">
                          <p style="margin:0 0 12px;color:#4F46E5;font-size:12px;
                                    font-weight:600;letter-spacing:1.5px;text-transform:uppercase;">
                            Your verification code
                          </p>
                          <div>%s</div>
                          <p style="margin:16px 0 0;color:#9ca3af;font-size:13px;">
                            ⏱ This code expires in <strong>5 minutes</strong>
                          </p>
                        </div>

                        <p style="margin:0 0 8px;color:#6b7280;font-size:14px;line-height:1.6;">
                          If you didn't create a %s account, you can safely ignore this email.
                        </p>
                        <p style="margin:0;color:#9ca3af;font-size:13px;">
                          For security, never share this code with anyone.
                        </p>
                      </td>
                    </tr>

                    <!-- Footer -->
                    <tr>
                      <td style="background:#f9fafb;border-top:1px solid #e5e7eb;
                                 padding:20px 48px;text-align:center;">
                        <p style="margin:0;color:#9ca3af;font-size:12px;">
                          © 2025 %s. All rights reserved.
                        </p>
                      </td>
                    </tr>

                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(appName, name, digitBoxes.toString(), appName, appName);
    }
}

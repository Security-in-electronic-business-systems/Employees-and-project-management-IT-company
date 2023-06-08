package com.SIEBS.ITCompany.service.impl;

import com.SIEBS.ITCompany.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmailServiceImpl implements EmailService {
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public String sendMail( String to, String subject, String body) {
        try{
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);

            StringBuilder htmlBody = new StringBuilder();
            htmlBody.append("<html><body>");
            htmlBody.append("<h1>").append(subject).append("</h1>");
            htmlBody.append("<p>Postovani,</p>");
            htmlBody.append("<p>").append(body).append("</p>");
            htmlBody.append("<hr>");
            htmlBody.append("<p></p>");
            htmlBody.append("</body></html>");

            mimeMessageHelper.setText(htmlBody.toString(), true);

            javaMailSender.send(mimeMessage);
            return "Success!";
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}

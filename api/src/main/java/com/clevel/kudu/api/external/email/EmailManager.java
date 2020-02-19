package com.clevel.kudu.api.external.email;

import com.clevel.kudu.api.exception.EmailException;
import com.clevel.kudu.api.model.SystemConfig;
import com.clevel.kudu.api.system.Application;
import com.clevel.kudu.util.DateTimeUtil;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;

import javax.ejb.Asynchronous;
import javax.inject.Inject;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Map;
import java.util.Properties;

public class EmailManager {
    @Inject
    Logger log;
    @Inject
    Application app;

    private static final String EMAIL_ENCODE = "UTF-8";

    protected String emailTemplate;

    @Asynchronous
    public void sendMail(String toAddress, String subject, String ccAddress, Map<String, String> valuesMap) throws EmailException {
        log.debug("sendMail. (toAddress: {}, subject: {}, ccAddress: {})", toAddress, subject, ccAddress);
        log.debug("SMTP host : {}", app.getConfig(SystemConfig.EMAIL_SERVER));

        if (toAddress == null || "".equalsIgnoreCase(toAddress.trim())) {
            log.debug("empty email! do nothing.");
            throw new EmailException("invalid address!");
        }

        Properties p = new Properties();
        p.put("mail.smtp.host", app.getConfig(SystemConfig.EMAIL_SERVER));
        p.put("mail.smtp.port", app.getConfig(SystemConfig.EMAIL_PORT));
        p.put("mail.smtp.auth", app.getConfig(SystemConfig.EMAIL_SMTP_AUTH));
        p.put("mail.transport.protocol", "smtp");
        p.put("mail.mime.encodefilename", "true");
        p.put("mail.smtp.starttls.enable", app.getConfig(SystemConfig.EMAIL_TLS_ENABLE));

        try {
            Session session = Session.getInstance(p, new EmailAuthenticator(app.getConfig(SystemConfig.EMAIL_USERNAME), app.getConfig(SystemConfig.EMAIL_PASSWORD)));
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(app.getConfig(SystemConfig.EMAIL_USERNAME), app.getConfig(SystemConfig.EMAIL_SENDER_NAME), EMAIL_ENCODE));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress, false));
            if (ccAddress != null && !"".equalsIgnoreCase(ccAddress.trim())) {
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccAddress, false));
            }

            msg.setSubject(subject, EMAIL_ENCODE);
            msg.setContent(replaceTemplate(valuesMap), "text/html; charset=" + EMAIL_ENCODE);
            msg.setSentDate(DateTimeUtil.now());

            Transport.send(msg);
        } catch (AuthenticationFailedException e) {
            log.error("", e);
            throw new EmailException("authentication failed!", e);
        } catch (AddressException e) {
            log.error("", e);
            throw new EmailException("invalid address!", e);
        } catch (MessagingException e) {
            log.error("", e);
            throw new EmailException("send mail failed!", e);
        } catch (Exception e) {
            log.error("Email Exception!", e);
            throw new EmailException("send mail failed!", e);
        }
    }

    private String readTemplateFile(String templateFile) {
        FileInputStream fis;
        InputStreamReader isr;
        BufferedReader bf;

        String fileName = app.getRealPath() + templateFile;
        log.debug("Email template file: {}", fileName);
        try {
            fis = new FileInputStream(fileName);
            isr = new InputStreamReader(fis, EMAIL_ENCODE);
        } catch (FileNotFoundException e) {
            log.error("Email template not found (file: {})", fileName, e);
            return "";
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
            return "";
        }
        String line;
        StringBuilder body = new StringBuilder();
        bf = new BufferedReader(isr);
        try {
            while ((line = bf.readLine()) != null) {
                body.append(line);
            }
            bf.close();
            isr.close();
            fis.close();
        } catch (IOException e) {
            log.error("IO Exception!", e);
        }

        return body.toString();
    }

    protected String replaceTemplate(Map<String, String> valuesMap) {
        String messageBody = readTemplateFile(emailTemplate);
        return StringSubstitutor.replace(messageBody,valuesMap);
    }

}

package com.clevel.kudu.api.external.email;

import com.clevel.kudu.api.exception.EmailException;
import com.clevel.kudu.model.SystemConfig;
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
    private Logger log;
    @Inject
    protected Application app;

    private static final String EMAIL_ENCODE = "UTF-8";
    private static final String TEMPLATE_PATH = "/WEB-INF/email/";

    protected String templateName;

    @Asynchronous
    public void sendMail(String toAddress, String ccAddress, Map<String, String> valuesMap) throws EmailException {
        log.debug("sendMail. (toAddress: {}, ccAddress: {})", toAddress, ccAddress);
        log.debug("SMTP host : {}", app.getConfig(SystemConfig.EMAIL_SERVER));

        if (toAddress == null || "".equalsIgnoreCase(toAddress.trim())) {
            log.debug("empty email! do nothing.");
            throw new EmailException("invalid address!");
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", app.getConfig(SystemConfig.EMAIL_SERVER));
        props.put("mail.smtp.port", app.getConfig(SystemConfig.EMAIL_PORT));
        props.put("mail.smtp.auth", app.getConfig(SystemConfig.EMAIL_SMTP_AUTH));
        props.put("mail.smtp.starttls.enable", app.getConfig(SystemConfig.EMAIL_TLS_ENABLE));
        log.debug("SMTP-Props: {}", props);

        try {
            Session session = Session.getInstance(props, new EmailAuthenticator(app.getConfig(SystemConfig.EMAIL_USERNAME), app.getConfig(SystemConfig.EMAIL_PASSWORD)));
            MimeMessage mimeMessage = new MimeMessage(session);

            mimeMessage.setSentDate(DateTimeUtil.now());
            mimeMessage.setFrom(new InternetAddress(app.getConfig(SystemConfig.EMAIL_USERNAME), app.getConfig(SystemConfig.EMAIL_SENDER_NAME), EMAIL_ENCODE));

            InternetAddress[] sendTo = InternetAddress.parse(removeQuotes(toAddress), false);
            mimeMessage.setRecipients(Message.RecipientType.TO, sendTo);

            if (ccAddress != null && !ccAddress.isEmpty()) {
                InternetAddress[] sendToCC = InternetAddress.parse(removeQuotes(ccAddress), false);
                mimeMessage.setRecipients(Message.RecipientType.CC, sendToCC);
                mimeMessage.setReplyTo(sendToCC);
            }

            replaceLineBreaks(valuesMap);
            mimeMessage.setSubject(replaceTemplate("subject.txt",valuesMap), EMAIL_ENCODE);
            mimeMessage.setContent(replaceTemplate("html",valuesMap), "text/html; charset=" + EMAIL_ENCODE);

            Transport.send(mimeMessage);
            log.debug("message already sent.");

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

    private void replaceLineBreaks(Map<String, String> valuesMap) {
        String value;
        for (String key : valuesMap.keySet()) {
            value = replaceLineBreaks(valuesMap.get(key), "<br />");
            valuesMap.put(key, value);
        }
    }

    private String replaceLineBreaks(String source, String replacement) {
        return source.replaceAll("\\n", replacement);
    }

    protected String replaceTemplate(String fileExtension, Map<String, String> valuesMap) {
        String fileName = TEMPLATE_PATH + templateName +"."+ fileExtension;
        String messageBody = readTemplateFile(fileName);
        return StringSubstitutor.replace(messageBody, valuesMap);
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

    protected String removeQuotes(String to) {
        return to.replaceAll("[\"]", "");
    }

}

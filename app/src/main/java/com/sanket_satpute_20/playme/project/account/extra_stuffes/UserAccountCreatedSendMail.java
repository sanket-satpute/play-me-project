package com.sanket_satpute_20.playme.project.account.extra_stuffes;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class UserAccountCreatedSendMail extends AsyncTask<Void,Void,Void> {

    public static final String EMAIL_FROM = "playmeplayermail@gmail.com";
    public static final String PASSWORD_EMAIL_FROM = "hhlelaxfsqjxqvzj";

    Session session;

    String email, subject, EMassage;

    //Constructor
    public UserAccountCreatedSendMail(String email, String name) {
        this.email = email;
        subject = "Thank you for joining Us.";
        EMassage = getMassage(name);
    }

    private String getMassage(String name) {
        return "<html>" +
                "<body>" +
                "Dear <strong>" + name+ "</strong>," +
                "<br><br>" +
                "I would like to take this opportunity to express my sincere gratitude for joining our <strong>PlayMe App</strong>. " +
                "We are thrilled to have you as part of our community and are excited to provide you with the best possible " +
                "experience." +
                "<br><br>" +
                "Your support means a lot to us, and we value your contribution to our platform. " +
                "We promise to continue to deliver the highest quality service and features that meet your needs and exceed your expectations." +
                "<br><br>" +
                "Thank you once again for joining us, and we look forward to serving you." +
                "<br><br>" +
                "Best regards,<br>" +
                "<strong>PlayMe Team</strong>" +
                "</body>" +
                "</html>";
    }

    @Override
    protected Void doInBackground(Void... params) {
        Properties props = new Properties();

        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.true", "true");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");

        session = Session.getDefaultInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(EMAIL_FROM, PASSWORD_EMAIL_FROM);
                    }
                });
        session.setDebug(true);

        try {
            MimeMessage massage = new MimeMessage(session);
            massage.setFrom(new InternetAddress(EMAIL_FROM));
            massage.addRecipient(Message.RecipientType.TO, new InternetAddress(email));
            massage.setContent(EMassage, "text/html");
            massage.setSubject(subject);
            Transport.send(massage);

/*            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(message);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            messageBodyPart = new MimeBodyPart();
            DataSource source = new FileDataSource(filePath);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(filePath);
            multipart.addBodyPart(messageBodyPart);
            mm.setContent(multipart);*/

        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package com.sanket_satpute_20.playme.project.account.extra_stuffes;

import static com.sanket_satpute_20.playme.project.extra_stuffes.Constants.currentUser;

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

public class JavaMailAPI extends AsyncTask<Void,Void,Void> {

    public static final String action_otp_sent_completed = "action.OTP.Sent.Completed";
    public static final String otp_sent_success_or_fail_extra = "OTP_SENT_SUCCESS_OR_FAILED_EXTRA";
    public static final String otp_sent_success_or_fail_error_msg_extra = "OTP_SENT_SUCCESS_OR_FAILED_ERROR_MSG_EXTRA";

    public static final String EMAIL_FROM = "playmeplayermail@gmail.com";
    public static final String PASSWORD_EMAIL_FROM = "hhlelaxfsqjxqvzj";
    // oeqohyeqwvgkgfbk - ssatpute738@gmail.com
    // hhlelaxfsqjxqvzj - playmeplayermail@gmail.com

    //Add those line in dependencies
    //implementation files('libs/activation.jar')
    //implementation files('libs/additionnal.jar')
    //implementation files('libs/mail.jar')

    //  Variables

    Context context;
    Session session;

    String email, subject, EMassage, otp_code, error_msg;
    boolean isOtpSent = false;

    //Constructor
    public JavaMailAPI(Context context, String email, String otp_code) {
        this.context = context;
        this.email = email;
        this.otp_code = otp_code;
        subject = "Your OTP FOR PlayMe";
        EMassage = "<html><body>Hi, " + currentUser.getFirst_name() + " Your Request for changing email address is " +
                "received.YOUR OTP IS <strong style='font-size:1.3em;'>" + otp_code + "</strong>. if you not aware ignore this massage</body></html>";
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Intent intent = new Intent();
        intent.setAction(action_otp_sent_completed);
        intent.putExtra(otp_sent_success_or_fail_extra, isOtpSent);
        intent.putExtra(otp_sent_success_or_fail_error_msg_extra, error_msg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
//            massage.setText(EMassage);
            Transport.send(massage);

            isOtpSent = true;

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
            error_msg = e.getMessage();
            isOtpSent = false;
        }
        return null;
    }
}
/**
 * License: src/main/resources/license/escidoc.license
 */
package de.mpg.imeji.presentation.user.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import de.mpg.imeji.presentation.util.PropertyReader;

public class EmailClient
{
    private static Logger logger = Logger.getLogger(EmailClient.class);

    public void sendMail(String to, String from, String subject, String message) throws IOException, URISyntaxException
    {
        String emailUser = PropertyReader.getProperty("imeji.email.user");
        String password = PropertyReader.getProperty("imeji.email.password");
        String server = PropertyReader.getProperty("imeji.email.server.smtp");
        String auth = PropertyReader.getProperty("imeji.email.auth");
        String sender = PropertyReader.getProperty("imeji.email.sender");
        if (from != null)
        {
            sender = from;
        }
        String[] recipientsAdress = { to };
        sendMail(server, auth, emailUser, password, sender, recipientsAdress, null, null, null, subject, message);
    }

    public String sendMail(String smtpHost, String withAuth, String usr, String pwd, String senderAddress,
            String[] recipientsAddresses, String[] recipientsCCAddresses, String[] recipientsBCCAddresses,
            String[] replytoAddresses, String subject, String text)
    {
        logger.debug("EmailHandlingBean sendMail...");
        String status = "not sent";
        try
        {
            // Setup mail server
            Properties props = System.getProperties();
            props.put("mail.smtp.host", smtpHost);
            props.put("mail.smtp.auth", withAuth);
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.port", "25");
            // Get a mail session with authentication
            MailAuthenticator authenticator = new MailAuthenticator(usr, pwd);
            Session mailSession = Session.getInstance(props, authenticator);
            // Define a new mail message
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(senderAddress));
            // add TO recipients
            for (String ra : recipientsAddresses)
            {
                if (ra != null && !ra.trim().equals(""))
                {
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(ra));
                    logger.debug(">>> recipientTO: " + ra);
                }
            }
            // add CC recipients
            if (recipientsCCAddresses != null)
                for (String racc : recipientsCCAddresses)
                {
                    if (racc != null && !racc.trim().equals(""))
                    {
                        message.addRecipient(Message.RecipientType.CC, new InternetAddress(racc));
                        logger.debug(">>> recipientCC  " + racc);
                    }
                }
            // add BCC recipients
            if (recipientsBCCAddresses != null)
                for (String rabcc : recipientsBCCAddresses)
                {
                    if (rabcc != null && !rabcc.trim().equals(""))
                    {
                        message.addRecipient(Message.RecipientType.BCC, new InternetAddress(rabcc));
                        logger.debug(">>> recipientBCC  " + rabcc);
                    }
                }
            // add replyTo
            if (replytoAddresses != null)
            {
                InternetAddress[] adresses = new InternetAddress[recipientsAddresses.length];
                int i = 0;
                for (String a : replytoAddresses)
                {
                    if (a != null && !a.trim().equals(""))
                    {
                        adresses[i] = new InternetAddress(a);
                        i++;
                        logger.debug(">>> replyToaddress  " + a);
                    }
                }
                if (i > 0)
                    message.setReplyTo(adresses);
            }
            message.setSubject(subject);
            Date date = new Date();
            message.setSentDate(date);
            // Create a message part to represent the body text
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(text);
            // use a MimeMultipart as we need to handle the file attachments
            Multipart multipart = new MimeMultipart();
            // add the message body to the mime message
            multipart.addBodyPart(messageBodyPart);
            // Put all message parts in the message
            message.setContent(multipart);
            logger.debug("Transport will send now....  ");
            // Send the message
            Transport.send(message);
            status = "sent";
            logger.debug("Email sent!");
        }
        catch (MessagingException e)
        {
            logger.error("Error in sendMail(...)", e);
        }
        return status;
    }

    public class MailAuthenticator extends Authenticator
    {
        private final String user;
        private final String password;

        /**
         * Public constructor.
         */
        public MailAuthenticator(String usr, String pwd)
        {
            this.user = usr;
            this.password = pwd;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication()
        {
            PasswordAuthentication pwdAut = new PasswordAuthentication(this.user, this.password);
            return pwdAut;
        }
    }
}

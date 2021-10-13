import java.util.Properties;
import java.io.*;
import java.security.NoSuchProviderException;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;

public class EmailReader {

    public static void main(String[] args) throws NoSuchProviderException, IOException {
        while (true) {
            receveMail("cotede158@gmail.com", "cotede11223344");
        }

    }

    public static void receveMail(String userName, String passWord) throws NoSuchProviderException, IOException {

        try {
            Properties properties = new Properties();
            properties.setProperty("mail.store.protocol", "imaps");
            Session emailSession = Session.getDefaultInstance(properties);
            Store emailStore = emailSession.getStore("imaps");
            emailStore.connect("imap.gmail.com", userName, passWord);

            Folder emailFolder = emailStore.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);
            Message messages[] = emailFolder.getMessages();
            
            for (int i = 0 ; i < messages.length; i++) {

                Message message = messages[i];

                String subject = message.getSubject().toString();

               
                String  value = getTextFromMessage(message).replaceAll("[^0-9]","");
                if (subject.equalsIgnoreCase("test")) {

                    System.out.println(value);
                } else {

                    " ".isEmpty();
                }

            }
            emailFolder.close(false);
            emailStore.close();
        } catch (MessagingException me) {
            me.printStackTrace();

        }

    }

    private static String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
            }
        }
        return result;
    }

}
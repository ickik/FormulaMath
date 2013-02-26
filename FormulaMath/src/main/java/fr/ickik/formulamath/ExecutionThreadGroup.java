package fr.ickik.formulamath;

import java.awt.Frame;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.ickik.formulamath.view.AbstractFormulaMathFrame;

/**
 * This class extends the thread group class to redefine the behavior when an
 * exception is thrown.
 * @author Ickik
 * @version 0.1.002, 13 June 2012
 */
public class ExecutionThreadGroup extends ThreadGroup {

	private static final Logger log = LoggerFactory.getLogger(ExecutionThreadGroup.class);
	
	/**
	 * Constructor of the thread group.
	 */
	public ExecutionThreadGroup() {
		super("ExecutionGroup");
	}

	public void uncaughtException(Thread t, Throwable e) {
		log.error("", e);
		JOptionPane.showMessageDialog(findActiveFrame(), e.toString(), AbstractFormulaMathFrame.getTitle() + " - Exception Occurred", JOptionPane.ERROR_MESSAGE);
		System.exit(1);
	}
	
//	private void sendMail(String errorTitle) {
//		try {
//			Properties prop = System.getProperties();
//			prop.put("mail.smtps.host", "smtp.gmail.com");
//			prop.put("mail.smtps.port", "465");
//			prop.put("mail.smtps.auth", "true");
//			Session session = Session.getDefaultInstance(prop,null);
//			Message message = new MimeMessage(session);
//			message.setFrom(new InternetAddress("formulamath@gmail.com"));
//			InternetAddress[] internetAddresses = new InternetAddress[1];
//			internetAddresses[0] = new InternetAddress("formulamath@gmail.com");
//			message.setRecipients(Message.RecipientType.TO,internetAddresses);
//			message.setSubject("Error on FormulaMath : " + errorTitle);
//			message.setText("test mail");
//			message.setHeader("X-Mailer", "Java");
//			
//			message.setContent(getLogAttachment());
//			message.setSentDate(new Date());
//			session.setDebug(true);
//			Transport.send(message);
//		} catch (AddressException e) {
//			e.printStackTrace();
//		} catch (MessagingException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private Multipart getLogAttachment() {
//		Multipart multiPart = new MimeMultipart();
//		BodyPart bodypart = new MimeBodyPart();
//		FileDataSource source = new FileDataSource(new File(System.getProperty("user.home") + "/.FormulaMath/log/FormulaMath.log"));
//		try {
//			bodypart.setFileName("FormulaMath.log");
//			bodypart.setDataHandler(new DataHandler(source));
//			multiPart.addBodyPart(bodypart);
//		} catch (MessagingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return multiPart;
//	}
	
	private Frame findActiveFrame() {
		Frame[] frames = Frame.getFrames();
		for (Frame frame : frames) {
			if (frame.isVisible() || frame.isActive()) {
				return frame;
			}
		}
		return null;
	}
}

package fr.ickik.formulamath.model;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @author Ickik
 * @version 0.1.000, 4th September 2012
 * @since 0.3.10
 */
public final class UserModel {

	public boolean isKeyValide() {
		String key = "";
		return isKeyValide(key);
	}
	
	public boolean isKeyValide(String key) {
		String serialNumberEncoded = getSerialNumber();
		return key.equalsIgnoreCase(serialNumberEncoded);
	}
	
	public void keyRequest(String emailAddress) {
		/*try {
			Properties prop = System.getProperties();
			prop.put("mail.smtps.host", "smtp.gmail.com");
			prop.put("mail.smtps.port", "465");
			prop.put("mail.smtps.auth", "true");
			Session session = Session.getDefaultInstance(prop,null);
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailAddress));
			InternetAddress[] internetAddresses = new InternetAddress[1];
			internetAddresses[0] = new InternetAddress("formulamath@gmail.com");
			message.setRecipients(Message.RecipientType.TO,internetAddresses);
			message.setSubject("Key request");
			message.setText(getReversedMacAddress() + "-" + FormulaMathProperties.);
			message.setHeader("X-Mailer", "Java");
			message.setSentDate(new Date());
			session.setDebug(true);
			Transport.send(message);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}*/
	}

	private String getReversedMacAddress() {
		try {
			byte[] array = NetworkInterface.getByIndex(0).getHardwareAddress();
			StringBuilder p = new StringBuilder();
			for (byte b : array){
				p.append(String.format("%02X",b));
			}
			return p.reverse().toString();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String getSerialNumber() {
		try {
			String mac = getReversedMacAddress();
			//225
			String encodedSerialNumber = calculateSecurityHash(mac,"SHA-512") + calculateSecurityHash(mac,"MD5") + calculateSecurityHash(mac,"SHA-256");

			System.out.println(encodedSerialNumber);
			
			String hashedSerialNumber = "" + encodedSerialNumber.charAt(66)
					+ encodedSerialNumber.charAt(51)
					+ encodedSerialNumber.charAt(79)
					+ encodedSerialNumber.charAt(186)
					+ encodedSerialNumber.charAt(202)
					+ encodedSerialNumber.charAt(3)
					+ encodedSerialNumber.charAt(191)
					+ encodedSerialNumber.charAt(83)
					+ encodedSerialNumber.charAt(36)
					+ encodedSerialNumber.charAt(218)
					+ encodedSerialNumber.charAt(47)
					+ encodedSerialNumber.charAt(123)
					+ encodedSerialNumber.charAt(177)
					+ encodedSerialNumber.charAt(12)
					+ encodedSerialNumber.charAt(130)
					+ encodedSerialNumber.charAt(94)
					+ encodedSerialNumber.charAt(53)
					+ encodedSerialNumber.charAt(102)
					+ encodedSerialNumber.charAt(15)
					+ encodedSerialNumber.charAt(169);
			String reEncodedSerialNumber = calculateSecurityHash(hashedSerialNumber,"SHA-512");
			String reHashedSerialNumber = reEncodedSerialNumber.charAt(54)
					+ reEncodedSerialNumber.charAt(78)
					+ reEncodedSerialNumber.charAt(98)
					+ reEncodedSerialNumber.charAt(101)
					+ reEncodedSerialNumber.charAt(4)
					+ "-"
					+ reEncodedSerialNumber.charAt(22)
					+ reEncodedSerialNumber.charAt(13)
					+ reEncodedSerialNumber.charAt(83)
					+ reEncodedSerialNumber.charAt(124)
					+ reEncodedSerialNumber.charAt(61)
					+ "-"
					+ reEncodedSerialNumber.charAt(52)
					+ reEncodedSerialNumber.charAt(47)
					+ reEncodedSerialNumber.charAt(117)
					+ reEncodedSerialNumber.charAt(121)
					+ reEncodedSerialNumber.charAt(32)
					+ "-"
					+ reEncodedSerialNumber.charAt(26)
					+ reEncodedSerialNumber.charAt(38)
					+ reEncodedSerialNumber.charAt(86)
					+ reEncodedSerialNumber.charAt(116)
					+ reEncodedSerialNumber.charAt(59);
			return reHashedSerialNumber.toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private String calculateSecurityHash(String stringInput, String algorithmName) throws NoSuchAlgorithmException {
		String hexMessageEncode = "";
		byte[] buffer = stringInput.getBytes();
		MessageDigest messageDigest = MessageDigest.getInstance(algorithmName);
		messageDigest.update(buffer);
		byte[] messageDigestBytes = messageDigest.digest();
		for (int index=0; index < messageDigestBytes.length ; index ++) {
			int countEncode = messageDigestBytes[index] & 0xff;
			if (Integer.toHexString(countEncode).length() == 1) hexMessageEncode = hexMessageEncode + "0";
			hexMessageEncode = hexMessageEncode + Integer.toHexString(countEncode);
		}
		return hexMessageEncode;
	}
}

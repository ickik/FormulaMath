package fr.ickik.updater;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.swing.JOptionPane;

/**
 * Main class which contains the main static method.
 * @author Ickik
 * @version 0.1.001, 13 apr. 2012
 */
public final class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ThreadGroup executionThreadGroup = new ExecutionThreadGroup();
		new Thread(executionThreadGroup, "") {
			public void run() {
				if (!checkVersion()) {
					JOptionPane.showMessageDialog(null, "Java version not compatible please update", "ERROR!", JOptionPane.ERROR_MESSAGE);
					return ;
				}
				long jour = elapsedDays();
				if (jour >= 7) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
					String savedDate = dateFormat.format(Calendar.getInstance().getTime());
					PropertiesModel.getSingleton().put(FormulaMathProperty.LAST_UPDATE, savedDate);
					try {
						PropertiesModel.getSingleton().save();
					} catch (IOException e) {
						e.printStackTrace();
					}
					UpdateModel model = new UpdateModel();
					if (model.isConnectionAvailable()) {
						UpdateFrame main = new UpdateFrame(model);
						main.setVisible(true);
						model.update();
					} else {
						openApplication();
					}
				} else {
					openApplication();
				}
			}
		}.start();
	}
	
	private static boolean checkVersion() {
		String version = System.getProperty("java.version");
		if (version.matches("1\\.[6-7].*")) {
			return true;
		}
		return false;
	}
	
	private static void openApplication() {
		try {
			Desktop.getDesktop().open(new File("FormulaMath.jar"));
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
	
	private static long elapsedDays() {
		String date = PropertiesModel.getSingleton().getProperty(FormulaMathProperty.LAST_UPDATE);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
		Calendar calendar = Calendar.getInstance();
		try {
			Date d = dateFormat.parse(date);
			calendar.setTime(d);
		} catch (ParseException e) {
			PropertiesModel.getSingleton().putDefaultProperty(FormulaMathProperty.LAST_UPDATE);
			date = PropertiesModel.getSingleton().getProperty(FormulaMathProperty.LAST_UPDATE);
			try {
				Date d = dateFormat.parse(date);
				calendar.setTime(d);
			} catch (ParseException e1) {}
		}
		return TimeUnit.DAYS.convert(Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis(), TimeUnit.MILLISECONDS);
	}

}

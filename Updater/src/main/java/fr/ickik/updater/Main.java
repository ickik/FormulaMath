package fr.ickik.updater;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import fr.ickik.formulamath.model.FormulaMathProperty;
import fr.ickik.formulamath.model.PropertiesModel;
import fr.ickik.formulamath.update.UpdateModel;
import fr.ickik.formulamath.view.ConfigurationFrame;
import fr.ickik.formulamath.view.UpdateFrame;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ThreadGroup executionThreadGroup = new ExecutionThreadGroup();
		new Thread(executionThreadGroup, "") {
			public void run() {
				long jour = elapsedDays();
				if (jour >= 7) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
					String savedDate = dateFormat.format(Calendar.getInstance().getTime());
					PropertiesModel.getSingleton().put(FormulaMathProperty.LAST_UPDATE, savedDate);
//					try {
//						PropertiesModel.getSingleton().save();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					UpdateModel model = new UpdateModel();
					if (model.isConnectionAvailable()) {
						UpdateFrame main = new UpdateFrame(model);
						main.setVisible(true);
						model.update();
					} else {
						new ConfigurationFrame();
					}
				} else {
					new ConfigurationFrame();
				}
			}
		}.start();
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

package fr.ickik.formulamath.update;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.ickik.formulamath.model.FormulaMathProperty;
import fr.ickik.formulamath.model.PropertiesModel;
import fr.ickik.formulamath.view.MainFrame;

/**
 * The model which managed the update from a web server. It initializes the connection
 * and search the last release to update. It download and placed it in the right directory.
 * After downloading, it rename the jar file into the current jar file to start the application.
 * @author Ickik
 * @version 0.1.000, 27 mar. 2012
 */
public final class UpdateModel {

	private final List<Version> versionList = new ArrayList<Version>();
	private List<UpdaterListener> listenerList = new ArrayList<UpdaterListener>();
	private static final String USER_DIRECTORY = System.getProperty("user.dir");
	private final Logger logger = LoggerFactory.getLogger(UpdateModel.class);
	
	private static final int VERSION_CHECKED_PERCENTAGE = 10;
	private static final int UPDATE_AVAILABLE_PERCENTAGE = 15;
	private static final int BEGIN_DOWNLOAD_PERCENTAGE = 20;
	private static final int DOWNLOAD_PERCENTAGE = 80;
	private static final int MAX_PERCENTAGE = 100;
	private static final String xmlConfigurationFile = "versions.xml";
	
	/**
	 * Default constructor.
	 */
	public UpdateModel() {}

	/**
	 * Search the last version and update the application.
	 */
	public void update() {
		searchAvailableVersion();
		if (isUpdateAvailable()) {
			fireUpdateListener(UPDATE_AVAILABLE_PERCENTAGE, "Search new version");
			Version v = getLastVersion();
			downloadFiles(v);
			fireUpdateListener(MAX_PERCENTAGE, "Your application is up to date");
			renameFiles(v);
			fireRestartListener();
			restartApplication();
		} else {
			fireUpdateListener(MAX_PERCENTAGE, "Your application is up to date");
			fireStartListener();
		}
	}
	
	private void restartApplication() {
		//File application = new File(USER_DIRECTORY + "/d.jar");
		//try {
		//	Desktop.getDesktop().open(application);
			try {
				new ProcessBuilder("java -jar target/FormulaMath.jar").start();
			       /*InputStream is = process.getInputStream();
			       InputStreamReader isr = new InputStreamReader(is);
			       BufferedReader br = new BufferedReader(isr);
			       String line;

			        while ((line = br.readLine()) != null) {
			         System.out.println(line);
			       }*/
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
			System.exit(0);
		//} catch (IOException e) {
		//	logger.error(e.getMessage());
		//}
	}
	
	private void renameFiles(Version v) {
		for (String[] s : v.getFileList()) {
			renameFile(s[1]);
		}
	}
	
	private void renameFile(String fileName) {
		File newVersion = new File(USER_DIRECTORY + "/" + fileName);
		File currentVersion = new File(USER_DIRECTORY + "/FormulaMath.jar");
		File oldVersion = new File(USER_DIRECTORY + "/old.jar");
		if (newVersion.exists()) {
			if (currentVersion.renameTo(oldVersion)) {
				if (newVersion.renameTo(currentVersion)) {
					oldVersion.delete();
				}
			} else {
				logger.debug("Problem to rename file");
			}
		}
	}
	
	private void downloadFiles(Version v) {
		int len = getTotalFileLength(v);
		fireUpdateListener(BEGIN_DOWNLOAD_PERCENTAGE, "Search new version");
		for (String[] files : v.getFileList()) {
			downloadFile(files[0], files[1], len);
		}
	}
	
	private int getTotalFileLength(Version v) {
		int len = 0;
		for (String[] files : v.getFileList()) {
			len += getFileLength(files[0]);
		}
		return len;
	}
	
	private int getFileLength(String filePath) {
		try {
			URL url = new URL(filePath);
			URLConnection connection = url.openConnection();
			return connection.getContentLength();
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return 0;
	}

	private void downloadFile(String filePath, String destination, int totalFilesLength) { 
		URLConnection connection = null;
		InputStream is = null;
		FileOutputStream destinationFile = null;
		final int bufferSize = 4096;
		try { 
			URL url = new URL(filePath);
			connection = url.openConnection();
			int length = connection.getContentLength();
			if(length == -1){
				throw new IOException("Fichier vide");
			}

			is = new BufferedInputStream(connection.getInputStream());

			destinationFile = new FileOutputStream(destination + filePath.substring(filePath.lastIndexOf("/"), filePath.length()));
			
			byte[] data = new byte[bufferSize];

			int len;
			int fileLength = 0;
			while((len = is.read(data)) > 0){
				destinationFile.write(data);
				fileLength += len;
				fireUpdateListener((DOWNLOAD_PERCENTAGE * fileLength) / totalFilesLength, "Updating...");
			}

			if(fileLength != length){
				throw new IOException("Le fichier n'a pas été lu en entier (seulement " 
						+ fileLength + " sur " + length + ")");
			}		

			destinationFile.flush();

		} catch (MalformedURLException e) { 
			logger.error(e.getMessage());
		} catch (IOException e) { 
			logger.error(e.getMessage());
		} finally{
			try {
				is.close();
				destinationFile.close();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}


	private void searchAvailableVersion() {
		versionList.addAll(getAvailableVersions());
		fireUpdateListener(VERSION_CHECKED_PERCENTAGE, "Search new version");
	}

	private boolean isUpdateAvailable() {
		Version v = getLastVersion();
		return v.getVersion().compareTo(MainFrame.VERSION) > 0;
	}

	private Version getLastVersion() {
		return versionList.get(versionList.size() - 1);
	}

	/**
	 * Check if the connection to the web server is available or not.
	 * @return true if the connection is available, false otherwise.
	 */
	public boolean isConnectionAvailable() {
		try {
			URL xmlUrl = new URL(PropertiesModel.getSingleton().getProperty(FormulaMathProperty.UPDATE_SERVER));
			URLConnection urlConnection = xmlUrl.openConnection();
			urlConnection.connect();
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	private List<Version> getAvailableVersions() {
		try {
			URL xmlUrl = new URL(PropertiesModel.getSingleton().getProperty(FormulaMathProperty.UPDATE_SERVER) + "/" + xmlConfigurationFile);
			URLConnection urlConnection = xmlUrl.openConnection();
			urlConnection.setUseCaches(false);
			urlConnection.connect();

			InputStream stream = urlConnection.getInputStream();
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			VersionHandler versionHandler =  new VersionHandler();
			parser.parse(stream, versionHandler);
			List<Version> vl = versionHandler.getVersionList();
			Collections.sort(vl);
			for (Version v : vl) {
				if (v != null) {
					logger.trace(v.getVersion() + v.getFileList().get(0).toString());
				}
			}
			return vl;
		} catch (ParserConfigurationException e) {
			logger.error(e.getMessage());
		} catch (SAXException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Add a listener to this model. The listener will be called to
	 * fire the download state.
	 * @param listener
	 */
	public void addUpdateListener(UpdaterListener listener) {
		listenerList.add(listener);
	}

	private void fireUpdateListener(int value, String msg) {
		for (UpdaterListener l : listenerList) {
			l.updateValue(value, msg);
		}
	}
	
	private void fireRestartListener() {
		for (UpdaterListener l : listenerList) {
			l.restart();
		}
	}
	
	private void fireStartListener() {
		for (UpdaterListener l : listenerList) {
			l.start();
		}
	}

	/*<versions>
	<version>
		<nom>Num�ro de version</nom>
		<files>
			<file>
				<url>Chemin vers le fichier</url>
				<destination>Destination relative</destination>
			</file>
		</files>
	</version>
</versions>
	 */

	class VersionHandler extends DefaultHandler {

		private String versionName;
		private String url;
		private String destination;
		private List<String[]> fileList;
		private StringBuffer buffer;
		private final List<Version> versionList = new ArrayList<Version>();

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equals("version")) {
				versionName = "";
				fileList = new ArrayList<String[]>();
			} else {
				buffer = new StringBuffer();
				if (qName.equals("file")) {
					url = "";
					destination = "";
				}
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equals("version")) {
				Version v = new Version(versionName, fileList);
				versionList.add(v);
			} else {
				if (qName.equals("nom")) {
					versionName = buffer.toString();
				} else if (qName.equals("file")) {
					fileList.add(new String[] {url, destination});
				} else if (qName.equals("url")) {
					url = buffer.toString();
				} else if (qName.equals("destination")) {
					destination = buffer.toString();
				}
			}
		}

		@Override
		public void characters(char[] ch,int start, int length) throws SAXException{
			String lecture = new String(ch,start,length);
			if (buffer != null) {
				buffer.append(lecture);       
			}
		}

		public List<Version> getVersionList() {
			return versionList;
		}
	}

	private class Version implements Comparable<Version> {

		private final String version;
		private final List<String[]> fileList;

		Version(String version, List<String[]> fileList) {
			this.version = version;
			this.fileList = fileList;
		}

		public String getVersion() {
			return version;
		}

		public List<String[]> getFileList() {
			return fileList;
		}

		public int compareTo(Version o) {
			return version.compareTo(o.getVersion());
		}
	}

}

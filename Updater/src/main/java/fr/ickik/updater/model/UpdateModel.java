package fr.ickik.updater.model;
import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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

import fr.ickik.updater.UpdaterListener;

/**
 * The model which managed the update from a web server. It initializes the connection
 * and search the last release to update. It download and placed it in the right directory.
 * The updater overrides all existing files and then start the application.
 * @author Ickik
 * @version 0.1.007, 1 june 2012
 */
public final class UpdateModel {

	private final List<Version> versionList = new ArrayList<Version>();
	private List<UpdaterListener> listenerList = new ArrayList<UpdaterListener>();
	private final Logger logger = LoggerFactory.getLogger(UpdateModel.class);
	private String currentVersion;
	private static final int UPDATE_AVAILABLE_PERCENTAGE = 10;
	private static final int BEGIN_DOWNLOAD_PERCENTAGE = 20;
	private static final int DOWNLOAD_PERCENTAGE = 75;
	private static final int MAX_PERCENTAGE = 100;
	private static final String xmlConfigurationFile = "versions.xml";
	private static final String extension = ".jar";
	private static final String APPLICATION = "FormulaMath-";

	/**
	 * Default constructor.
	 */
	public UpdateModel() {
		currentVersion = getCurrentVersion();
		logger.debug("Current version of FormulaMath found : {}", currentVersion);
	}

	/**
	 * Search the last version and update the application.
	 */
	public void update() {
		fireUpdateListener(UPDATE_AVAILABLE_PERCENTAGE, "Search new version");
		Version v = getNextVersion();
		if (v != null) {
			downloadFiles(v);
			PropertiesModel.getSingleton().put(FormulaMathProperty.VERSION, v.getVersion());
			try {
				PropertiesModel.getSingleton().save();
			} catch (IOException e) {}
			currentVersion = v.getVersion();
		}
		fireUpdateListener(MAX_PERCENTAGE, "Your application is up to date");
		fireRestartListener();
		startApplication();
	}
	
	public void startApplication() {
//		File currentVersion = new File(APPLICATION);
//		try {
//			new ProcessBuilder("java -jar " + currentVersion.getName()).start();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		try {
			Desktop.getDesktop().open(new File(APPLICATION + currentVersion + extension));
		} catch(Exception exception) {
			exception.printStackTrace();
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
		final int bufferSize = 8192;
		try { 
			URL url = new URL(filePath);
			connection = url.openConnection();
			int length = connection.getContentLength();
			if(length == -1){
				throw new IOException("Fichier vide");
			}

			is = new BufferedInputStream(connection.getInputStream());

			destinationFile = new FileOutputStream(destination);
			
			byte[] data = new byte[bufferSize];

			int len;
			int fileLength = 0;
			while((len = is.read(data)) > 0){
				destinationFile.write(data,0, len);
				fileLength += len;
				fireUpdateListener( BEGIN_DOWNLOAD_PERCENTAGE + ((DOWNLOAD_PERCENTAGE * fileLength) / totalFilesLength), "Updating...");
			}

			if(fileLength != length){
				throw new IOException("The file (" + filePath + ") was not completly readed (only " 
						+ fileLength + " / " + length + ")");
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
		List<Version> list = getAvailableVersions();
		if (list != null && !list.isEmpty()) {
			versionList.addAll(getAvailableVersions());
		}
		//fireUpdateListener(VERSION_CHECKED_PERCENTAGE, "Search new version");
	}

	public boolean isUpdateAvailable() {
		searchAvailableVersion();
		if (versionList.isEmpty()) {
			return false;
		}
		Version v = getNextVersion();
		if (v == null) {
			return false;
		}
		//logger.debug("next version is {}", v.getVersion());
		//boolean isUpdateAvailable = v.getVersion().compareTo(currentVersion) > 0;
		//logger.debug("{} is available ? {}", isUpdateAvailable);
		return true;
	}
	
	private Version getNextVersion() {
		/*if (versionList.isEmpty()) {
			return null;
		}*/
		for(Version v : versionList) {
			logger.debug("{} compareto {} = {}", new Object[]{v.getVersion(), currentVersion, v.getVersion().compareTo(currentVersion)});
			if (v.getVersion().compareTo(currentVersion) > 0) {
				logger.debug("{} compareto {} = {}", new Object[]{v.getVersion(), currentVersion, v.getVersion().compareTo(currentVersion)});
				logger.debug("Next version found: {}" , v.getVersion());
				return v;
			}
		}
		return null;
	}
	
	private String getCurrentVersion() {
		String jarVersion = loadCurrentVersion();
		logger.debug("getCurrentVersion from classloader : {}", jarVersion);
		if (jarVersion == null || jarVersion.isEmpty()) {
			return PropertiesModel.getSingleton().getProperty(FormulaMathProperty.VERSION);
		}
		return jarVersion;
	}
	
	private String loadCurrentVersion() {
		try {
			URL[] urlArray = new URL[] {new File(APPLICATION + currentVersion + extension).toURI().toURL()};
			ClassLoader classLoader = new URLClassLoader(urlArray);
			Class<?> classe = Class.forName("fr.ickik.formulamath.view.AbstractFormulaMathFrame", false, classLoader);
			Field field = classe.getDeclaredField("VERSION");
			classe = null;
			classLoader = null;
			return (String) field.get(null);
		} catch (MalformedURLException e) {
			logger.error("loadCurrentVersion : {}", e.getMessage());
		} catch (ClassNotFoundException e) {
			logger.error("loadCurrentVersion : {}", e.getMessage());
		} catch (NoSuchFieldException e) {
			logger.error("loadCurrentVersion : {}", e.getMessage());
		} catch (SecurityException e) {
			logger.error("loadCurrentVersion : {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("loadCurrentVersion : {}", e.getMessage());
		} catch (IllegalAccessException e) {
			logger.error("loadCurrentVersion : {}", e.getMessage());
		}
		return null;
	}

	/*private Version getLastVersion() {
		logger.debug("return the last version : {}", versionList.get(versionList.size() - 1).getVersion());
		return versionList.get(versionList.size() - 1);
	}*/

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

	private class VersionHandler extends DefaultHandler {

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

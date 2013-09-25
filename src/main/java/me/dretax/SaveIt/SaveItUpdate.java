package me.dretax.SaveIt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.io.*;
import java.lang.Runnable;
import java.lang.Thread;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class SaveItUpdate {
	private Plugin plugin;
	private UpdateType type;
	private String versionTitle;
	private String versionLink;
	private long totalSize; // Holds the total size of the file
	//private double downloadedSize;
	private int sizeLine; // Used for detecting file size
	private int multiplier; // Used for determining when to broadcast download updates
	private boolean announce; // Whether to announce file downloads
	private URL url; // Connecting to RSS
	private File file; // The plugin's file
	private Thread thread; // Updater thread
	private static final String DBOUrl = "http://dev.bukkit.org/server-mods/"; // Slugs will be appended to this to get to the project's RSS feed
	private String[] noUpdateTag = {"-DEV", "-PRE", "-SNAPSHOT"}; // If the version number contains one of these, don't update.
	private static final int BYTE_SIZE = 1024; // Used for downloading files
	private String updateFolder = YamlConfiguration.loadConfiguration(new File("bukkit.yml")).getString("settings.update-folder"); // The folder that downloads will be placed in
	private SaveItUpdate.UpdateResult result = SaveItUpdate.UpdateResult.SUCCESS; // Used for determining the outcome of the update process
	// Strings for reading RSS
	private static final String TITLE = "title";
	private static final String LINK = "link";
	private static final String ITEM = "item";

	public enum UpdateResult {
		SUCCESS,
		NO_UPDATE,
		FAIL_DOWNLOAD,
		FAIL_DBO,
		FAIL_NOVERSION,
		FAIL_BADSLUG,
		UPDATE_AVAILABLE
	}

	public enum UpdateType {
		DEFAULT,
		NO_VERSION_CHECK,
		NO_DOWNLOAD
	}

	/**
	 * Initialize the updater
	 *
	 * @param plugin   The plugin that is checking for an update.
	 * @param slug     The dev.bukkit.org slug of the project (http://dev.bukkit.org/server-mods/SLUG_IS_HERE)
	 * @param file     The file that the plugin is running from, get this by doing this.getFile() from within your main class.
	 * @param type     Specify the type of update this will be. See {@link UpdateType}
	 * @param announce True if the program should announce the progress of new updates in console
	 */
	public SaveItUpdate(Plugin plugin, String slug, File file, UpdateType type, boolean announce) {
		this.plugin = plugin;
		this.type = type;
		this.announce = announce;
		this.file = file;
		try {
			// Obtain the results of the project's file feed
			url = new URL(DBOUrl + slug + "/files.rss");
		} catch (MalformedURLException ex) {
			// Invalid slug
			plugin.getLogger().warning("The author of this plugin (" + plugin.getDescription().getAuthors().get(0) + ") has misconfigured their Auto Update system");
			plugin.getLogger().warning("The project slug given ('" + slug + "') is invalid. Please nag the author about this.");
			result = SaveItUpdate.UpdateResult.FAIL_BADSLUG; // Bad slug! Bad!
		}
		thread = new Thread(new UpdateRunnable());
		thread.start();
	}

	public SaveItUpdate.UpdateResult getResult() {
		waitForThread();
		return result;
	}

	public long getFileSize() {
		waitForThread();
		return totalSize;
	}

	public String getLatestVersionString() {
		waitForThread();
		return versionTitle;
	}

	public void waitForThread() {
		if (thread.isAlive()) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveFile(File folder, String file, String u) {
		if (!folder.exists()) {
			folder.mkdir();
		}
		BufferedInputStream in = null;
		FileOutputStream fout = null;
		try {
			// Download the file
			URL url = new URL(u);
			int fileLength = url.openConnection().getContentLength();
			in = new BufferedInputStream(url.openStream());
			fout = new FileOutputStream(folder.getAbsolutePath() + "/" + file);

			byte[] data = new byte[BYTE_SIZE];
			int count;
			if (announce) plugin.getLogger().info("About to download a new update: " + versionTitle);
			long downloaded = 0;
			while ((count = in.read(data, 0, BYTE_SIZE)) != -1) {
				downloaded += count;
				fout.write(data, 0, count);
				int percent = (int) (downloaded * 100 / fileLength);
				if (announce & (percent % 10 == 0)) {
					plugin.getLogger().info("Downloading update: " + percent + "% of " + fileLength + " bytes.");
				}
			}
			//Just a quick check to make sure we didn't leave any files from last time...
			for (File xFile : new File("plugins/" + updateFolder).listFiles()) {
				if (xFile.getName().endsWith(".zip")) {
					xFile.delete();
				}
			}
			// Check to see if it's a zip file, if it is, unzip it.
			File dFile = new File(folder.getAbsolutePath() + "/" + file);
			if (dFile.getName().endsWith(".zip")) {
				// Unzip
				unzip(dFile.getCanonicalPath());
			}
			if (announce) plugin.getLogger().info("Finished updating.");
		} catch (Exception ex) {
			plugin.getLogger().warning("The auto-updater tried to download a new update, but was unsuccessful.");
			result = SaveItUpdate.UpdateResult.FAIL_DOWNLOAD;
		} finally {
			try {
				if (in != null) {
					in.close();
				}
				if (fout != null) {
					fout.close();
				}
			} catch (Exception ex) {
			}
		}
	}

	private void unzip(String file) {
		try {
			File fSourceZip = new File(file);
			String zipPath = file.substring(0, file.length() - 4);
			ZipFile zipFile = new ZipFile(fSourceZip);
			Enumeration<? extends ZipEntry> e = zipFile.entries();
			while (e.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) e.nextElement();
				File destinationFilePath = new File(zipPath, entry.getName());
				destinationFilePath.getParentFile().mkdirs();
				if (entry.isDirectory()) {
					continue;
				} else {
					BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
					int b;
					byte buffer[] = new byte[BYTE_SIZE];
					FileOutputStream fos = new FileOutputStream(destinationFilePath);
					BufferedOutputStream bos = new BufferedOutputStream(fos, BYTE_SIZE);
					while ((b = bis.read(buffer, 0, BYTE_SIZE)) != -1) {
						bos.write(buffer, 0, b);
					}
					bos.flush();
					bos.close();
					bis.close();
					String name = destinationFilePath.getName();
					if (name.endsWith(".jar") && pluginFile(name)) {
						destinationFilePath.renameTo(new File("plugins/" + updateFolder + "/" + name));
					}
				}
				entry = null;
				destinationFilePath = null;
			}
			e = null;
			zipFile.close();
			zipFile = null;
			// Move any plugin data folders that were included to the right place, Bukkit won't do this for us.
			for (File dFile : new File(zipPath).listFiles()) {
				if (dFile.isDirectory()) {
					if (pluginFile(dFile.getName())) {
						File oFile = new File("plugins/" + dFile.getName()); // Get current dir
						File[] contents = oFile.listFiles(); // List of existing files in the current dir
						for (File cFile : dFile.listFiles()) // Loop through all the files in the new dir
						{
							boolean found = false;
							for (File xFile : contents) // Loop through contents to see if it exists
							{
								if (xFile.getName().equals(cFile.getName())) {
									found = true;
									break;
								}
							}
							if (!found) {
								// Move the new file into the current dir
								cFile.renameTo(new File(oFile.getCanonicalFile() + "/" + cFile.getName()));
							} else {
								// This file already exists, so we don't need it anymore.
								cFile.delete();
							}
						}
					}
				}
				dFile.delete();
			}
			new File(zipPath).delete();
			fSourceZip.delete();
		} catch (IOException ex) {
			ex.printStackTrace();
			plugin.getLogger().warning("The auto-updater tried to unzip a new update file, but was unsuccessful.");
			result = SaveItUpdate.UpdateResult.FAIL_DOWNLOAD;
		}
		new File(file).delete();
	}

	public boolean pluginFile(String name) {
		for (File file : new File("plugins").listFiles()) {
			if (file.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	private String getFile(String link) {
		String download = null;
		try {
			// Open a connection to the page
			URL url = new URL(link);
			URLConnection urlConn = url.openConnection();
			InputStreamReader inStream = new InputStreamReader(urlConn.getInputStream());
			BufferedReader buff = new BufferedReader(inStream);

			int counter = 0;
			String line;
			while ((line = buff.readLine()) != null) {
				counter++;
				// Search for the download link
				if (line.contains("<li class=\"user-action user-action-download\">")) {
					// Get the raw link
					download = line.split("<a href=\"")[1].split("\">Download</a>")[0];
				}
				// Search for size
				else if (line.contains("<dt>Size</dt>")) {
					sizeLine = counter + 1;
				} else if (counter == sizeLine) {
					String size = line.replaceAll("<dd>", "").replaceAll("</dd>", "");
					multiplier = size.contains("MiB") ? 1048576 : 1024;
					size = size.replace(" KiB", "").replace(" MiB", "");
					totalSize = (long) (Double.parseDouble(size) * multiplier);
				}
			}
			urlConn = null;
			inStream = null;
			buff.close();
			buff = null;
		} catch (Exception ex) {
			ex.printStackTrace();
			plugin.getLogger().warning("The auto-updater tried to contact dev.bukkit.org, but was unsuccessful.");
			result = SaveItUpdate.UpdateResult.FAIL_DBO;
			return null;
		}
		return download;
	}

	private boolean versionCheck(String title) {
		if (type != UpdateType.NO_VERSION_CHECK) {
			String version = plugin.getDescription().getVersion();
			if (title.split(" v").length == 2) {
				String remoteVersion = title.split(" v")[1].split(" ")[0]; // Get the newest file's version number
				int remVer = -1, curVer = 0;
				try {
					remVer = calVer(remoteVersion);
					curVer = calVer(version);
				} catch (NumberFormatException nfe) {
					remVer = -1;
				}
				if (hasTag(version) || version.equalsIgnoreCase(remoteVersion) || curVer >= remVer) {
					// We already have the latest version, or this build is tagged for no-update
					result = SaveItUpdate.UpdateResult.NO_UPDATE;
					return false;
				}
			} else {
				// The file's name did not contain the string 'vVersion'
				plugin.getLogger().warning("The author of this plugin (" + plugin.getDescription().getAuthors().get(0) + ") has misconfigured their Auto Update system");
				plugin.getLogger().warning("Files uploaded to BukkitDev should contain the version number, seperated from the name by a 'v', such as PluginName v1.0");
				plugin.getLogger().warning("Please notify the author of this error.");
				System.out.println(title);
				result = SaveItUpdate.UpdateResult.FAIL_NOVERSION;
				return false;
			}
		}
		return true;
	}

	private Integer calVer(String s) throws NumberFormatException {
		if (s.contains(".")) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < s.length(); i++) {
				Character c = s.charAt(i);
				if (Character.isLetterOrDigit(c)) {
					sb.append(c);
				}
			}
			return Integer.parseInt(sb.toString());
		}
		return Integer.parseInt(s);
	}

	private boolean hasTag(String version) {
		for (String string : noUpdateTag) {
			if (version.contains(string)) {
				return true;
			}
		}
		return false;
	}

	private boolean readFeed() {
		try {
			// Set header values intial to the empty string
			String title = "";
			String link = "";
			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			InputStream in = read();
			if (in != null) {
				XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
				// Read the XML document
				while (eventReader.hasNext()) {
					XMLEvent event = eventReader.nextEvent();
					if (event.isStartElement()) {
						if (event.asStartElement().getName().getLocalPart().equals(TITLE)) {
							event = eventReader.nextEvent();
							title = event.asCharacters().getData();
							continue;
						}
						if (event.asStartElement().getName().getLocalPart().equals(LINK)) {
							event = eventReader.nextEvent();
							link = event.asCharacters().getData();
							continue;
						}
					} else if (event.isEndElement()) {
						if (event.asEndElement().getName().getLocalPart().equals(ITEM)) {
							// Store the title and link of the first entry we get - the first file on the list is all we need
							versionTitle = title;
							versionLink = link;
							// All done, we don't need to know about older files.
							break;
						}
					}
				}
				return true;
			} else {
				return false;
			}
		} catch (XMLStreamException e) {
			plugin.getLogger().warning("Could not reach dev.bukkit.org for update checking. Is it offline?");
			return false;
		}
	}

	private InputStream read() {
		try {
			return url.openStream();
		} catch (IOException e) {
			plugin.getLogger().warning("Could not reach BukkitDev file stream for update checking. Is dev.bukkit.org offline?");
			return null;
		}
	}

	private class UpdateRunnable implements Runnable {

		public void run() {
			if (url != null) {
				// Obtain the results of the project's file feed
				if (readFeed()) {
					if (versionCheck(versionTitle)) {
						String fileLink = getFile(versionLink);
						if (fileLink != null && type != UpdateType.NO_DOWNLOAD) {
							String name = file.getName();
							// If it's a zip file, it shouldn't be downloaded as the plugin's name
							if (fileLink.endsWith(".zip")) {
								String[] split = fileLink.split("/");
								name = split[split.length - 1];
							}
							saveFile(new File("plugins/" + updateFolder), name, fileLink);
						} else {
							result = UpdateResult.UPDATE_AVAILABLE;
						}
					}
				}
			}
		}
	}
}
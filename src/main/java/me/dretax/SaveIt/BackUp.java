package me.dretax.SaveIt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: DreTaX
 * Date: 2013.07.22.
 * Time: 21:28
 * Backup System
 */
public class BackUp {
	private String rootdir = Bukkit.getServer().getWorldContainer().getAbsolutePath();
	private Main p = Main.getInstance();
	private SaveItConfig SaveItConfig = p.getSaveItConfig();
	private final int BUFFER_SIZE = 4096;

	protected void check() {
		// Check if SaveItBackups folder doesn't exist, and create it.
		File ff = new File(rootdir, "SaveItBackups");
		if (!ff.exists()) {
			ff.mkdirs();
		}
	}

	public void kcheck() {
		SaveItConfig.load();
		if (SaveItConfig.EnableBackup) {
			long timeStamp = System.currentTimeMillis() / 1000L;
			long date = timeStamp + SaveItConfig.DateIntv * 24 * 60 * 60;
			if (SaveItConfig.AutoBackup) {
				// Check if we are using the Given Daily Backup
				if ((SaveItConfig.Decide).equalsIgnoreCase("DAY")) {
					// If we didn't put the time to the Date yet.
					if (SaveItConfig.Date == 0) {
						SaveItConfig.getPluginConfig().set(String.valueOf("BackUp.Date"), date);
						try {
							SaveItConfig.getPluginConfig().save(SaveItConfig.configFile);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// If we already put the Date there
					else {
						// If the Date is in the YML older than the current Date
						if (SaveItConfig.Date <= timeStamp) {
							// Set the newest Date
							SaveItConfig.getPluginConfig().set(String.valueOf("BackUp.Date"), date);
							try {
								SaveItConfig.getPluginConfig().save(SaveItConfig.configFile);
							} catch (Exception e) {
								e.printStackTrace();
							}
							// Backup
							backupdir();
						}
					}
				}
			}
		}
	}

	public void backupdir() {
		SaveItConfig.load();
		if (SaveItConfig.Debug) {
			p.sendConsoleMessage(ChatColor.GREEN + "Starting Backup.....");
		}
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "saveit save");
		if (SaveItConfig.EnableBackupMSG) {
			String n = SaveItConfig.getPluginConfig().getString("BackUp.WarningMSG");
			Bukkit.getServer().broadcastMessage(colorize(n));
		}
		if (!SaveItConfig.DisableDefaultWorldSave) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off");
		}
		if (SaveItConfig.KickBackup) {
			for (Player p : Bukkit.getServer().getOnlinePlayers()) {
				p.kickPlayer("");
			}
		}
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
		String zipFile = rootdir + "/SaveItBackups/SaveItBackup" + timeStamp + ".zip";
		File f = new File(rootdir);
		File[] flist = f.listFiles();
		assert flist != null;
		try {
			backup(flist, zipFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (SaveItConfig.EnableBackupMSG) {
			String n = SaveItConfig.getPluginConfig().getString("BackUp.WarningMSG2");
			Bukkit.getServer().broadcastMessage(colorize(n));
		}
		if (!SaveItConfig.DisableDefaultWorldSave) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on");
		}
		if (SaveItConfig.Debug) {
			p.sendConsoleMessage(ChatColor.GREEN + "Done!");
		}
	}

	private void backup(File[] listFiles, String destZipFile) throws IOException {
		ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));
		for (File file : listFiles) {
			if (!SaveItConfig.Directory.contains(file.getName()) && !file.getName().equalsIgnoreCase("SaveItBackup") && !file.getName().equalsIgnoreCase("SaveItBackups")) {
				if (file.isDirectory()) {
					addFolderToZip(file, file.getName(), zos);
				} else {
					addFileToZip(file, zos);
				}
				if (SaveItConfig.Debug) p.sendConsoleMessage("Adding file: " + file.getName());
			}
		}

		zos.flush();
		zos.close();
	}

	private void addFolderToZip(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
		File[] listFilesOfFolder = folder.listFiles();
		assert listFilesOfFolder != null;
		for (File file : listFilesOfFolder) {
			if (file.isDirectory()) {
				addFolderToZip(file, parentFolder + "/" + file.getName(), zos);
				continue;
			}

			zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));

			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

			long bytesRead = 0;
			byte[] bytesIn = new byte[BUFFER_SIZE];
			int read = 0;

			while ((read = bis.read(bytesIn)) != -1) {
				zos.write(bytesIn, 0, read);
				bytesRead += read;
			}

			zos.closeEntry();

		}
	}

	private void addFileToZip(File file, ZipOutputStream zos) throws IOException {
		String name = file.getName();
		if (!name.endsWith(".lck")) {
			zos.putNextEntry(new ZipEntry(file.getName()));
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

			long bytesRead = 0;
			byte[] bytesIn = new byte[BUFFER_SIZE];
			int read = 0;

			while ((read = bis.read(bytesIn)) != -1) {
				zos.write(bytesIn, 0, read);
				bytesRead += read;
			}
		}

		zos.closeEntry();
	}

	public void delZip() {
		File folder = new File(rootdir + "/SaveItBackups/");
		// List files
		File[] fileList = folder.listFiles();
		// Assert it
		assert fileList != null;
		// Make the purgetime. Basically defines that how old should the file be, to get deleted
		long purgeTime = System.currentTimeMillis() - (SaveItConfig.daysBack * 24 * 60 * 60 * 1000);
		long lastModifiedTime = 0;
		// Yeah has to be null, because of the size...
		// Find the files
		File lastModifiedFile = null;
		for (File listFile : fileList) {
			// If maxbackup is enabled
			if (SaveItConfig.MaxBackups) {
				long last = listFile.lastModified();
				// If the size is bigger than X
				if (fileList.length > SaveItConfig.maxbackups) {
					// Get the lastmodified file's size , without getting null pointer exceptions
					double bytes = lastModifiedFile != null ? lastModifiedFile.length() : 0;
					// Check if It's not 0
					if (bytes > 0) {
						// Delete it
						lastModifiedFile.delete();
					}
				}
				// Catch the lastmodified file
				if (last > lastModifiedTime) {
					lastModifiedTime = last;
					lastModifiedFile = listFile;
				}
				// If It's older, delete it.
				if (listFile.lastModified() < purgeTime) {
					listFile.delete();
				}
			}
		}
	}


	private String colorize(String s) {
		// This little code supports coloring.
		// If String is null it will return null
		if (s == null) return null;
		return ChatColor.translateAlternateColorCodes('&', s);
	}
}

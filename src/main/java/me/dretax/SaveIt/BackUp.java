package me.dretax.SaveIt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
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
	private String rootdir = Bukkit.getServer().getWorldContainer().getPath();
	Main p;
	private SaveItConfig SaveItConfig = new SaveItConfig(p);

	protected BackUp(Main i, SaveItConfig i2) {
		this.p = i;
		this.SaveItConfig = i2;
		p._cs = Bukkit.getServer().getConsoleSender();
	}

	protected void check() {
		// Check if SaveItBackups folder doesn't exist, and create it.
		File ff;
		ff = new File(rootdir, "SaveItBackups");
		if (!ff.exists()) {
			ff.mkdirs();
		}
	}

	protected void kcheck() {
		SaveItConfig.load();
		if (SaveItConfig.EnableBackup) {
			long timeStamp = System.currentTimeMillis() / 1000L;
			long date = timeStamp + SaveItConfig.DateIntv * 24 * 60 * 60;
			if (SaveItConfig.AutoBackup) {
				// Check if we are using the Given Daily Backup
				if ((SaveItConfig.Decide).equalsIgnoreCase("DAY")) {
					// If we didn't put the time to the Date yet.
					if (SaveItConfig.Date == 0) {
						p.config.set(String.valueOf("BackUp.Date"), date);
						try {
							p.config.save(SaveItConfig.configFile);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					// If we already put the Date there
					else {
						// If the Date is in the YML older than the current Date
						if (SaveItConfig.Date <= timeStamp) {
							// Set the newest Date
							p.config.set(String.valueOf("BackUp.Date"), date);
							try {
								p.config.save(SaveItConfig.configFile);
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

	protected void backupdir() {
		SaveItConfig.load();
		delZip();
		if (SaveItConfig.Debug) {
			sendConsoleMessage(ChatColor.GREEN + "Starting Backup.....");
		}
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "saveit save");
		if (SaveItConfig.EnableBackupMSG) {
			String n = p.config.getString("BackUp.WarningMSG");
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
		try {
			ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(rootdir + "/SaveItBackups/SaveItBackup" + timeStamp + ".zip"));
			File folder = new File(rootdir);
			File[] fileList = folder.listFiles();
			assert fileList != null;
			for (File file : fileList) {
				if (file.isDirectory()) {
					if ((!file.getName().equalsIgnoreCase("SaveItBackups")) && (!SaveItConfig.Directory.contains(file.getName()))) {
						zipDir(file.getAbsolutePath(), zos);
					}
				} else {
					zipDir(file.getAbsolutePath(), zos);
				}
			}
			zos.closeEntry();
			//remember close it
			zos.close();
			if (SaveItConfig.EnableBackupMSG) {
				String n = p.config.getString("BackUp.WarningMSG2");
				Bukkit.getServer().broadcastMessage(colorize(n));
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (!SaveItConfig.DisableDefaultWorldSave) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on");
		}
		if (SaveItConfig.Debug) {
			sendConsoleMessage(ChatColor.GREEN + "Done!");
		}
	}

	private void zipDir(String dir2zip, ZipOutputStream zos) {
		try {
			//create a new File object based on the directory we
			//have to zip File
			File zipDir = new File(dir2zip);
			//get a listing of the directory content
			String[] dirList = zipDir.list();
			byte[] readBuffer = new byte[2156];
			int bytesIn = 0;
			//loop through dirList, and zip the files
			for (String aDirList : dirList) {
				File f = new File(zipDir, aDirList);
				if (f.isDirectory()) {
					//if the File object is a directory, call this
					//function again to add its content recursively
					String filePath = f.getPath();
					zipDir(filePath, zos);
					//loop again
					continue;
				}
				//if we reached here, the File object f was not
				//a directory
				//create a FileInputStream on top of f
				FileInputStream fis = new FileInputStream(f);
				//create a new zip entry
				ZipEntry anEntry = new ZipEntry(f.getPath());
				//place the zip entry in the ZipOutputStream object
				zos.putNextEntry(anEntry);
				//now write the content of the file to the ZipOutputStream
				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				//close the Stream
				fis.close();
			}
		} catch (Exception e) {
			//handle exception
		}
	}

	protected void delZip() {
		File folder = new File(rootdir + "/SaveItBackups/");
		// List files
		File[] fileList = folder.listFiles();
		// Assert it
		assert fileList != null;
		// Make the purgetime. Basically defines that how old should the file be, to get deleted
		long purgeTime = System.currentTimeMillis() - (SaveItConfig.daysBack * 24 * 60 * 60 * 1000);
		long lastModifiedTime = 0;
		// Yeah has to be null, because of the size...
		File lastModifiedFile = null;
		// Find the files
		for (File listFile : fileList) {
			long last = listFile.lastModified();
			// Catch the lastmodified file
			if (last > lastModifiedTime) {
				lastModifiedTime = last;
				lastModifiedFile = listFile;
			}
			// If It's older, delete it.
			if (listFile.lastModified() < purgeTime) {
				listFile.delete();
			}
			// If maxbackup is enabled
			if (SaveItConfig.MaxBackups) {
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
			}
		}
	}

	private String colorize(String s) {
		// This little code supports coloring.
		// If String is null it will return null
		if (s == null) return null;
		// Extra Stuff, taken from My SimpleNames Plugin
		s = s.replaceAll("&r", ChatColor.RESET + "");
		s = s.replaceAll("&l", ChatColor.BOLD + "");
		s = s.replaceAll("&m", ChatColor.STRIKETHROUGH + "");
		s = s.replaceAll("&o", ChatColor.ITALIC + "");
		s = s.replaceAll("&n", ChatColor.UNDERLINE + "");
		//This one Supports all the Default Colors
		return s.replaceAll("&([0-9a-f])", "\u00A7$1");
	}

	private void sendConsoleMessage(String msg) {
		// My Nice Colored Console Message Prefix.
		p._cs = p.getServer().getConsoleSender();
		p._cs.sendMessage(p._prefix + ChatColor.AQUA + msg);
	}
}

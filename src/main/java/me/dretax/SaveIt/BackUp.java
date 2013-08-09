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
 * Backup System BETA
 */
public class BackUp {
    private String rootdir = Bukkit.getServer().getWorldContainer().getPath();
    Main p;
    private SaveItConfig SaveItConfig = new SaveItConfig(p);

    protected BackUp(Main i) {
        this.p = i;
    }

    protected void check()
    {
        File ff;
        ff = new File(rootdir, "SaveItBackups");
        if(!ff.exists()) {
            ff.mkdirs();
        }
    }

    protected void kcheck() {
        System.out.println("sss");
        SaveItConfig.load();
        if (SaveItConfig.EnableBackup) {
            System.out.println("s");
            //String timeStamp = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
            long timeStamp = System.currentTimeMillis()/1000;
            long date = timeStamp + SaveItConfig.DateIntv*24*60*60;
            if (SaveItConfig.AutoBackup) {
                System.out.println("s2");
                if ((SaveItConfig.Decide).equalsIgnoreCase("DAY")) {
                    System.out.println("s3");
                    if (SaveItConfig.Date == 0) {
                        SaveItConfig.config.set(String.valueOf(SaveItConfig.Date), date);
                        System.out.println("s4");
                    }
                    else {
                        if (SaveItConfig.Date < date) {
                            SaveItConfig.config.set(String.valueOf(SaveItConfig.Date), date);
                            backupdir();
                        }
                    }
                }
            }
        }
    }

    protected void backupdir()
    {
        if(SaveItConfig.Debug) {
            sendConsoleMessage(ChatColor.GREEN + "Starting Backup.....");
        }
        SaveItConfig.load();
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "saveit save");
        if (SaveItConfig.EnableBackupMSG) {
            String n = SaveItConfig.config.getString("BackUp.WarningMSG");
            Bukkit.getServer().broadcastMessage(colorize(n));
        }
        if(!SaveItConfig.DisableDefaultWorldSave) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-off");
        }
        if (SaveItConfig.KickBackup) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                p.kickPlayer(SaveItConfig.KickBackupMSG);
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        try {
            ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(rootdir + "/SaveItBackups/SaveItBackup" + timeStamp + ".zip"));
            File folder = new File(rootdir);
            File[] fileList = folder.listFiles();
            assert fileList != null;
            for (File file : fileList) {
                if(file.isDirectory()) {
                    if(!file.getName().equalsIgnoreCase("SaveItBackups")) {
                        zipDir(file.getAbsolutePath(), zos);
                    }
                }
                else {
                    zipDir(file.getAbsolutePath(), zos);
                }
            }
            zos.closeEntry();
            //remember close it
            zos.close();
            if (SaveItConfig.EnableBackupMSG) {
                String n = SaveItConfig.config.getString("BackUp.WarningMSG2");
                Bukkit.getServer().broadcastMessage(colorize(n));
            }
            kcheck();
            //TODDO: Ellenőrizd, hogy beírja az új dátumot ha az kisebb mint a mostani
        }catch(IOException ex){
            ex.printStackTrace();
        }
        if(!SaveItConfig.DisableDefaultWorldSave) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-on");
        }
        if(SaveItConfig.Debug) {
            sendConsoleMessage(ChatColor.GREEN + "Done!");
        }
    }

    private void zipDir(String dir2zip, ZipOutputStream zos)
    {
        try
        {
            //create a new File object based on the directory we
            //have to zip File
            File zipDir = new File(dir2zip);
            //get a listing of the directory content
            String[] dirList = zipDir.list();
            byte[] readBuffer = new byte[2156];
            int bytesIn = 0;
            //loop through dirList, and zip the files
            for(int i=0; i<dirList.length; i++)
            {
                File f = new File(zipDir, dirList[i]);
                if(f.isDirectory())
                {
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
                while((bytesIn = fis.read(readBuffer)) != -1)
                {
                    zos.write(readBuffer, 0, bytesIn);
                }
                //close the Stream
                fis.close();
            }
        }
        catch(Exception e)
        {
            //handle exception
        }
    }
    private String colorize(String s) {
        // This little code supports coloring.
        // If String is null it will return null
        if(s == null) return null;
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
        p._cs.sendMessage(p._prefix + ChatColor.AQUA + msg);
    }
}

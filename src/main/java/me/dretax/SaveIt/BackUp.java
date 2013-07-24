package me.dretax.SaveIt;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    private static String rootdir = Bukkit.getServer().getWorldContainer().getPath();

    protected static void check()
    {
        File ff;
        ff = new File(rootdir, "SaveItBackups");
        if(!ff.exists()) {
            ff.mkdirs();
        }
    }

    protected static void backupdir()
    {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "saveit save");
        System.out.println("Starting Backup.....");
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

            System.out.println("Done!");
            if (SaveItConfig.EnableBackupMSG) {
                String n = Bukkit.getServer().getPluginManager().getPlugin("SaveIt").getConfig().getString("BackUp.WarningMSG2");
                Bukkit.getServer().broadcastMessage(colorize(n));
            }


        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    private static void zipDir(String dir2zip, ZipOutputStream zos)
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
    private static String colorize(String s) {
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
}

package ru.lgi.main;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.JOptionPane;


/**
 *
 * @author Thomas Otero (H3R3T1C)
 */
public class Main{

    private final String root = "updater/update/";
    public static String ver = "server_1";

     public Main() {
        System.out.print("Contacting Download Server...");
        download();
        launch();
     }
    private void download()
    {
       
               try {
                    downloadFile(getDownloadLinkFromHost());
                    unzip();
                    copyFiles(new File(root),new File("").getAbsolutePath());
                    cleanup();
                    
                    System.out.print("\nUpdate Finished!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "An error occured while preforming update!");
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
    }
    private void launch()
    {
        String[] run = {"java","-jar","Server.jar", "5674"};
        try {
            Runtime.getRuntime().exec(run);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.exit(0);
    }
    private void cleanup()
    {
    	System.out.print("\nPreforming clean up...");
       // File f = new File("c:\\update\1.zip"); http://www.dreamincode.net/forums/topic/190944-creating-an-updater-in-java/
        
        new File(ver + ".zip").delete();
        	
        remove(new File(root));
        //new File(ver + ".zip").delete();
    }
    private void remove(File f)
    {
        File[]files = f.listFiles();
        for(File ff:files)
        {
            if(ff.isDirectory())
            {
                remove(ff);
                ff.delete();
            }
            else
            {	
            	ff.delete();
                
            }
        }
    }
    private void copyFiles(File f,String dir) throws IOException
    {
        File[]files = f.listFiles();
        for(File ff:files)
        {
            if(ff.isDirectory()){
                new File(dir+"/"+ff.getName()).mkdir();
                copyFiles(ff,dir+"/"+ff.getName());
            }
            else
            {
                copy(ff.getAbsolutePath(),dir+"/"+ff.getName());
            }

        }
    }
    public void copy(String srFile, String dtFile) throws FileNotFoundException, IOException{

          File f1 = new File(srFile);
          File f2 = new File(dtFile);

          InputStream in = new FileInputStream(f1);

          OutputStream out = new FileOutputStream(f2);

          byte[] buf = new byte[1024];
          int len;
          while ((len = in.read(buf)) > 0){
            out.write(buf, 0, len);
          }
          in.close();
          out.close();
      }
    private void unzip() throws IOException
    {
         int BUFFER = 2048;
         BufferedOutputStream dest = null;
         BufferedInputStream is = null;
         ZipEntry entry;
		ZipFile zipfile = new ZipFile(root + ver + ".zip");
         @SuppressWarnings("rawtypes")
		Enumeration e = zipfile.entries();
         (new File(root)).mkdir();
         while(e.hasMoreElements()) {
            entry = (ZipEntry) e.nextElement();
            System.out.print("\nExtracting: " +entry);
            if(entry.isDirectory())
                (new File(root+entry.getName())).mkdir();
            else{
                (new File(root+entry.getName())).createNewFile();
                is = new BufferedInputStream
                  (zipfile.getInputStream(entry));
                int count;
                byte data[] = new byte[BUFFER];
                FileOutputStream fos = new
                  FileOutputStream(root+entry.getName());
                dest = new
                  BufferedOutputStream(fos, BUFFER);
                while ((count = is.read(data, 0, BUFFER))
                  != -1) {
                   dest.write(data, 0, count);
                }
                dest.flush();
                dest.close();
                is.close();
            }
         }

         zipfile.close();
    }
    private void downloadFile(String link) throws MalformedURLException, IOException
    {
        URL url = new URL(link);
        URLConnection conn = url.openConnection();
        InputStream is = conn.getInputStream();
        long max = conn.getContentLength();
        System.out.print("\n"+"Downloding file...\nUpdate Size(compressed): "+max+" Bytes");
        BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(new File(root + ver + ".zip")));
        byte[] buffer = new byte[32 * 1024];
		int bytesRead = 0;
		@SuppressWarnings("unused")
		int in = 0;
        while ((bytesRead = is.read(buffer)) != -1) {
            in += bytesRead;
            fOut.write(buffer, 0, bytesRead);
        }
        fOut.flush();
        fOut.close();
        is.close();
        System.out.print("\nDownload Complete!");

    }
    private String getDownloadLinkFromHost() throws MalformedURLException, IOException
    {
        String path = "http://laughingman.ru/chat_update/";
        return (path + ver + ".zip").trim();
    }
    public static void main(String args[]) {
       // java.awt.EventQueue.invokeLater(new Runnable() {
          //  public void run() {
            	//ver = args[0];
                new Main();
           // }
        //});
    }


}


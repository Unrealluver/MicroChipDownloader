/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dian;

import com.sun.glass.ui.Window.Level;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;



@ActionID(
        category = "Downloading",
        id = "org.dian.OpenWindowAction"
)
@ActionRegistration(
        iconBase = "org/dian/fire24.png",
        displayName = "#CTL_OpenWindowAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/RunProject", position = 0)
    ,
  @ActionReference(path = "Toolbars/How Do I?", position = 300)
})
@Messages("CTL_OpenWindowAction=MicroChipDownloader")
public final class OpenWindowAction implements ActionListener {
    public static boolean ifPerform = true;

    @Override
    public void actionPerformed(ActionEvent e) {
        if(ifPerform) {
            ifPerform = false;
            doActionPerform(ifPerform);
        }
    }
    
    private static void  callCmd(String locationCmd, javax.swing.JTextArea j){
        StringBuilder sb = new StringBuilder();
        try {
            Process child = Runtime.getRuntime().exec(locationCmd);
            j.append("检测脚本运行完毕." + "\n");
        } catch (IOException e) {
            j.append("找不到监测脚本!" + "\n");
            System.out.println(e);
        }
     }
    
    public static void list(File file, javax.swing.JTextArea j) {
		// 获取了当前目录下的所有文件和文件夹
	File[] listFiles = file.listFiles();
	if (listFiles == null) {
            return;
	}
	for (File file2 : listFiles) {
            if (file2.isFile()) {
		System.out.println(file2.getName());
                j.append(file2.getName() + "\n");
            }
	    // 如果是文件夹list
	    if (file2.isDirectory()) {
		System.out.println(file2.getName());
                j.append(file2.getName() + "\n");
		list(file2, j);
	    }
	}
    }
    
    //BIN_LIB为JAR包中存放DLL的路径   
    //getResourceAsStream以JAR中根路径为开始点   
    private void loadLib(String libName, javax.swing.JTextArea j) throws IOException {   
        String systemType = System.getProperty("os.name");   
        String libExtension = (systemType.toLowerCase().indexOf("win")!=-1) ? ".dll" : ".so";   

//        String libFullName = libName + libExtension;   
        String libFullName = libName;

        String nativeTempDir = System.getProperty("java.io.tmpdir");   

        InputStream in = null;   
        BufferedInputStream reader = null;   
        FileOutputStream writer = null;   
        
        String BIN_LIB = "resources/";
        File extractedLibFile = new File(nativeTempDir+File.separator+libFullName);   
        if(!extractedLibFile.exists()){   
            try {   
                in = getClass().getClassLoader().getResourceAsStream(BIN_LIB + libFullName);   
//                j.append("inputstream: " + BIN_LIB + libFullName + "\n");
//                if(in==null)   
//                    in =  SMAgent.class.getResourceAsStream(libFullName);   
//                SMAgent.class.getResource(libFullName);   
                reader = new BufferedInputStream(in);   
                writer = new FileOutputStream(extractedLibFile);   

                byte[] buffer = new byte[1024];   

                while (reader.read(buffer) > 0){   
                    writer.write(buffer);   
                    buffer = new byte[1024];   
                }   
            } catch (IOException e){   
                e.printStackTrace();   
            } finally {   
                if(in!=null)   
                    in.close();   
                if(writer!=null)   
                    writer.close();   
            }   
        }   
//        System.load(extractedLibFile.toString());   
    }  

    private boolean ifInvorimentSatisfied() {
        File invoFile1 = new File("C:\\\\Windows\\\\System32\\\\rxtxParallel.dll");
        File invoFile2 = new File("C:\\\\Windows\\\\System32\\\\rxtxSerial.dll");
//        j.append(nativeTempDir + "\\test.bat \n");
        if(invoFile1.exists()&&invoFile2.exists())
            return true;
        else
            return false;
    }

    private void installInvorimentComponent(javax.swing.JTextArea j) throws IOException {
        loadLib("test.bat", j);
        loadLib("rxtxParallel.dll", j);
        loadLib("rxtxSerial.dll", j);

        String nativeTempDir = System.getProperty("java.io.tmpdir");   
        String batPath = nativeTempDir + "\\test.bat";
        
        File batFile = new File(batPath);
//        j.append(nativeTempDir + "\\test.bat \n");
        Boolean batFileExist = batFile.exists();
        j.append("找到环境监测脚本. " + "\n");
        System.out.println("batFileExist:" + batFileExist);
        if (batFileExist) 
            callCmd(batPath, j);
    }

    private void doActionPerform(boolean ifPerform) {
        OutputWindowTopComponent instance = OutputWindowTopComponent.getInstance();
        javax.swing.JTextArea j = instance.getJTextArea1();    
        j.setText("");
        
        if(ifInvorimentSatisfied())
            j.append("下载器运行环境监测合格!\n");
        else {
            j.append("正在安装下载器运行环境!\n");
            try {
                installInvorimentComponent(j);
                ifPerform = true;
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
                j.append("环境安装失败!");
                ifPerform = true;
            }
        }
   
        Mode outputMode = WindowManager.getDefault().findMode("output");
        outputMode.dockInto(instance);
        instance.open();
        instance.requestActive();

        // send enter and waiting for reset
        SerialPortUtils instanceSerial = SerialPortUtils.getInstance();
        instanceSerial.setProgressBar(instance.getProgressBar2());
        instanceSerial.setOutputText(instance.getJTextArea1());
        instance.getProgressBar2().setValue(0);
        instance.getProgressBar1().setValue(0);
        // wait for reset and then download the program to the board
        
//        j.setText("");
        j.append("下载程序已启动.\n");
        WaitAndDownload waitProcess = new WaitAndDownload(instance.getProgressBar2(), instance.getProgressBar1(), instanceSerial, instance.getJTextArea1());
        new Thread(waitProcess).start();
    }
    
    
}

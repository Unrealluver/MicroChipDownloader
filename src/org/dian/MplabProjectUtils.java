/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dian;

import com.microchip.mplab.nbide.embedded.makeproject.*;
import com.microchip.mplab.nbide.embedded.makeproject.api.configurations.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.List;
import org.netbeans.spi.project.ProjectConfigurationProvider;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ProjectConfiguration;
import org.openide.filesystems.FileObject;
/**
 *
 * @author 69084
 */
public class MplabProjectUtils {
    
    private javax.swing.JTextArea textArea;
    
    public void setTextArea(javax.swing.JTextArea textArea){
        this.textArea = textArea;
    }
    
    public String getHexFileContent(){
        Project project = HotProject.getProject();
        FileObject fileObject = project.getProjectDirectory();
        String defaultConf = project.getLookup().lookup(ProjectConfigurationProvider.class).getActiveConfiguration().getDisplayName();
        String hexFileFolderPath = fileObject.getPath() + "/dist/" + defaultConf + "/production";
        File hexFileFolder = new File(hexFileFolderPath);
        try {
            File[] listFiles = hexFileFolder.listFiles();
            for (File file: listFiles){
                if (file.getName().contains(".hex")) {
                    System.out.println(file.getName());
                    FileReader fileReader = new FileReader(file);
                    BufferedReader bfr = new BufferedReader(fileReader);
                    String read;
                    StringBuilder sb = new StringBuilder();
                    while ((read = bfr.readLine()) != null) {
                        sb.append(read).append("\n");
                    }
                    bfr.close();
                    fileReader.close();
                    
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } 
        return "";
    }
    
}

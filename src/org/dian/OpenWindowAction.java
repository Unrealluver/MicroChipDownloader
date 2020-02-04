/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dian;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
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
@Messages("CTL_OpenWindowAction=The Angry Code")
public final class OpenWindowAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        OutputWindowTopComponent instance = OutputWindowTopComponent.getInstance();
//        instance.availableModes();
        
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
        javax.swing.JTextArea j = instance.getJTextArea1();
        j.setText("");
        j.append("下载程序已启动.\n");
        WaitAndDownload waitProcess = new WaitAndDownload(instance.getProgressBar2(), instance.getProgressBar1(), instanceSerial, instance.getJTextArea1());
        new Thread(waitProcess).start();
    }
}

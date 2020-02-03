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
        TestOutputWindowTopComponent instance = TestOutputWindowTopComponent.getInstance();
//        instance.availableModes();
        
        Mode outputMode = WindowManager.getDefault().findMode("output");
        outputMode.dockInto(instance);
//       instance.setEnabled(true);
        instance.open();
        instance.requestActive();

    // send enter and waiting for reset
    //        SerialPortTest1 instance = SerialPortTest1.getInstance();
        //wait for reset and then download the program to the board
//        WaitAndDownload waitProcess = new WaitAndDownload(instance., instance.getProgressBar1(), instance, instance.getExcuteBtn());
//        new Thread(waitProcess).start();
    }
}

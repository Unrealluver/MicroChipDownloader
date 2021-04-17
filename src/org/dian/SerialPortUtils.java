/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.util.HashSet;
import org.openide.util.Exceptions;

public class SerialPortUtils implements Runnable, SerialPortEventListener {

    // 检测系统中可用的通讯端口类
    private CommPortIdentifier portId;
    // 枚举类型
    private Enumeration<CommPortIdentifier> portList;

    // RS232串口
    private SerialPort serialPort;

    public SerialPort getSerialPort() {
        return serialPort;
    }

    // 输入输出流
    private InputStream inputStream;
    private OutputStream outputStream;

    // 保存串口返回信息
    private String recvMsg = "";

    public void setRecvMsg(String recvMsg) {
        this.recvMsg = recvMsg;
    }

    // 单例创建
    private static SerialPortUtils uniqueInstance = new SerialPortUtils();

    //波特率
    private static final int SEND_RETE = 4800;

    //烧录起始信号
    private static final String BEGIN_SIGNAL = "W_Hex>";

    // 初始化串口
    @SuppressWarnings("unchecked")
    public boolean init(String portName) {
        // 获取系统中所有的通讯端口
        portList = CommPortIdentifier.getPortIdentifiers();
        // 循环通讯端口
        while (portList.hasMoreElements()) {
            portId = portList.nextElement();
            // 判断是否是串口
            if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                // 比较串口名称是否是"COM3"
                if (portName.equals(portId.getName())) {
                    System.out.println("找到串口" + portName);
                    outputText.append("找到串口" + portName + "\n");
                    // 打开串口
                    try {
                        // open:（应用程序名【随意命名】，阻塞时等待的毫秒数）
                        serialPort = (SerialPort) portId.open(this.getClass().getSimpleName(), 2000);
                        System.out.println("获取到串口对象" + portName);
                        outputText.append("获取到串口对象" + portName + "\n");
                        //实例化输入流
                        inputStream = serialPort.getInputStream();
                        // 设置串口监听
                        serialPort.addEventListener(this);
                        // 设置串口数据时间有效(可监听)
                        serialPort.notifyOnDataAvailable(true);
                        // 设置串口通讯参数
                        // 波特率，数据位，停止位和校验方式
                        // 波特率4800,偶校验
                        serialPort.setSerialPortParams(SEND_RETE, SerialPort.DATABITS_8, //
                                SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                        return true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }

                }
            }
        }
        return false;
    }

    // 实现接口SerialPortEventListener中的方法 读取从串口中接收的数据
    @Override
    public void serialEvent(SerialPortEvent event) {
        System.out.println("收到串口信息");
        switch (event.getEventType()) {
            case SerialPortEvent.BI: // 通讯中断
            case SerialPortEvent.OE: // 溢位错误
            case SerialPortEvent.FE: // 帧错误
            case SerialPortEvent.PE: // 奇偶校验错误
            case SerialPortEvent.CD: // 载波检测
            case SerialPortEvent.CTS: // 清除发送
            case SerialPortEvent.DSR: // 数据设备准备好
            case SerialPortEvent.RI: // 响铃侦测
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY: // 输出缓冲区已清空
                break;
            case SerialPortEvent.DATA_AVAILABLE: // 有数据到达
                readComm();
                break;
            default:
                break;
        }
    }

    // 读取串口返回信息
    public void readComm() {
        System.out.println("开始读取串口信息.");
        byte[] readBuffer = new byte[1024];
        try {
            inputStream = serialPort.getInputStream();
            // 从线路上读取数据流
            int len = 0;
            while ((len = inputStream.read(readBuffer)) != -1) {
                String recv = new String(readBuffer, 0, len);
                if (recv.length() > 0) //                    System.out.println("实时反馈：" + recv + new Date());
                {
                    recvMsg += recv;
                }
//                outputText.append("收到串口返回消息: " + recvMsg + "\n");
                break;
            }
            //closeSerialPort();
        } catch (IOException e) {
            System.out.println("读取串口信息出错.");
            serialPort = null;
            e.printStackTrace();
        }
    }

    public void closeSerialPort() {
        try {
            inputStream = null;
            serialPort.notifyOnDataAvailable(false);
            serialPort.removeEventListener();
            serialPort.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            recvMsg = "";
            serialPort = null;
        }
    }

    //向串口发送数据
    public void sendMsg(String information) throws Exception {
        //实例化输出流
        outputStream = serialPort.getOutputStream();
        outputStream.write(information.getBytes());

        //按行发送
//        String[] strs = information.split("\n");
//        for (String str : strs){
//            str += "\r";
//            outputStream.write(str.getBytes());
//            progressBar.setValue(progressBar.getValue() + 1);
//        }
    }

    //向串口发送回车并等待按下reset按钮
    public boolean sendEnterAndWaiting() throws Exception {
        recvMsg = "";
        
        if (serialPort == null) {
            HashSet<CommPortIdentifier> portSet = SerialTool.getAvailableSerialPorts();
            System.out.println("PortSet Size: " + portSet.size());
            for (CommPortIdentifier comm : portSet) {
                System.out.println("CommPortIdentifire: " + comm.getName());
                String portInfo = comm.getName();
                boolean opened = init(portInfo);
                System.out.println("串口为空时: " + recvMsg);
                if (opened && waitForReset()) {
                    return true;
                }
                closeSerialPort();
            }
        } else {
            System.out.println("串口不为空时: " + recvMsg);
            return waitForReset();
        }
        
        return false;
    }

    private boolean waitForReset() throws Exception {
        System.out.println("wait for reset start: " + recvMsg);
        if (progressBar != null)
            progressBar.setValue(0);
        //实例化输出流
        outputStream = serialPort.getOutputStream();

        long t1 = System.currentTimeMillis();

        //每1s设置一次进度条
        long temp = 0;
        while (true) {
            long t2 = System.currentTimeMillis();
            if (t2 - t1 > 5 * 1000) {
                if (progressBar != null)
                    progressBar.setValue(progressBar.getMaximum());
                break;
            }
            if (t2 - temp - 500 > 0) {
                System.out.println("wait for reset 倒计时: " + recvMsg);
                if (progressBar != null)
                    progressBar.setValue(progressBar.getValue() + 1);
                temp = t2;  
                String information = "\r";
                outputStream.write(information.getBytes());
            }
            
            System.out.println("before contains: " + recvMsg);
            if (recvMsg.contains(BEGIN_SIGNAL)) {
                System.out.println("reset bug fix: " + recvMsg);
                if (progressBar != null)
                    progressBar.setValue(progressBar.getMaximum());
                System.out.println("返回true: " + recvMsg);
                return true;
            }
        }
        System.out.println("返回false: " + recvMsg);
        return false;
    }

    @Override
    public void run() {
        //TODO
    }

    public static SerialPortUtils getInstance() {
        return uniqueInstance;
    }

    //top component
    private javax.swing.JTextArea outputText;
    private javax.swing.JProgressBar progressBar;

    public void setOutputText(javax.swing.JTextArea outputText) {
        this.outputText = outputText;
        SerialTool.outputText = outputText;
    }

    public void setProgressBar(javax.swing.JProgressBar progressBar) {
        this.progressBar = progressBar;
    }

}

class WaitAndDownload implements Runnable {

    javax.swing.JProgressBar waitBar;
    javax.swing.JProgressBar sendBar;
    SerialPortUtils instance;
//    javax.swing.JButton sendButton;
    javax.swing.JTextArea outputText;

    public WaitAndDownload(javax.swing.JProgressBar waitBar,
            javax.swing.JProgressBar sendBar,
            SerialPortUtils instance,
            javax.swing.JTextArea outputText) {
        this.waitBar = waitBar;
        this.instance = instance;
        this.sendBar = sendBar;
//        this.sendButton = sendButton;
        this.outputText = outputText;
    }

    @Override
    public void run() {
        outputText.append("请触发MCU的reset按键.\n");
        waitBar.setMaximum(10);
        waitBar.setValue(0);
        try {
            if (instance.sendEnterAndWaiting()) {
                System.out.println("start to send");
                outputText.append("检测到对应的MCU芯片.\n");
                
                // get the hex file
                MplabProjectUtils utils = new MplabProjectUtils();
                outputText.append("获取hex代码文件中...\n");
                String sendContent = utils.getHexFileContent();
                outputText.append("hex代码文件准备完毕.\n");
                //download to the board by line
                String[] codes = sendContent.split("\n");
                sendBar.setMaximum(codes.length);
                sendBar.setValue(0);
                outputText.append("准备发送二进制代码文件中.\n");
                for (String line : codes) {
                    line += "\r";
                    instance.sendMsg(line);
                    System.out.println("print code lines: " + line);
                    sendBar.setValue(sendBar.getValue() + 1);
                }
                outputText.append("代码发送完毕.\n");
                //excute
                instance.sendMsg("g\r");
                System.out.println("发送启动信号\n");
                outputText.append("代码开始执行.\n");
            } else {
                outputText.append("未检测到对应的MCU芯片.\n");
            }
        } catch (Exception e) {
            outputText.append("出现异常错误.\n");
            e.printStackTrace();
        } finally {
            try {
                //            sendButton.setEnabled(true);
                outputText.append("端口释放中...\n");
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            }
            instance.closeSerialPort();
            outputText.append("端口占用解除.\n");
            OpenWindowAction.ifPerform = true;
        }
    }
}

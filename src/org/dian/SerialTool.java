/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dian;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.util.HashSet;
/**
 *
 * @author Jestiny
 */
/**
 * 串口服务类，提供打开、关闭串口，读取、发送串口数据等服务（采用单例设计模式）
 * @author Jestiny
 *
 */
public class SerialTool {
    public static javax.swing.JTextArea outputText;
    
    /**
     * @Description:列出所有可用串口
     * @author:Jestiny
     * @date:2018年11月27日, PM 02:50:48
     */
    public static void listPorts() {
        HashSet<CommPortIdentifier> portSet = getAvailableSerialPorts();
        for (CommPortIdentifier comm : portSet) {
            System.out.println(comm.getName() + " - " + getPortTypeName(comm.getPortType()));
            outputText.append("检测到串口: " + comm.getName() + " - " 
                    + getPortTypeName(comm.getPortType()) + "\n");
        }
    }
 
    /**
     * @Description:列出所有通信端口
     * @author:Jestiny
     * @date:2018年11月27日, PM 02:51:00
     */
    @SuppressWarnings("unchecked")
    public static void listCommPorts() {
        CommPortIdentifier.getPortIdentifiers();
        /*
         * 不带参数的getPortIdentifiers方法可以获得一个枚举对象，该对象包含了
         * 系统中每个端口的CommPortIdentifier对象。注意这里的端口不仅仅是指串口，也包括并口。
         * 这个方法还可以带参数，getPortIdentifiers(CommPort)获得已经被应用程序打开的端口
         * 相对应的CommPortIdentifier对象。getPortIdentifier(String portName)
         * 获取指定端口名（比如“COM1”）的CommPortIdentifier对象。
         */
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            System.out.println(portIdentifier.getName() + " - " + getPortTypeName(portIdentifier.getPortType()));
            outputText.append("检测到端口对象: " + portIdentifier.getName() + " - " 
                    + getPortTypeName(portIdentifier.getPortType()) + "\n");
        }
    }
 
    /**
     * @Description:获取通信端口类型名称
     * @author:Jestiny
     * @date:2018年11月27日, PM 02:51:13
     */
    public static String getPortTypeName(int portType) {
        switch (portType) {
        case CommPortIdentifier.PORT_I2C:
            return "I2C";
        case CommPortIdentifier.PORT_PARALLEL: // 并口
            return "Parallel";
        case CommPortIdentifier.PORT_RAW:
            return "Raw";
        case CommPortIdentifier.PORT_RS485: // RS485端口
            return "RS485";
        case CommPortIdentifier.PORT_SERIAL: // 串口
            return "Serial";
        default:
            return "unknown type";
        }
    }
 
    /**
     * @Description:获取所有可用的串口集合
     * @author:Jestiny
     * @date:2018年11月27日, PM 02:51:24
     */
    @SuppressWarnings("unchecked")
    public static HashSet<CommPortIdentifier> getAvailableSerialPorts() {
        HashSet<CommPortIdentifier> h = new HashSet<>();
        Enumeration<CommPortIdentifier> portList = null;
        try {
            portList = CommPortIdentifier.getPortIdentifiers();
        } catch(Throwable t) {
            outputText.setText("为载入下载器运行环境, 请重启IDE.\n");
            System.exit(1);
        }

        while (portList.hasMoreElements()) {
            CommPortIdentifier com = (CommPortIdentifier) portList.nextElement();
            switch (com.getPortType()) {
            case CommPortIdentifier.PORT_SERIAL:
                try {
                    // open:（应用程序名【随意命名】，阻塞时等待的毫秒数）
                    /*
                     * open方法打开通讯端口，获得一个CommPort对象，它使程序独占端口。
                     * 如果端口正被其他应用程序占用，将使用CommPortOwnershipListener事件机制
                     * 传递一个PORT_OWNERSHIP_REQUESTED事件。
                     * 每个端口都关联一个InputStream和一个OutputStream,如果端口是用
                     * open方法打开的，那么任何的getInputStream都将返回相同的数据流对象，除非 有close被调用。
                     */
                    CommPort thePort = com.open(Object.class.getSimpleName(), 50);
                    thePort.close();
                    h.add(com);
                } catch (PortInUseException e) {
                    // 不可用串口
                    System.out.println("Port, " + com.getName() + ", is in use.");
                    outputText.append("端口对象: " + com.getName() + ", 正在使用.\n");
                } catch (Exception e) {
                    System.err.println("Failed to open port " + com.getName());
                    outputText.append("打开端口: " + com.getName()+ " 遭遇失败.\n");
                    e.printStackTrace();
                }
            }
        }
        return h;
    }
 
}
/*
 * Written by Mindaugas Burvys PI17C
 * 
 * Project assignment for robot's pbject recognition system
 *
 * This class is used to directly control arduino controller over usb port

 */
package robot.camera;

import com.fazecast.jSerialComm.*;


/**
 *
 * @author blade
 */
public class ArduinoCommunication implements Runnable{

    private SerialPort sPort;
    private String usbPort;
    private int baudRate;
    
    public ArduinoCommunication(String usbPort, int baudRate){
        this.usbPort = usbPort;
        sPort = SerialPort.getCommPort(this.usbPort);
        this.baudRate = baudRate;
        sPort.setBaudRate(this.baudRate);
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}

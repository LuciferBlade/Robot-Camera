/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.camera;

/**
 *
 * @author blade
 */
public class RobotCamera {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String usbPort = "/dev/ttyUSB0";
        int baudRate = 9600;
        ArduinoCommunication rt = new ArduinoCommunication(usbPort, baudRate);
        System.out.println("");
    }
}

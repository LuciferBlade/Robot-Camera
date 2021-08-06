/*
 * Written by Mindaugas Burvys PI17C
 * 
 * Project assignment for robot's pbject recognition system
 *
 * This class is used to directly control arduino controller over usb port
 *
 */
package robot.camera;

import com.fazecast.jSerialComm.*;
import java.awt.Color;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.JLabel;
import javax.swing.JTextArea;

/**
 *
 * @author blade
 */
public class ArduinoCommunication {

    private SerialPort sPort;
    private String usbPort = "";
    private int baudRate = -10;
    private Scanner input;
    private PrintWriter output;

    private boolean check = false;
    private JTextArea textArea;
    private JLabel warnArea;

    private boolean initiated = false;

    private String receivedMsg;

    public ArduinoCommunication() {
        //empty constructor if port undecided
    }

    //constructor with port. Requires baud rate set before communication
    public ArduinoCommunication(String usbPort) {
        this.usbPort = usbPort;
        sPort = SerialPort.getCommPort(this.usbPort);
    }

    //constructor with port and baud rate
    public ArduinoCommunication(String usbPort, int baudRate) {
        this.usbPort = usbPort;
        sPort = SerialPort.getCommPort(this.usbPort);
        this.baudRate = baudRate;
        sPort.setBaudRate(this.baudRate);
    }

    public boolean startConnection() throws InterruptedException {
        if (baudRate == -10) {
            return false;
        }
        if (usbPort.equals("")) {
            return false;
        }
        sPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
        sPort.openPort();
        input = new Scanner(sPort.getInputStream());
        output = new PrintWriter(sPort.getOutputStream());
        sPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent arg0) {
                while (sPort.bytesAvailable() > 0) {
                    receivedMsg = input.nextLine();
                    if (check == false) {
                        System.out.print(receivedMsg);
                        if (warnArea != null) {
                            warnArea.setForeground(Color.red);
                            warnArea.setText(receivedMsg);
                        }
                    } else {
                        textArea.append(receivedMsg + "\n");
                        textArea.setCaretPosition(textArea.getText().length());
                        if (warnArea != null) {
                            if (receivedMsg.startsWith("Number not right!") || receivedMsg.startsWith("ERROR!")) {
                                warnArea.setForeground(Color.red);
                            } else {
                                warnArea.setForeground(Color.BLUE);
                            }
                            warnArea.setText("Message from controller: " + receivedMsg);
                        }
                    }
                }
            }
        });

        initiated = true;

        Thread.sleep(2000);
        return true;
    }

    public boolean stopConnection() {
        sPort.removeDataListener();
        output.close();
        input.close();
        sPort.closePort();
        initiated = false;
        return true;
    }

    //set new usb port
    public void setUsbPort(String usbPort) {
        if (initiated == true) {
            stopConnection();
        }
        this.usbPort = usbPort;
        sPort = SerialPort.getCommPort(this.usbPort);
    }

    public void setCheck() {
        check = true;
    }

    public void setTextArea(JTextArea area) {
        textArea = area;
    }

    public void setWarnLabel(JLabel label) {
        warnArea = label;
    }

    //set new baud rate
    public void setBaudRate(int baudRate) {
        if (initiated == true) {
            stopConnection();
        }
        this.baudRate = baudRate;
        sPort.setBaudRate(this.baudRate);
    }

    //get usb port
    public String getPortDescription() {
        return usbPort;
    }

    //get serial port object
    public SerialPort getSerialPort() {
        return sPort;
    }

    //get baud rate
    public int getBaudRate() {
        return baudRate;
    }

    //get last message from stream
    public String getMessage() {
        return receivedMsg;
    }

    //get status if connection is present
    public boolean getStatus() {
        return initiated;
    }

    //writes string onto stream
    public void serialWrite(String msg) {
        if (!msg.isBlank() && !msg.isEmpty()) {
            byte temp[] = msg.getBytes();
            sPort.writeBytes(temp, temp.length);
        }
    }
}

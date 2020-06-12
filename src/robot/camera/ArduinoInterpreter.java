package robot.camera;

import javax.swing.JTextArea;

/**
 *
 * @author blade
 */
public class ArduinoInterpreter {

    private String message = "";
    private final ArduinoCommunication ArduCom;
    JTextArea area;

    public ArduinoInterpreter(String usbPort, int baudRate, JTextArea caller) throws InterruptedException {
        ArduCom = new ArduinoCommunication(usbPort, baudRate);
        //ArduCom.setArduInt(this);
        ArduCom.startConnection();
        area = caller;
    }

    public void setMessage(String msg) {
        message = msg;
    }

    public boolean sendMessage(String msg) throws InterruptedException {
        if (ArduCom.getStatus()) {
            ArduCom.setCheck();
            ArduCom.serialWrite(msg);
        }
        Thread.sleep(1000);
        //if (message.startsWith("ERROR!") || message.startsWith("Hello")) {
            area.append(message);
        //}
        return !message.startsWith("ERROR!");
    }

    public void transferMessage(String msg) {
        area.append(msg);
    }

    public ArduinoCommunication GetComObject() {
        return ArduCom;
    }
}

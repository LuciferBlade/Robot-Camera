package robot.camera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author blade
 */
public class RobotCamera {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        /*String usbPort = "/dev/ttyUSB0";
        int baudRate = 9600;

        ArduinoCommunication rt = new ArduinoCommunication(usbPort, baudRate);

        rt.startConnection();
        rt.serialWrite("Hello Arduino!");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String temp = "";

        while (!temp.equals("exit")) {
            temp = reader.readLine();
            if (!temp.equals("exit")) {
                rt.serialWrite(temp);
            }
            else {
                break;
            }
        }*/
        
        String temp = "-12";
        System.out.print(temp.matches("^-?\\d{1,2}$"));
    }
}

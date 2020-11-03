package robot.camera;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.HighGui;
import org.opencv.videoio.VideoCapture;

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
        
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        //Mat mat = Mat.eye(3,3, CvType.CV_8UC1);
        //System.out.println("mat = " + mat.dump());
        
        VideoCapture cam = new VideoCapture();
        Mat image = new Mat();
        cam.open(0);
        cam.read(image);
        BufferedImage temp = (BufferedImage) HighGui.toBufferedImage(image);
        File output = new File("try1.png");
        ImageIO.write(temp, "png", output);
        cam.release();
        Thread.sleep(1000);
        
        cam.open(1);
        cam.read(image);
        temp = (BufferedImage) HighGui.toBufferedImage(image);
        output = new File("try2.png");
        ImageIO.write(temp, "png", output);
        cam.release();
        
        
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
        
        //String temp = "-12";
        //System.out.print(temp.matches("^-?\\d{1,2}$"));
    }
}

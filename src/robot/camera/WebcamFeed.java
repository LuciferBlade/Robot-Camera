/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robot.camera;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author blade
 */
public class WebcamFeed implements Runnable {

    private final MatOfByte frame;
    private final VideoCapture webcam;
    private final String webcamPort;
    private volatile boolean active = false;
    private final Interface gui;
    private boolean colorRec = false;

    public WebcamFeed(String webcamPort, Interface gui) throws InterruptedException {
        frame = new MatOfByte();
        webcam = new VideoCapture();
        this.webcamPort = webcamPort;
        Thread.sleep(500);
        this.gui = gui;
    }

    public void open() {
        webcam.open(webcamPort);
        active = true;
    }

    public boolean isOpen() {
        return active;
    }

    public void release() {
        webcam.release();
    }

    public MatOfByte getFrame() {
        return frame;
    }

    public BufferedImage getImage() {
        return (BufferedImage) HighGui.toBufferedImage(frame);
    }

    public void setColorRecognition() {
        colorRec = colorRec != true;
    }

    @Override
    public void run() {
        synchronized (this) {
            if (webcam.isOpened()) {
                while (active) {
                    webcam.read(frame);
                    Imgproc.resize(frame, frame, new Size(640, 480));
                    if (colorRec == true) {
                        Mat temp = colorRecognition(frame, gui.hsv[0], gui.hsv[1],
                                gui.hsv[2], gui.hsv[3], gui.hsv[4], gui.hsv[5]);
                        BufferedImage buffImg = (BufferedImage) HighGui.toBufferedImage(temp);
                        gui.changeImage(buffImg);
                    } else {
                        BufferedImage buffImg = (BufferedImage) HighGui.toBufferedImage(frame);
                        gui.changeImage(buffImg);
                    }

                    try {
                        this.wait(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(WebcamFeed.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }

    public Mat colorRecognition(Mat frame, int minH, int minS, int minV, int maxH, int maxS, int maxV) {
        Mat hsvImage = new Mat();

        Mat thresholded = new Mat();
        Mat thresholded2 = new Mat();

        Mat array255 = new Mat(new Size(640, 480), CvType.CV_8UC1);
        array255.setTo(new Scalar(255));
        Mat distance = new Mat(new Size(640, 480), CvType.CV_8UC1);

        List<Mat> lhsv = new ArrayList<>(3);
        Mat circles = new Mat();

        Scalar minColor = new Scalar(minH, minS, minV);
        Scalar maxColor = new Scalar(maxH, maxS, maxV);

        Imgproc.cvtColor(frame, hsvImage, Imgproc.COLOR_BGR2HSV, 3);
        Core.inRange(hsvImage, minColor, maxColor, thresholded);
        Imgproc.erode(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));
        Imgproc.dilate(thresholded, thresholded, Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2)));
        
        Core.split(hsvImage, lhsv);
        Mat S = lhsv.get(1);
        Mat V = lhsv.get(2);
        Core.subtract(array255, S, S);
        Core.subtract(array255, V, V);
        S.convertTo(S, CvType.CV_32F);
        V.convertTo(V, CvType.CV_32F);

        
        Core.magnitude(S, V, distance);
        Core.inRange(distance, new Scalar(0.0), new Scalar(200.0), thresholded2);
        Core.bitwise_and(thresholded, thresholded2, thresholded);

        Imgproc.GaussianBlur(thresholded, thresholded, new Size(9, 9), 0, 0);
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.HoughCircles(thresholded, circles, Imgproc.CV_HOUGH_GRADIENT, 2,
                thresholded.height() / 8, 200, 100, 0, 0);
        Imgproc.findContours(thresholded, contours, thresholded2, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.drawContours(frame, contours, -2, new Scalar(10, 0, 0), 4);
        return thresholded;
    }
}

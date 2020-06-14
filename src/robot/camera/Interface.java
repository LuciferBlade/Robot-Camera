package robot.camera;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author blade
 */
public class Interface extends javax.swing.JFrame {

    private int valueHor = 90;
    private int valueVer = 90;
    
    ArduinoCommunication arduCom;
    String arduinoPort = "/dev/ttyUSB0";
    int baudRate = 9600;

    String webcamPort = "/dev/video1";
    
    private VideoCapture webcam;
    
    /**
     * Creates new form Interface
     * @throws java.lang.InterruptedException
     */
    public Interface() throws InterruptedException {
        initComponents();
        centerButton.setEnabled(false);
        
        upValueField.setText(String.valueOf(valueVer - 90));
        downValueField.setText(String.valueOf(90 - valueVer));
        leftValueField.setText(String.valueOf(90 - valueHor));
        rightValueField.setText(String.valueOf(valueHor - 90));
        
        upValueField.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent arg0) {
            }
            @Override
            public void focusLost(FocusEvent arg0) {
                upValueField.setText(String.valueOf(valueVer - 90));
            }
        });
        upValueField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (!upValueField.getText().matches("^-?\\d{1,2}$")){
                    messageReceiver.append(upValueField.getText()+"\n");
                    upValueField.setText(String.valueOf(valueVer - 90));
                }
                else{
                    setValueVer(Integer.parseInt(upValueField.getText())+90);
                }
            }
        });
        
        downValueField.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent arg0) {
            }

            @Override
            public void focusLost(FocusEvent arg0) {
                downValueField.setText(String.valueOf(90 -valueVer));
            }
        });
        downValueField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (!downValueField.getText().matches("^-?\\d{1,2}$")){
                    messageReceiver.append(downValueField.getText()+"\n");
                    downValueField.setText(String.valueOf(90 -valueVer));
                }
                else{
                    setValueVer(90 - Integer.parseInt(downValueField.getText()));
                }
            }
        });
        
        leftValueField.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent arg0) {
            }

            @Override
            public void focusLost(FocusEvent arg0) {
                leftValueField.setText(String.valueOf(90 - valueHor));
            }
        });
        leftValueField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (!leftValueField.getText().matches("^-?\\d{1,2}$")){
                    messageReceiver.append(leftValueField.getText()+"\n");
                    leftValueField.setText(String.valueOf(90 - valueHor));
                }
                else{
                    setValueHor(90 - Integer.parseInt(leftValueField.getText()));
                }
            }
        });
        
        rightValueField.addFocusListener(new FocusListener(){
            @Override
            public void focusGained(FocusEvent arg0) {
            }

            @Override
            public void focusLost(FocusEvent arg0) {
                rightValueField.setText(String.valueOf(valueHor - 90));
            }
        });
        rightValueField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (!rightValueField.getText().matches("^-?\\d{1,2}$")){
                    messageReceiver.append(rightValueField.getText()+"\n");
                    rightValueField.setText(String.valueOf(valueHor - 90));
                }
                else{
                    setValueHor(Integer.parseInt(rightValueField.getText()) + 90);
                }
            }
            
        });
        
        MatOfByte frame = new MatOfByte();
        webcam = new VideoCapture();
        webcam.open(webcamPort);
        Thread.sleep(500);
        if (webcam.isOpened()){
            Runnable capture = new Runnable(){
                @Override
                public void run() {
                    webcam.read(frame);
                    Imgproc.resize(frame, frame, new Size(640, 480));
                    BufferedImage buffImg = (BufferedImage) HighGui.toBufferedImage(frame);
                                        
                    camPanel.add(new JLabel(new ImageIcon(buffImg)));
                    camPanel.repaint();
                }
            
            };
            capture.run();
        }
        
                
        
        arduCom = new ArduinoCommunication(arduinoPort, baudRate);
        arduCom.setTextArea(messageReceiver);
        arduCom.setCheck();
        arduCom.startConnection();
    }

    private void setValueHor(int valueHor) {
        if (valueHor >= 0 && valueHor <= 180) {
            this.valueHor = valueHor;
            leftValueField.setText(String.valueOf(90 - this.valueHor));
            rightValueField.setText(String.valueOf(this.valueHor - 90));
            switch (valueHor) {
                case 0:
                    leftButton.setEnabled(false);
                    leftDoubleButton.setEnabled(false);
                    rightButton.setEnabled(true);
                    rightDoubleButton.setEnabled(true);
                    centerButton.setEnabled(true);
                    break;
                case 180:
                    leftButton.setEnabled(true);
                    leftDoubleButton.setEnabled(true);
                    rightButton.setEnabled(false);
                    rightDoubleButton.setEnabled(false);
                    centerButton.setEnabled(true);
                    break;
                default:
                    leftButton.setEnabled(true);
                    leftDoubleButton.setEnabled(true);
                    rightButton.setEnabled(true);
                    rightDoubleButton.setEnabled(true);
                    centerButton.setEnabled(true);
                    break;
            }
            if (this.valueVer == 90 && this.valueHor == 90){
                centerButton.setEnabled(false);
            }
        } else if (valueHor < 0) {
            this.valueHor = 0;
            leftValueField.setText(String.valueOf(90 - this.valueHor));
            rightValueField.setText(String.valueOf(this.valueHor - 90));
            leftButton.setEnabled(false);
            leftDoubleButton.setEnabled(false);
            rightButton.setEnabled(true);
            rightDoubleButton.setEnabled(true);
            centerButton.setEnabled(true);
                    
        } else if (valueHor > 180) {
            this.valueHor = 180;
            leftValueField.setText(String.valueOf(90 - this.valueHor));
            rightValueField.setText(String.valueOf(this.valueHor - 90));
            rightButton.setEnabled(false);
            rightDoubleButton.setEnabled(false);
            leftButton.setEnabled(true);
            leftDoubleButton.setEnabled(true);
            centerButton.setEnabled(true);
        }
        
        arduCom.serialWrite(this.valueHor+"&"+valueVer+"&");
    }

    private void setValueVer(int valueVer) {
        if (valueVer >= 0 && valueVer <= 180) {
            this.valueVer = valueVer;
            upValueField.setText(String.valueOf(this.valueVer - 90));
            downValueField.setText(String.valueOf(90 - this.valueVer));
            switch (valueVer) {
                case 0:
                    downButton.setEnabled(false);
                    downDoubleButton.setEnabled(false);
                    upButton.setEnabled(true);
                    upDoubleButton.setEnabled(true);
                    centerButton.setEnabled(true);
                    break;
                case 180:
                    downButton.setEnabled(true);
                    downDoubleButton.setEnabled(true);
                    upButton.setEnabled(false);
                    upDoubleButton.setEnabled(false);
                    centerButton.setEnabled(true);
                    break;
                default:
                    downButton.setEnabled(true);
                    downDoubleButton.setEnabled(true);
                    upButton.setEnabled(true);
                    upDoubleButton.setEnabled(true);
                    centerButton.setEnabled(true);
                    break;
            }
            if (this.valueVer == 90 && this.valueHor == 90){
                centerButton.setEnabled(false);
            }
        } else if (valueVer < 0) {
            this.valueVer = 0;
            upValueField.setText(String.valueOf(this.valueVer - 90));
            downValueField.setText(String.valueOf(90 - this.valueVer));
            upButton.setEnabled(true);
            upDoubleButton.setEnabled(true);
            downButton.setEnabled(false);
            downDoubleButton.setEnabled(false);
            centerButton.setEnabled(true);
        } else if (valueVer > 180) {
            this.valueVer = 180;
            upValueField.setText(String.valueOf(this.valueVer - 90));
            downValueField.setText(String.valueOf(90 - this.valueVer));
            upButton.setEnabled(false);
            upDoubleButton.setEnabled(false);
            downButton.setEnabled(true);
            downDoubleButton.setEnabled(true);
            centerButton.setEnabled(true);
        }
        
        arduCom.serialWrite(valueHor+"&"+this.valueVer+"&");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        leftButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        upDoubleButton = new javax.swing.JButton();
        rightButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        rightValueField = new javax.swing.JTextField();
        leftValueField = new javax.swing.JTextField();
        downValueField = new javax.swing.JTextField();
        downDoubleButton = new javax.swing.JButton();
        leftDoubleButton = new javax.swing.JButton();
        rightDoubleButton = new javax.swing.JButton();
        centerButton = new javax.swing.JButton();
        upValueField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        messageReceiver = new javax.swing.JTextArea();
        msgEntryPanel = new javax.swing.JPanel();
        message = new javax.swing.JTextField();
        buttonSend = new javax.swing.JButton();
        camPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        java.awt.GridBagLayout jPanel1Layout = new java.awt.GridBagLayout();
        jPanel1Layout.columnWidths = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        jPanel1Layout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        jPanel1.setLayout(jPanel1Layout);

        leftButton.setText("←");
        leftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        jPanel1.add(leftButton, gridBagConstraints);

        downButton.setText("↓");
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 8;
        jPanel1.add(downButton, gridBagConstraints);

        upDoubleButton.setText("↑ ↑");
        upDoubleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upDoubleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        jPanel1.add(upDoubleButton, gridBagConstraints);

        rightButton.setText("→");
        rightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 6;
        jPanel1.add(rightButton, gridBagConstraints);

        upButton.setText("↑");
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        jPanel1.add(upButton, gridBagConstraints);

        rightValueField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rightValueField.setText("0");
        rightValueField.setPreferredSize(new java.awt.Dimension(40, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 6;
        jPanel1.add(rightValueField, gridBagConstraints);

        leftValueField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        leftValueField.setText("0");
        leftValueField.setMinimumSize(new java.awt.Dimension(30, 30));
        leftValueField.setPreferredSize(new java.awt.Dimension(40, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        jPanel1.add(leftValueField, gridBagConstraints);

        downValueField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        downValueField.setText("0");
        downValueField.setPreferredSize(new java.awt.Dimension(40, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 12;
        jPanel1.add(downValueField, gridBagConstraints);

        downDoubleButton.setText("↓ ↓");
        downDoubleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downDoubleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 10;
        jPanel1.add(downDoubleButton, gridBagConstraints);

        leftDoubleButton.setText("← ←");
        leftDoubleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftDoubleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        jPanel1.add(leftDoubleButton, gridBagConstraints);

        rightDoubleButton.setText("→ →");
        rightDoubleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightDoubleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 6;
        jPanel1.add(rightDoubleButton, gridBagConstraints);

        centerButton.setText("+");
        centerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                centerButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        jPanel1.add(centerButton, gridBagConstraints);

        upValueField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        upValueField.setText("0");
        upValueField.setPreferredSize(new java.awt.Dimension(40, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        jPanel1.add(upValueField, gridBagConstraints);

        messageReceiver.setEditable(false);
        messageReceiver.setColumns(20);
        messageReceiver.setRows(5);
        jScrollPane1.setViewportView(messageReceiver);

        msgEntryPanel.setLayout(new java.awt.GridBagLayout());

        message.setPreferredSize(new java.awt.Dimension(200, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        msgEntryPanel.add(message, gridBagConstraints);

        buttonSend.setText("Send message");
        buttonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSendActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        msgEntryPanel.add(buttonSend, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(camPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(msgEntryPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 214, Short.MAX_VALUE)
                        .addComponent(msgEntryPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(camPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        setValueVer(valueVer + 1);
        //System.out.println(valueVer);
    }//GEN-LAST:event_upButtonActionPerformed

    private void upDoubleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upDoubleButtonActionPerformed
        setValueVer(valueVer + 10);
        //System.out.println(valueVer);
    }//GEN-LAST:event_upDoubleButtonActionPerformed

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        setValueVer(valueVer - 1);
        //System.out.println(valueVer);
    }//GEN-LAST:event_downButtonActionPerformed

    private void downDoubleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downDoubleButtonActionPerformed
        setValueVer(valueVer - 10);
        //System.out.println(valueVer);
    }//GEN-LAST:event_downDoubleButtonActionPerformed

    private void leftButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftButtonActionPerformed
        setValueHor(valueHor - 1);
        //System.out.println(valueHor);
    }//GEN-LAST:event_leftButtonActionPerformed

    private void leftDoubleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leftDoubleButtonActionPerformed
        setValueHor(valueHor - 10);
        //.out.println(valueHor);
    }//GEN-LAST:event_leftDoubleButtonActionPerformed

    private void rightButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightButtonActionPerformed
        setValueHor(valueHor + 1);
        //System.out.println(valueHor);
    }//GEN-LAST:event_rightButtonActionPerformed

    private void rightDoubleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rightDoubleButtonActionPerformed
        setValueHor(valueHor + 10);
        //System.out.println(valueHor);
    }//GEN-LAST:event_rightDoubleButtonActionPerformed

    private void centerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_centerButtonActionPerformed
        downButton.setEnabled(true);
        downDoubleButton.setEnabled(true);
        upButton.setEnabled(true);
        upDoubleButton.setEnabled(true);
        rightButton.setEnabled(true);
        rightDoubleButton.setEnabled(true);
        leftButton.setEnabled(true);
        leftDoubleButton.setEnabled(true);
        centerButton.setEnabled(false);
        valueVer = 90;
        valueHor = 90;
        arduCom.serialWrite("90&90&");
        //System.out.println(valueHor + " " + valueVer);
    }//GEN-LAST:event_centerButtonActionPerformed

    private void buttonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSendActionPerformed
        arduCom.serialWrite(message.getText());
    }//GEN-LAST:event_buttonSendActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        webcam.release();
    }//GEN-LAST:event_formWindowClosing

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Interface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Interface().setVisible(true);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonSend;
    private javax.swing.JPanel camPanel;
    private javax.swing.JButton centerButton;
    private javax.swing.JButton downButton;
    private javax.swing.JButton downDoubleButton;
    private javax.swing.JTextField downValueField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton leftButton;
    private javax.swing.JButton leftDoubleButton;
    private javax.swing.JTextField leftValueField;
    private javax.swing.JTextField message;
    private javax.swing.JTextArea messageReceiver;
    private javax.swing.JPanel msgEntryPanel;
    private javax.swing.JButton rightButton;
    private javax.swing.JButton rightDoubleButton;
    private javax.swing.JTextField rightValueField;
    private javax.swing.JButton upButton;
    private javax.swing.JButton upDoubleButton;
    private javax.swing.JTextField upValueField;
    // End of variables declaration//GEN-END:variables
}

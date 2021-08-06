package robot.camera;

import com.fazecast.jSerialComm.SerialPort;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.opencv.core.Core;
import org.opencv.videoio.VideoCapture;

/**
 *
 * @author blade
 */
public class Interface extends javax.swing.JFrame{

    //default possitions
    private int valueHor = 90;
    private int valueVer = 90;
    
    //arduino object and baud rate
    ArduinoCommunication arduCom;
    int baudRate = 9600;
    
    //variables for webcam feed view
    JLabel imgLabel = new JLabel();
    BufferedImage image = null;
    WebcamFeed webcam;
    int webcamConnected = -10;
    Thread thread;
    int hsv[] = new int[6];
    
    ArrayList<Integer> cameraList;
    
    int erode = 10;
    int dilate = 4;
    
    /**
     * Creates new form Interface
     * @throws java.lang.InterruptedException
     */
    public Interface() throws InterruptedException {
        initComponents();
        this.setTitle("Robot Eye Control Application");
        
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
                    messageReceiver.setCaretPosition(messageReceiver.getText().length());
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
                    messageReceiver.setCaretPosition(messageReceiver.getText().length());
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
                    messageReceiver.setCaretPosition(messageReceiver.getText().length());
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
                    messageReceiver.setCaretPosition(messageReceiver.getText().length());
                    rightValueField.setText(String.valueOf(valueHor - 90));
                }
                else{
                    setValueHor(Integer.parseInt(rightValueField.getText()) + 90);
                }
            }
            
        });
        
        erodeField.addFocusListener( new FocusListener(){
            @Override
            public void focusGained(FocusEvent arg0) {
            }

            @Override
            public void focusLost(FocusEvent arg0) {
                erodeField.setText(String.valueOf(erode));
            }
        });
        erodeField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (!erodeField.getText().matches("^[1-9]\\d*$")){
                    messageReceiver.append("Wrong erode value: " + erodeField.getText()+"\n");
                    messageReceiver.setCaretPosition(messageReceiver.getText().length());
                    erodeField.setText(String.valueOf(erode));
                }
                else{
                    String temp = erodeField.getText();
                    int temp2 =Integer.parseInt(temp);
                    erode = temp2;
                }
            }
        });
        
        dilateField.addFocusListener( new FocusListener(){
            @Override
            public void focusGained(FocusEvent arg0) {
            }

            @Override
            public void focusLost(FocusEvent arg0) {
                dilateField.setText(String.valueOf(dilate));
            }
        });
        dilateField.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (!dilateField.getText().matches("^[1-9]\\d*$")){
                    messageReceiver.append("Wrong dilate value: " + dilateField.getText()+"\n");
                    messageReceiver.setCaretPosition(messageReceiver.getText().length());
                    dilateField.setText(String.valueOf(dilate));
                }
                else{
                    String temp = erodeField.getText();
                    int temp2 =Integer.parseInt(temp);
                    dilate = temp2;
                }
            }
        });
        
        //refresh all available cameras
        cameraSourceBox.removeAllItems();
        VideoCapture test = new VideoCapture();
        cameraList = new ArrayList<>();
        for (int i = 0; i < 64; i++){
            test.open(i);
            if (test.isOpened()){
                cameraSourceBox.addItem(String.valueOf(i));
                cameraList.add(i);
            }
            test.release();
        }
        cameraSourceBox.setSelectedIndex(cameraSourceBox.getItemCount()-1);
        
        if (cameraList.size() > 0){
            webcam = new WebcamFeed(cameraList.get(cameraList.size()-1), this);
            webcamConnected = cameraList.get(cameraList.size()-1);
            webcam.open();
        } else {
            webcam = new WebcamFeed(this);
            warningLabel.setForeground(Color.red);
            warningLabel.setText("webcam haven't been connected!!!");
            messageReceiver.append("webcam haven't been connected!!!\n");
            messageReceiver.setCaretPosition(messageReceiver.getText().length());
        }
        
        thread = new Thread(webcam);
        thread.start();
        this.addPropertyChangeListener(new PropertyChangeListener(){
            @Override
            public void propertyChange(PropertyChangeEvent arg0) {
                if (arg0.getPropertyName().equals("image")){
                    changeImage(image);
                }
            }
        });
        
        //arduino device list
        arduinoSourceBox.removeAllItems();
        SerialPort[] list = SerialPort.getCommPorts();
        for (SerialPort temp : list){
            arduinoSourceBox.addItem(temp.getSystemPortName());
        }
        
        //set up for arduino communication with first device
        if (list.length != 0){
            arduCom = new ArduinoCommunication(list[0].getSystemPortName(), baudRate);
            arduCom.setTextArea(messageReceiver);
            arduCom.setWarnLabel(warningLabel);
            arduCom.setCheck();
            arduCom.startConnection();  
        } else {
            arduCom = new ArduinoCommunication();
            arduCom.setBaudRate(baudRate);
            arduCom.setTextArea(messageReceiver);
            arduCom.setWarnLabel(warningLabel);
            arduCom.setCheck();
            warningLabel.setForeground(Color.red);
            warningLabel.setText(warningLabel.getText() + "Arduino haven't been connected!!!");
            messageReceiver.append("Arduino haven't been connected!!!\n");
            messageReceiver.setCaretPosition(messageReceiver.getText().length());
        }
        
        sizeBox.removeAllItems();
        sizeBox.addItem("640x480");
        sizeBox.addItem("800x600");
    }
    
    public JPanel getCamPanel(){
        return camPanel;
    }
    
    public void changeImage(BufferedImage image){
        imgLabel.setIcon(new ImageIcon(image));
        camPanel.add(imgLabel);
        camPanel.repaint();
    }

    private void setValueHor(int valueHor) {
        if (valueHor >= 0 && valueHor <= 180) {
            this.valueHor = valueHor;
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
            leftButton.setEnabled(false);
            leftDoubleButton.setEnabled(false);
            rightButton.setEnabled(true);
            rightDoubleButton.setEnabled(true);
            centerButton.setEnabled(true);
                    
        } else if (valueHor > 180) {
            this.valueHor = 180;
            rightButton.setEnabled(false);
            rightDoubleButton.setEnabled(false);
            leftButton.setEnabled(true);
            leftDoubleButton.setEnabled(true);
            centerButton.setEnabled(true);
        }
        
        if (!invHorCheckBox.isSelected()){
            leftValueField.setText(String.valueOf(this.valueHor - 90));
            rightValueField.setText(String.valueOf(90 - this.valueHor));
        } else {
            leftValueField.setText(String.valueOf(90 - this.valueHor));
            rightValueField.setText(String.valueOf(this.valueHor - 90));
        }
        
        int temp, temp2;
        if (invHorCheckBox.isSelected()){
            temp = this.valueHor;
        } else{
            temp = 180 - this.valueHor;
        }
        
        if (invVerCheckBox.isSelected()){
            temp2 = 180 -this.valueVer;
        } else {
            temp2 = this.valueVer;
        }
        arduCom.serialWrite(temp+"&"+temp2+"&");
    }

    private void setValueVer(int valueVer) {
        if (valueVer >= 0 && valueVer <= 180) {
            this.valueVer = valueVer;
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
            upButton.setEnabled(true);
            upDoubleButton.setEnabled(true);
            downButton.setEnabled(false);
            downDoubleButton.setEnabled(false);
            centerButton.setEnabled(true);
        } else if (valueVer > 180) {
            this.valueVer = 180;
            upButton.setEnabled(false);
            upDoubleButton.setEnabled(false);
            downButton.setEnabled(true);
            downDoubleButton.setEnabled(true);
            centerButton.setEnabled(true);
        }
        
        if (!invVerCheckBox.isSelected()){
            upValueField.setText(String.valueOf(this.valueVer - 90));
            downValueField.setText(String.valueOf(90 - this.valueVer));
        } else {
            upValueField.setText(String.valueOf(90 - this.valueVer));
            downValueField.setText(String.valueOf(this.valueVer - 90));
        }
        
        int temp, temp2;
        if (invHorCheckBox.isSelected()){
            temp = this.valueHor;
        } else{
            temp = 180 - this.valueHor;
        }
        
        if (invVerCheckBox.isSelected()){
            temp2 = 180 - this.valueVer;
        } else {
            temp2 = this.valueVer;
        }
        arduCom.serialWrite(temp+"&"+temp2+"&");
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

        WindowTabPane = new javax.swing.JTabbedPane();
        mainWindowPanel = new javax.swing.JPanel();
        camPanel = new javax.swing.JPanel();
        functionalityTabPane = new javax.swing.JTabbedPane();
        movementPanel = new javax.swing.JPanel();
        movement = new javax.swing.JPanel();
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
        MessageBoard = new javax.swing.JPanel();
        warningLabel = new javax.swing.JLabel();
        recognitionPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        minHField = new javax.swing.JTextField();
        minSField = new javax.swing.JTextField();
        minVField = new javax.swing.JTextField();
        hsvRangeButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        maxHField = new javax.swing.JTextField();
        maxSField = new javax.swing.JTextField();
        maxVField = new javax.swing.JTextField();
        colorRecButton = new javax.swing.JButton();
        shapeRecButton = new javax.swing.JButton();
        shapeRecLabel = new javax.swing.JLabel();
        dilateField = new javax.swing.JTextField();
        erodeField = new javax.swing.JTextField();
        erodeLabel = new javax.swing.JLabel();
        dilateLabel = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        messagingPanel = new javax.swing.JPanel();
        message = new javax.swing.JTextField();
        buttonSend = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        messageReceiver = new javax.swing.JTextArea();
        jLabel11 = new javax.swing.JLabel();
        optionPanel = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        invVerCheckBox = new javax.swing.JCheckBox();
        invHorCheckBox = new javax.swing.JCheckBox();
        globalOptionsPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        confirmGlobal = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        cameraSourceBox = new javax.swing.JComboBox<>();
        arduinoSourceBox = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        sizeBox = new javax.swing.JComboBox<>();
        reloadButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        helpPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTextArea7 = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTextArea8 = new javax.swing.JTextArea();
        jScrollPane10 = new javax.swing.JScrollPane();
        jTextArea9 = new javax.swing.JTextArea();
        jScrollPane11 = new javax.swing.JScrollPane();
        jTextArea10 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        mainWindowPanel.setPreferredSize(new java.awt.Dimension(1000, 500));

        camPanel.setBorder(null);

        java.awt.GridBagLayout jPanel1Layout = new java.awt.GridBagLayout();
        jPanel1Layout.columnWidths = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        jPanel1Layout.rowHeights = new int[] {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0};
        movement.setLayout(jPanel1Layout);

        leftButton.setText("←");
        leftButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        movement.add(leftButton, gridBagConstraints);

        downButton.setText("↓");
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 8;
        movement.add(downButton, gridBagConstraints);

        upDoubleButton.setText("↑ ↑");
        upDoubleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upDoubleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        movement.add(upDoubleButton, gridBagConstraints);

        rightButton.setText("→");
        rightButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 6;
        movement.add(rightButton, gridBagConstraints);

        upButton.setText("↑");
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 4;
        movement.add(upButton, gridBagConstraints);

        rightValueField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        rightValueField.setText("0");
        rightValueField.setPreferredSize(new java.awt.Dimension(40, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 6;
        movement.add(rightValueField, gridBagConstraints);

        leftValueField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        leftValueField.setText("0");
        leftValueField.setMinimumSize(new java.awt.Dimension(30, 30));
        leftValueField.setPreferredSize(new java.awt.Dimension(40, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        movement.add(leftValueField, gridBagConstraints);

        downValueField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        downValueField.setText("0");
        downValueField.setPreferredSize(new java.awt.Dimension(40, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 12;
        movement.add(downValueField, gridBagConstraints);

        downDoubleButton.setText("↓ ↓");
        downDoubleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downDoubleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 10;
        movement.add(downDoubleButton, gridBagConstraints);

        leftDoubleButton.setText("← ←");
        leftDoubleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leftDoubleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        movement.add(leftDoubleButton, gridBagConstraints);

        rightDoubleButton.setText("→ →");
        rightDoubleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rightDoubleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 6;
        movement.add(rightDoubleButton, gridBagConstraints);

        centerButton.setText("+");
        centerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                centerButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 6;
        movement.add(centerButton, gridBagConstraints);

        upValueField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        upValueField.setText("0");
        upValueField.setPreferredSize(new java.awt.Dimension(40, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        movement.add(upValueField, gridBagConstraints);

        warningLabel.setText(" ");

        javax.swing.GroupLayout MessageBoardLayout = new javax.swing.GroupLayout(MessageBoard);
        MessageBoard.setLayout(MessageBoardLayout);
        MessageBoardLayout.setHorizontalGroup(
            MessageBoardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MessageBoardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(warningLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MessageBoardLayout.setVerticalGroup(
            MessageBoardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MessageBoardLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(warningLabel)
                .addContainerGap(133, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout movementPanelLayout = new javax.swing.GroupLayout(movementPanel);
        movementPanel.setLayout(movementPanelLayout);
        movementPanelLayout.setHorizontalGroup(
            movementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(movementPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(movementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(movement, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(MessageBoard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        movementPanelLayout.setVerticalGroup(
            movementPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, movementPanelLayout.createSequentialGroup()
                .addContainerGap(27, Short.MAX_VALUE)
                .addComponent(movement, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(MessageBoard, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        functionalityTabPane.addTab("Control", movementPanel);

        jLabel1.setText("HSV color recognition");

        jLabel4.setText("Hue");

        jLabel5.setText("Satu.");

        jLabel6.setText("Value");

        jLabel2.setText("min");

        minHField.setText("0");

        minSField.setText("0");

        minVField.setText("0");

        hsvRangeButton.setText("Change HSV");
        hsvRangeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hsvRangeButtonActionPerformed(evt);
            }
        });

        jLabel3.setText("max");

        maxHField.setText("80");

        maxSField.setText("255");

        maxVField.setText("255");

        colorRecButton.setText("Color recognition");
        colorRecButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorRecButtonActionPerformed(evt);
            }
        });

        shapeRecButton.setText("Shape recognition");
        shapeRecButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shapeRecButtonActionPerformed(evt);
            }
        });

        shapeRecLabel.setText("Shape recognition");
        shapeRecLabel.setEnabled(false);

        dilateField.setText("4");
        dilateField.setEnabled(false);

        erodeField.setText("10");
        erodeField.setEnabled(false);

        erodeLabel.setText("Erode");
        erodeLabel.setEnabled(false);

        dilateLabel.setText("Dilate");
        dilateLabel.setEnabled(false);

        javax.swing.GroupLayout recognitionPanelLayout = new javax.swing.GroupLayout(recognitionPanel);
        recognitionPanel.setLayout(recognitionPanelLayout);
        recognitionPanelLayout.setHorizontalGroup(
            recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recognitionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(recognitionPanelLayout.createSequentialGroup()
                        .addComponent(jSeparator2)
                        .addContainerGap())
                    .addGroup(recognitionPanelLayout.createSequentialGroup()
                        .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(recognitionPanelLayout.createSequentialGroup()
                                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(recognitionPanelLayout.createSequentialGroup()
                                        .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(minHField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel4))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(recognitionPanelLayout.createSequentialGroup()
                                                .addComponent(jLabel5)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jLabel6))
                                            .addGroup(recognitionPanelLayout.createSequentialGroup()
                                                .addComponent(minSField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(minVField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addGroup(recognitionPanelLayout.createSequentialGroup()
                                        .addComponent(maxHField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(maxSField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(maxVField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(hsvRangeButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addGroup(recognitionPanelLayout.createSequentialGroup()
                                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addGroup(recognitionPanelLayout.createSequentialGroup()
                                        .addComponent(colorRecButton)
                                        .addGap(18, 18, 18)
                                        .addComponent(shapeRecButton))
                                    .addComponent(shapeRecLabel)
                                    .addGroup(recognitionPanelLayout.createSequentialGroup()
                                        .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(erodeField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(erodeLabel))
                                        .addGap(18, 18, 18)
                                        .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(dilateLabel)
                                            .addComponent(dilateField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap(52, Short.MAX_VALUE))))
        );
        recognitionPanelLayout.setVerticalGroup(
            recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recognitionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(colorRecButton)
                    .addComponent(shapeRecButton))
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minHField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minSField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minVField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxHField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxSField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxVField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(hsvRangeButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(shapeRecLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(erodeLabel)
                    .addComponent(dilateLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dilateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(erodeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(201, Short.MAX_VALUE))
        );

        functionalityTabPane.addTab("Recognition", recognitionPanel);

        message.setPreferredSize(new java.awt.Dimension(200, 27));

        buttonSend.setText("Send message");
        buttonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSendActionPerformed(evt);
            }
        });

        messageReceiver.setEditable(false);
        messageReceiver.setColumns(20);
        messageReceiver.setRows(5);
        jScrollPane1.setViewportView(messageReceiver);

        jLabel11.setText("Messaging menu");

        javax.swing.GroupLayout messagingPanelLayout = new javax.swing.GroupLayout(messagingPanel);
        messagingPanel.setLayout(messagingPanelLayout);
        messagingPanelLayout.setHorizontalGroup(
            messagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(messagingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(messagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, messagingPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(message, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(messagingPanelLayout.createSequentialGroup()
                        .addGroup(messagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(buttonSend))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        messagingPanelLayout.setVerticalGroup(
            messagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(messagingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(message, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonSend)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(108, Short.MAX_VALUE))
        );

        functionalityTabPane.addTab("Arduino", messagingPanel);

        jLabel12.setText("Arduino control inversion");

        invVerCheckBox.setText("Invert verticaly");
        invVerCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invVerCheckBoxActionPerformed(evt);
            }
        });

        invHorCheckBox.setText("Invert horizontaly");
        invHorCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invHorCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout optionPanelLayout = new javax.swing.GroupLayout(optionPanel);
        optionPanel.setLayout(optionPanelLayout);
        optionPanelLayout.setHorizontalGroup(
            optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionPanelLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(optionPanelLayout.createSequentialGroup()
                        .addComponent(invHorCheckBox)
                        .addGap(18, 18, 18)
                        .addComponent(invVerCheckBox)
                        .addGap(0, 90, Short.MAX_VALUE))))
        );
        optionPanelLayout.setVerticalGroup(
            optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(invVerCheckBox)
                    .addComponent(invHorCheckBox))
                .addContainerGap(406, Short.MAX_VALUE))
        );

        functionalityTabPane.addTab("Options", optionPanel);

        javax.swing.GroupLayout mainWindowPanelLayout = new javax.swing.GroupLayout(mainWindowPanel);
        mainWindowPanel.setLayout(mainWindowPanelLayout);
        mainWindowPanelLayout.setHorizontalGroup(
            mainWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainWindowPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(camPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 640, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(functionalityTabPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainWindowPanelLayout.setVerticalGroup(
            mainWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainWindowPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(camPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(functionalityTabPane))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        WindowTabPane.addTab("Main", mainWindowPanel);

        jLabel7.setText("Camera selection");

        confirmGlobal.setText("Confirm");
        confirmGlobal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmGlobalActionPerformed(evt);
            }
        });

        jLabel8.setText("Arduino selection");

        cameraSourceBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        arduinoSourceBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel9.setText("Camera panel size");

        sizeBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        reloadButton.setText("Reload devices");
        reloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout globalOptionsPanelLayout = new javax.swing.GroupLayout(globalOptionsPanel);
        globalOptionsPanel.setLayout(globalOptionsPanelLayout);
        globalOptionsPanelLayout.setHorizontalGroup(
            globalOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(globalOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(globalOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(globalOptionsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 906, Short.MAX_VALUE))
                    .addGroup(globalOptionsPanelLayout.createSequentialGroup()
                        .addGroup(globalOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(sizeBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(confirmGlobal, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cameraSourceBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 300, Short.MAX_VALUE)
                            .addComponent(arduinoSourceBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(16, 16, 16)
                        .addComponent(reloadButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addComponent(jSeparator1)
        );
        globalOptionsPanelLayout.setVerticalGroup(
            globalOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(globalOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cameraSourceBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(globalOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(reloadButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(arduinoSourceBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sizeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 277, Short.MAX_VALUE)
                .addComponent(confirmGlobal)
                .addContainerGap())
        );

        WindowTabPane.addTab("Global options", globalOptionsPanel);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("\tRotation control\n\nControl tab of application contains few text fields and directional buttons which allow you to rotate camera using Arduino controller and robot mechanism. If Arduino controller is connected and user presses any of buttons, application creates a message that is sent to Arduino controller with a command which contrains your selected possition. \n\nIn that regard most similar style buttons and fields have similar use in robot control process by rotating robot mechanism 1, 10 or user-selected amount of degrees, while rotation range is limited to [0..180] horizontally and vertically. Each of the button groups are explained bellow with the actions they are assigned for:\n\n\tOne arrow button group ([ ← ] [ ↑ ] [ → ] [ ↓ ])\n\nThese buttons are assigned to rotate camera mechanism by 1 degree. In message for controller creation process that substracts or adds 1 degree for appropriate direction.\nex. from center possition pressing [ ↑ ] button will sent message to controller to set mechanism 90 degrees horizontally and 91 degress vertically.\n\n\tTwo arrow button group ([ ← ← ] [ ↑ ↑ ] [ → → ] [ ↓ ↓ ])\n\nThese buttons are assigned to rotate camera mechanism by 10 degrees. In message for controller creation process that substracts or adds 10 degrees for appropriate direction.\nex. from center possition pressing [ ↑ ↑ ] button will sent message to controller to set mechanism 90 degrees horizontally and 100 degress vertically.\n\n\tCenter button ( [ + ] )\n\nThis button is assigned to rotate camera mechanism back to the center possition. In message for controller creation process that sets wanted position as a default center possition (90 horizontally and 90 vertically).\nex. from any possition pressing [ + ] button will sent message to controller to set mechanism 90 degrees horizontally and 90 degress vertically.\n\n\tDirectional text fields\n\nEach direction has it's own text field which is made to allow user to rotate camera mechanism a desired amount in a range of [-90..90] to each direction. This range is different than sent to Arduino controller ( [0..180] ) to show that camera is rotated from center position. To rotate the mechanism user has to highlight wanted text field and entering a desired position and then confirm it by pressing 'Enter' key. If after entering desired possition user presses anywhere else on the screen, the field will be return to previous value and message won't be sent.\nex. from center possition if user will enter 10 in top field and press 'Enter' key, that will send message to controller to set mechanism 90 degrees horizontally and 100 degress vertically.\n\n\t!!!Warning!!!\n\nIf Arduino controller won't be connected when control buttons are pressed or control text field is confirmed, the application will attempt to send a message however it will be saves in buffer waiting for serial port recipient and that won't rotate the mechanism. In chance that Arduino controller would be accessible after pressing control buttons this message will be sent imediatly once serial port will be opened by a controller.\nIt is also suggested not to spam these buttons or rotation command messages. Because of limitations of serial port messages may be combined into a single message if done so and controller will report them as an error.");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setFocusable(false);
        jScrollPane2.setViewportView(jTextArea1);

        jTabbedPane1.addTab("Control", jScrollPane2);

        jTextArea7.setEditable(false);
        jTextArea7.setColumns(20);
        jTextArea7.setLineWrap(true);
        jTextArea7.setRows(5);
        jTextArea7.setText("\tVisual recognition systems\n\nApplication allows few object parameter recognition functions: color recognition and shape recognition.\n\n\tColor Recognition\n\nThis function allows user to detect specific colors in camera view. This is done by pressing [ Color recognition ] button which sets camera view into black and white mode. In this view by using white color are highlighted all colors that are within specified range. To change this range there are 6 text fields for each of minimum and maximum range values in HSV color system. After entering new range user should press [ change HSV ] button or press [ Color recognition ] button twise to turn off and turn back on this mode. The table with color ranges are shown in next tab [ Color range ]. If user wishes to exit this mode it can be done by pressing [ Color recognition ] button again.\n\n\tShape recognition\n\nThis function allows user to detect specific objects that are based around specific color. This is done by pressing [ Spahe recognition ] button which highlights with border the selected border of an object. In this user can change HSV color values to try finding different objects. Similarly to color recognition to change color range there are 6 text fields for each of minimum and maximum range values in HSV color system. After entering new range user should press [ change HSV ] button.\nAlso for precision reasons the system allows user to enter erosion and dilation values. These values are used to remove environment noise, avoid or includ smaller details of the object and isolate specific elements of an object. Dilation allows us to expand the image which adds a number of pixels to the boundaries of an object which allow us to find objects that may contain lines, hollows and other object features which may have split object by color. Erosion similar and also oposite to dilation allows us to increase the color effect of the object on surrounding pixels while minimising the effect environment colors do to the wanted object. These values are quite sensitive and depending on application use might require playing around with them for you prefference or more precise object recognition. It is recomended to use erosion values higher while keeping dilation values low for better object recognition.");
        jTextArea7.setWrapStyleWord(true);
        jTextArea7.setFocusable(false);
        jScrollPane8.setViewportView(jTextArea7);

        jTabbedPane1.addTab("Recognition", jScrollPane8);

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/color range.png"))); // NOI18N
        jScrollPane3.setViewportView(jLabel10);

        jTabbedPane1.addTab("Color range", jScrollPane3);

        jTextArea8.setEditable(false);
        jTextArea8.setColumns(20);
        jTextArea8.setLineWrap(true);
        jTextArea8.setRows(5);
        jTextArea8.setText("\tArduino messaging menu\n\nThis menu allows user to write direct commands or messages to Arduino controller. Such commands allow to use functionality which is present in Arduino controller but doesn't have specified commands within this application's user interface.\n\nTo write messages user has to enter them into the text field and press [ Send message ] button bellow. The response from the controller will be displayed in a text box bellow.\n\nCurrently available commands\n\"0..180\"&\"0..180\"& - rotation command that allows to rotate robot mechanism by user selected amount. In order to use this command \"0..180\" in this example must be replaced with value from 0 to 180 that represent horizontal and vertical position of the camera.\nHello Arduino! - a command mostly used for testing that returns \"Hello I'm Arduino!\" from the controller if controller open in serial port.\n\n\t!!!Warning!!!\n\nIf Arduino controller won't be connected when control buttons are pressed or control text field is confirmed, the application will attempt to send a message however it will be saves in buffer waiting for serial port recipient and that won't rotate the mechanism. In chance that Arduino controller would be accessible after pressing control buttons this message will be sent imediatly once serial port will be opened by a controller.\nIt is also suggested not to spam these buttons or rotation command messages. Because of limitations of serial port messages may be combined into a single message if done so and controller will report them as an error.");
        jTextArea8.setWrapStyleWord(true);
        jTextArea8.setFocusable(false);
        jScrollPane9.setViewportView(jTextArea8);

        jTabbedPane1.addTab("Arduino", jScrollPane9);

        jTextArea9.setEditable(false);
        jTextArea9.setColumns(20);
        jTextArea9.setLineWrap(true);
        jTextArea9.setRows(5);
        jTextArea9.setText("\tFunction options\n\nThese options allow to change minor details of application that do not affect application much or require application to be restarted to use them. Bellow are explained each of available options:\n\n\tControll inversion\n\nThese options allow to invert horizontal and vertical robot mechanism controlls in user interface. These options are for user convenience and if user would choose to send direct command using arduino communication menu, the command values wont be inverted.");
        jTextArea9.setWrapStyleWord(true);
        jTextArea9.setFocusable(false);
        jScrollPane10.setViewportView(jTextArea9);

        jTabbedPane1.addTab("Options", jScrollPane10);

        jTextArea10.setEditable(false);
        jTextArea10.setColumns(20);
        jTextArea10.setLineWrap(true);
        jTextArea10.setRows(5);
        jTextArea10.setText("\tGlobal options\n\nThese options allow user to set up specific options which affect most or whole application. Also these changes require restarting some of this application's functionality so they might take some time to finish their process. Bellow are explained each of available global options:\n\n\tCamera selection\n\nThis option allows user to select which camera is used as main robot camera and shown in camera view panel in main window. The list is generated automatically using OpenCV library, functions for device accessibility and because of that if camera doesn't show up in the list it might be required to restart this application, camera might be lacking required drivers or this application isn't allowed to access wanted device.\n\n\tArduino selection\n\nThis option allows user to select which Arduino controller is used for robot mechanism and communication between this aplication and the controller. The list is generated automatically using Fazecast jSerialComm library, functions for device accessibility and because of that if controller doesn't show up in the list, it might be required to restart this application, camera might be lacking required drivers or this application isn't allowed to access wanted device\n\n\tUser interface camera panel size\n\nThis is mostly user convenience option which allows user to change camera view panel as well as whole application windows size. This does not affect application buttons, text size or other user interface options and mostly just helps to better see robot camera view.");
        jTextArea10.setWrapStyleWord(true);
        jTextArea10.setFocusable(false);
        jScrollPane11.setViewportView(jTextArea10);

        jTabbedPane1.addTab("Global options", jScrollPane11);

        javax.swing.GroupLayout helpPanelLayout = new javax.swing.GroupLayout(helpPanel);
        helpPanel.setLayout(helpPanelLayout);
        helpPanelLayout.setHorizontalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1018, Short.MAX_VALUE)
        );
        helpPanelLayout.setVerticalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
        );

        WindowTabPane.addTab("Help", helpPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(WindowTabPane)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(WindowTabPane)
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
        upValueField.setText(String.valueOf(0));
        downValueField.setText(String.valueOf(0));
        leftValueField.setText(String.valueOf(0));
        rightValueField.setText(String.valueOf(0));
        //System.out.println(valueHor + " " + valueVer);
    }//GEN-LAST:event_centerButtonActionPerformed

    private void buttonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSendActionPerformed
        arduCom.serialWrite(message.getText());
        message.setText("");
    }//GEN-LAST:event_buttonSendActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        arduCom.serialWrite("90&90&");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        }
        arduCom.stopConnection();
        webcam.release();
    }//GEN-LAST:event_formWindowClosing

    private void colorRecButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorRecButtonActionPerformed
        hsv[0] = Integer.parseInt(minHField.getText());
        hsv[1] = Integer.parseInt(minSField.getText());
        hsv[2] = Integer.parseInt(minVField.getText());
        hsv[3] = Integer.parseInt(maxHField.getText());
        hsv[4] = Integer.parseInt(maxSField.getText());
        hsv[5] = Integer.parseInt(maxVField.getText());
        switch (webcam.getColorRecognition()) {
            case 0:
                webcam.setColorRecognition(1);
                break;
            case 1:
                webcam.setColorRecognition(0);
                break;
            case 2:
                webcam.setColorRecognition(1);
                shapeRecLabel.setEnabled(false);
                erodeLabel.setEnabled(false);
                dilateLabel.setEnabled(false);
                erodeField.setEnabled(false);
                dilateField.setEnabled(false);
                break;
            default:
                webcam.setColorRecognition(0);
                messageReceiver.append("Error!! something is wrong with modes!\n");
                break;
        } 
    }//GEN-LAST:event_colorRecButtonActionPerformed

    private void hsvRangeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hsvRangeButtonActionPerformed
        hsv[0] = Integer.parseInt(minHField.getText());
        hsv[1] = Integer.parseInt(minSField.getText());
        hsv[2] = Integer.parseInt(minVField.getText());
        hsv[3] = Integer.parseInt(maxHField.getText());
        hsv[4] = Integer.parseInt(maxSField.getText());
        hsv[5] = Integer.parseInt(maxVField.getText());
    }//GEN-LAST:event_hsvRangeButtonActionPerformed

    private void invHorCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invHorCheckBoxActionPerformed
        if (!invHorCheckBox.isSelected()){
            leftValueField.setText(String.valueOf(this.valueHor - 90));
            rightValueField.setText(String.valueOf(90 - this.valueHor));
        } else {
            leftValueField.setText(String.valueOf(90 - this.valueHor));
            rightValueField.setText(String.valueOf(this.valueHor - 90));
        }
    }//GEN-LAST:event_invHorCheckBoxActionPerformed

    private void invVerCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invVerCheckBoxActionPerformed
        if (!invVerCheckBox.isSelected()){
            upValueField.setText(String.valueOf(this.valueVer - 90));
            downValueField.setText(String.valueOf(90 - this.valueVer));
        } else {
            upValueField.setText(String.valueOf(90 - this.valueVer));
            downValueField.setText(String.valueOf(this.valueVer - 90));
        }
    }//GEN-LAST:event_invVerCheckBoxActionPerformed

    private void reloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadButtonActionPerformed
        //refresh all available arduino devices
        arduinoSourceBox.removeAllItems();
        SerialPort[] list = SerialPort.getCommPorts();
        for (SerialPort temp : list){
            arduinoSourceBox.addItem(temp.getSystemPortName());
        }
        
        //check all available cameras
        cameraSourceBox.removeAllItems();
        cameraList = new ArrayList<>();
        VideoCapture test = new VideoCapture();
        for (int i = 0; i < 64; i++){
            test.open(i);
            if (test.isOpened()){
                cameraSourceBox.addItem(String.valueOf(i));
                cameraList.add(i);
            }
            test.release();
        }
        if (webcamConnected != -10){
            cameraSourceBox.addItem(String.valueOf(webcamConnected));
            cameraList.add(webcamConnected);
            cameraSourceBox.setSelectedIndex(cameraSourceBox.getItemCount()-1);
            
        }
    }//GEN-LAST:event_reloadButtonActionPerformed

    private void confirmGlobalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_confirmGlobalActionPerformed
        //stop old connection and start new Arduino connection
        try {
            arduCom.stopConnection();
            arduCom.setUsbPort(arduinoSourceBox.getSelectedItem().toString());
            arduCom.startConnection();
        } catch (InterruptedException ex) {
            Logger.getLogger(Interface.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if (webcam.isOpen()){
            thread.stop();
            webcam.release();
            webcam.setWebcamPort(cameraList.get(cameraSourceBox.getSelectedIndex()));
            webcamConnected = cameraList.get(cameraSourceBox.getSelectedIndex());
            webcam.open();
            thread = new Thread(webcam);
            thread.start();
        }
        
        
    }//GEN-LAST:event_confirmGlobalActionPerformed

    private void shapeRecButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shapeRecButtonActionPerformed
        hsv[0] = Integer.parseInt(minHField.getText());
        hsv[1] = Integer.parseInt(minSField.getText());
        hsv[2] = Integer.parseInt(minVField.getText());
        hsv[3] = Integer.parseInt(maxHField.getText());
        hsv[4] = Integer.parseInt(maxSField.getText());
        hsv[5] = Integer.parseInt(maxVField.getText());
        switch (webcam.getColorRecognition()) {
            case 0:
            case 1:
                webcam.setColorRecognition(2);
                shapeRecLabel.setEnabled(true);
                erodeLabel.setEnabled(true);
                dilateLabel.setEnabled(true);
                erodeField.setEnabled(true);
                dilateField.setEnabled(true);
                break;
            case 2:
                webcam.setColorRecognition(0);
                shapeRecLabel.setEnabled(false);
                erodeLabel.setEnabled(false);
                dilateLabel.setEnabled(false);
                erodeField.setEnabled(false);
                dilateField.setEnabled(false);
                break;
            default:
                webcam.setColorRecognition(0);
                messageReceiver.append("Error!! something is wrong with modes!\n");
                break;
        }
    }//GEN-LAST:event_shapeRecButtonActionPerformed

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
    private javax.swing.JPanel MessageBoard;
    private javax.swing.JTabbedPane WindowTabPane;
    private javax.swing.JComboBox<String> arduinoSourceBox;
    private javax.swing.JButton buttonSend;
    private javax.swing.JPanel camPanel;
    private javax.swing.JComboBox<String> cameraSourceBox;
    private javax.swing.JButton centerButton;
    private javax.swing.JButton colorRecButton;
    private javax.swing.JButton confirmGlobal;
    private javax.swing.JTextField dilateField;
    private javax.swing.JLabel dilateLabel;
    private javax.swing.JButton downButton;
    private javax.swing.JButton downDoubleButton;
    private javax.swing.JTextField downValueField;
    private javax.swing.JTextField erodeField;
    private javax.swing.JLabel erodeLabel;
    private javax.swing.JTabbedPane functionalityTabPane;
    private javax.swing.JPanel globalOptionsPanel;
    private javax.swing.JPanel helpPanel;
    private javax.swing.JButton hsvRangeButton;
    private javax.swing.JCheckBox invHorCheckBox;
    private javax.swing.JCheckBox invVerCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea10;
    private javax.swing.JTextArea jTextArea7;
    private javax.swing.JTextArea jTextArea8;
    private javax.swing.JTextArea jTextArea9;
    private javax.swing.JButton leftButton;
    private javax.swing.JButton leftDoubleButton;
    private javax.swing.JTextField leftValueField;
    private javax.swing.JPanel mainWindowPanel;
    private javax.swing.JTextField maxHField;
    private javax.swing.JTextField maxSField;
    private javax.swing.JTextField maxVField;
    private javax.swing.JTextField message;
    private javax.swing.JTextArea messageReceiver;
    private javax.swing.JPanel messagingPanel;
    private javax.swing.JTextField minHField;
    private javax.swing.JTextField minSField;
    private javax.swing.JTextField minVField;
    private javax.swing.JPanel movement;
    private javax.swing.JPanel movementPanel;
    private javax.swing.JPanel optionPanel;
    private javax.swing.JPanel recognitionPanel;
    private javax.swing.JButton reloadButton;
    private javax.swing.JButton rightButton;
    private javax.swing.JButton rightDoubleButton;
    private javax.swing.JTextField rightValueField;
    private javax.swing.JButton shapeRecButton;
    private javax.swing.JLabel shapeRecLabel;
    private javax.swing.JComboBox<String> sizeBox;
    private javax.swing.JButton upButton;
    private javax.swing.JButton upDoubleButton;
    private javax.swing.JTextField upValueField;
    private javax.swing.JLabel warningLabel;
    // End of variables declaration//GEN-END:variables

}

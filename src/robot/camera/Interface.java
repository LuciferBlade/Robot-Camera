package robot.camera;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.opencv.core.Core;

/**
 *
 * @author blade
 */
public class Interface extends javax.swing.JFrame{

    private int valueHor = 90;
    private int valueVer = 90;
    
    ArduinoCommunication arduCom;
    String arduinoPort = "/dev/ttyUSB0";
    int baudRate = 9600;

    String webcamPort = "/dev/video2";
    
    JLabel imgLabel = new JLabel();
    BufferedImage image = null;
    
    WebcamFeed webcam;
    Thread thread;
    
    int hsv[] = new int[6];
    
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
        
        webcam = new WebcamFeed(webcamPort, this);
        webcam.open();
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
        
        arduCom = new ArduinoCommunication(arduinoPort, baudRate);
        arduCom.setTextArea(messageReceiver);
        arduCom.setWarnLabel(warningLabel);
        arduCom.setCheck();
        arduCom.startConnection();
        
        sizeBox.removeAllItems();
        sizeBox.addItem("640x480");
        
        cameraSourceBox.removeAllItems();
        cameraSourceBox.addItem("2");
        arduinoSourceBox.removeAllItems();
        arduinoSourceBox.addItem(arduCom.getPortDescription());
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
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        maxHField = new javax.swing.JTextField();
        maxSField = new javax.swing.JTextField();
        maxVField = new javax.swing.JTextField();
        colorRecButton = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        minRGB_RField = new javax.swing.JTextField();
        minRGB_GField = new javax.swing.JTextField();
        minRGB_BField = new javax.swing.JTextField();
        minRGB_HField = new javax.swing.JTextField();
        minRGB_SField = new javax.swing.JTextField();
        maxRGB_RField = new javax.swing.JTextField();
        maxRGB_GField = new javax.swing.JTextField();
        maxRGB_BField = new javax.swing.JTextField();
        maxRGB_HField = new javax.swing.JTextField();
        maxRGB_SField = new javax.swing.JTextField();
        colorRecButton1 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        messagingPanel = new javax.swing.JPanel();
        message = new javax.swing.JTextField();
        buttonSend = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        messageReceiver = new javax.swing.JTextArea();
        optionPanel = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        invHorCheckBox = new javax.swing.JCheckBox();
        invVerCheckBox = new javax.swing.JCheckBox();
        sizeBox = new javax.swing.JComboBox<>();
        setSizeButton = new javax.swing.JButton();
        globalOptionsPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        cameraSourceBox = new javax.swing.JComboBox<>();
        arduinoSourceBox = new javax.swing.JComboBox<>();
        helpPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

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

        jButton1.setText("changeHSV");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("max");

        maxHField.setText("80");

        maxSField.setText("255");

        maxVField.setText("255");

        colorRecButton.setText("colorRec");
        colorRecButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorRecButtonActionPerformed(evt);
            }
        });

        jLabel13.setText("RGB color recognition WIP");

        jLabel14.setText("Red");

        jLabel15.setText("max");

        jLabel16.setText("min");

        jLabel17.setText("Green");

        jLabel18.setText("Blue");

        jLabel19.setText("Hue");

        jLabel20.setText("Satu.");

        minRGB_RField.setText("0");
        minRGB_RField.setEnabled(false);

        minRGB_GField.setText("0");
        minRGB_GField.setEnabled(false);

        minRGB_BField.setText("0");
        minRGB_BField.setEnabled(false);

        minRGB_HField.setText("0");
        minRGB_HField.setEnabled(false);

        minRGB_SField.setText("0");
        minRGB_SField.setEnabled(false);

        maxRGB_RField.setText("0");
        maxRGB_RField.setEnabled(false);

        maxRGB_GField.setText("0");
        maxRGB_GField.setEnabled(false);

        maxRGB_BField.setText("0");
        maxRGB_BField.setEnabled(false);

        maxRGB_HField.setText("0");
        maxRGB_HField.setEnabled(false);

        maxRGB_SField.setText("0");
        maxRGB_SField.setEnabled(false);

        colorRecButton1.setText("colorRec");
        colorRecButton1.setEnabled(false);
        colorRecButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorRecButton1ActionPerformed(evt);
            }
        });

        jLabel10.setText("Shape recognition WIP super buggy");

        jButton3.setText("Enable");
        jButton3.setEnabled(false);

        javax.swing.GroupLayout recognitionPanelLayout = new javax.swing.GroupLayout(recognitionPanel);
        recognitionPanel.setLayout(recognitionPanelLayout);
        recognitionPanelLayout.setHorizontalGroup(
            recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recognitionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addGroup(recognitionPanelLayout.createSequentialGroup()
                        .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(recognitionPanelLayout.createSequentialGroup()
                                .addComponent(maxHField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(maxSField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(maxVField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                                        .addComponent(minVField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(26, 26, 26)
                                        .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(colorRecButton)
                                            .addComponent(jButton1)))))))
                    .addComponent(jLabel13)
                    .addGroup(recognitionPanelLayout.createSequentialGroup()
                        .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(recognitionPanelLayout.createSequentialGroup()
                                .addComponent(jLabel16)
                                .addGap(18, 18, 18)
                                .addComponent(minRGB_RField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(minRGB_GField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(minRGB_BField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(minRGB_HField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(recognitionPanelLayout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(jLabel14)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel17)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel18)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel19)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(minRGB_SField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(recognitionPanelLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addGap(15, 15, 15)
                        .addComponent(maxRGB_RField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxRGB_GField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxRGB_BField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxRGB_HField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maxRGB_SField, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(colorRecButton1)
                    .addComponent(jLabel10)
                    .addComponent(jButton3))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        recognitionPanelLayout.setVerticalGroup(
            recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(recognitionPanelLayout.createSequentialGroup()
                .addContainerGap()
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
                    .addComponent(jLabel2)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxHField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxSField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxVField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(colorRecButton))
                .addGap(18, 18, 18)
                .addComponent(jLabel13)
                .addGap(18, 18, 18)
                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(jLabel17)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(minRGB_RField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minRGB_GField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minRGB_BField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minRGB_HField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minRGB_SField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(recognitionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maxRGB_RField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxRGB_GField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxRGB_BField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxRGB_HField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maxRGB_SField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(colorRecButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addContainerGap(124, Short.MAX_VALUE))
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

        javax.swing.GroupLayout messagingPanelLayout = new javax.swing.GroupLayout(messagingPanel);
        messagingPanel.setLayout(messagingPanelLayout);
        messagingPanelLayout.setHorizontalGroup(
            messagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(messagingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(messagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(messagingPanelLayout.createSequentialGroup()
                        .addGroup(messagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(message, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(buttonSend))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        messagingPanelLayout.setVerticalGroup(
            messagingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(messagingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(message, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonSend)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(268, Short.MAX_VALUE))
        );

        functionalityTabPane.addTab("Arduino", messagingPanel);

        jLabel9.setText("video size");

        jLabel12.setText("Arduino control inversion");

        invHorCheckBox.setText("Invert horizontaly");

        invVerCheckBox.setText("Invert verticaly");

        sizeBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        setSizeButton.setText("Set");

        javax.swing.GroupLayout optionPanelLayout = new javax.swing.GroupLayout(optionPanel);
        optionPanel.setLayout(optionPanelLayout);
        optionPanelLayout.setHorizontalGroup(
            optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(optionPanelLayout.createSequentialGroup()
                        .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel12))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(optionPanelLayout.createSequentialGroup()
                        .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(sizeBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(optionPanelLayout.createSequentialGroup()
                                .addComponent(invHorCheckBox)
                                .addGap(18, 18, 18)
                                .addComponent(invVerCheckBox)))
                        .addGap(18, 18, 18)
                        .addComponent(setSizeButton)
                        .addGap(0, 23, Short.MAX_VALUE))))
        );
        optionPanelLayout.setVerticalGroup(
            optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(optionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sizeBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(setSizeButton))
                .addGap(19, 19, 19)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(optionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(invHorCheckBox)
                    .addComponent(invVerCheckBox))
                .addContainerGap(339, Short.MAX_VALUE))
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
                .addGroup(mainWindowPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(functionalityTabPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(camPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 480, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        WindowTabPane.addTab("Main", mainWindowPanel);

        jLabel7.setText("Camera");

        jButton2.setText("Confirm");

        jLabel8.setText("Arduino");

        cameraSourceBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        arduinoSourceBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout globalOptionsPanelLayout = new javax.swing.GroupLayout(globalOptionsPanel);
        globalOptionsPanel.setLayout(globalOptionsPanelLayout);
        globalOptionsPanelLayout.setHorizontalGroup(
            globalOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(globalOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(globalOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel7)
                    .addComponent(jButton2)
                    .addComponent(jLabel8)
                    .addComponent(cameraSourceBox, 0, 300, Short.MAX_VALUE)
                    .addComponent(arduinoSourceBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(714, Short.MAX_VALUE))
        );
        globalOptionsPanelLayout.setVerticalGroup(
            globalOptionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(globalOptionsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cameraSourceBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(arduinoSourceBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 347, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addContainerGap())
        );

        WindowTabPane.addTab("Global options", globalOptionsPanel);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("This is WIP help screen\n\nRotation controls menu\n\nIn control menu by pressing directional \">\" and \">>\" buttons it is possible to rotate camera by 1 or 10 degrees. It is also possible to rotate by custom amount of degrees by entering wanted value from -90 to 90 and pressing enter. If value is above or bellow this range, it will be changed back to previous value upon user losing focus of the field.\n\nArduino messaging menu\n\nIn this menu we can send messages to the Arduino controller by typing them in the field and pressing \"send\" button. If message isn't defined in Arduino controller it will be sent back as an error.\nCurrently supported messages: Hello Arduino! ; \\d&\\d& (\\d defines number from 0 to 180)");
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setFocusable(false);
        jScrollPane2.setViewportView(jTextArea1);

        javax.swing.GroupLayout helpPanelLayout = new javax.swing.GroupLayout(helpPanel);
        helpPanel.setLayout(helpPanelLayout);
        helpPanelLayout.setHorizontalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(helpPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1008, Short.MAX_VALUE)
                .addContainerGap())
        );
        helpPanelLayout.setVerticalGroup(
            helpPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(helpPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 486, Short.MAX_VALUE))
        );

        WindowTabPane.addTab("Help", helpPanel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(WindowTabPane)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(WindowTabPane)
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
            Thread.sleep(200);
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
        webcam.setColorRecognition();
    }//GEN-LAST:event_colorRecButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        hsv[0] = Integer.parseInt(minHField.getText());
        hsv[1] = Integer.parseInt(minSField.getText());
        hsv[2] = Integer.parseInt(minVField.getText());
        hsv[3] = Integer.parseInt(maxHField.getText());
        hsv[4] = Integer.parseInt(maxSField.getText());
        hsv[5] = Integer.parseInt(maxVField.getText());
    }//GEN-LAST:event_jButton1ActionPerformed

    private void colorRecButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorRecButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_colorRecButton1ActionPerformed

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
    private javax.swing.JButton colorRecButton1;
    private javax.swing.JButton downButton;
    private javax.swing.JButton downDoubleButton;
    private javax.swing.JTextField downValueField;
    private javax.swing.JTabbedPane functionalityTabPane;
    private javax.swing.JPanel globalOptionsPanel;
    private javax.swing.JPanel helpPanel;
    private javax.swing.JCheckBox invHorCheckBox;
    private javax.swing.JCheckBox invVerCheckBox;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JButton leftButton;
    private javax.swing.JButton leftDoubleButton;
    private javax.swing.JTextField leftValueField;
    private javax.swing.JPanel mainWindowPanel;
    private javax.swing.JTextField maxHField;
    private javax.swing.JTextField maxRGB_BField;
    private javax.swing.JTextField maxRGB_GField;
    private javax.swing.JTextField maxRGB_HField;
    private javax.swing.JTextField maxRGB_RField;
    private javax.swing.JTextField maxRGB_SField;
    private javax.swing.JTextField maxSField;
    private javax.swing.JTextField maxVField;
    private javax.swing.JTextField message;
    private javax.swing.JTextArea messageReceiver;
    private javax.swing.JPanel messagingPanel;
    private javax.swing.JTextField minHField;
    private javax.swing.JTextField minRGB_BField;
    private javax.swing.JTextField minRGB_GField;
    private javax.swing.JTextField minRGB_HField;
    private javax.swing.JTextField minRGB_RField;
    private javax.swing.JTextField minRGB_SField;
    private javax.swing.JTextField minSField;
    private javax.swing.JTextField minVField;
    private javax.swing.JPanel movement;
    private javax.swing.JPanel movementPanel;
    private javax.swing.JPanel optionPanel;
    private javax.swing.JPanel recognitionPanel;
    private javax.swing.JButton rightButton;
    private javax.swing.JButton rightDoubleButton;
    private javax.swing.JTextField rightValueField;
    private javax.swing.JButton setSizeButton;
    private javax.swing.JComboBox<String> sizeBox;
    private javax.swing.JButton upButton;
    private javax.swing.JButton upDoubleButton;
    private javax.swing.JTextField upValueField;
    private javax.swing.JLabel warningLabel;
    // End of variables declaration//GEN-END:variables

}

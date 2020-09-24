# Robot Camera

Main project idea is to create object recognition system which gets images from webcam and then translates it into understandable by user data (pictures, object location, etc). To have a higher control over webcam application also has access to Arduino Mega controller which using servo motors allows to control the rotation of simplified robot which holds webcam.

## File/folder description

- movement_control
contains all the necessary files, ready to upload to arduino uno/mega to initiate servo control and communication over Serial. In file it is possible to change global variables to select different connection pins for different servo.
- src/robot/camera
folder contains all the project classes for the java program, that is run to get video feed, recognize objects or control servos
#### Following files are in src/robot/camera
- ArduinoCommunication.java

class made to communicate with arduino and pass on messages between GUI and arduino
- Interface.java

GUI class for GUI implementation and various GUI component logic
- RobotCamera.java

Runable class for testing. Will be deleted in future
- WebcamFeed.java

# Robot Camera

A project to create object recognition using webcam and code to control a robot head with servo motors using Arduino.

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
- WebcamFeed
class which gathers webcam feed as images and sets required panel with webcam images, as if it was video stream. Also contains method to recognize colors (method might be removed or moved in the future)
- ArduinoInterpreter.java
  WORK IN PROGRESS!!!
- ObjectRecognition.java
  WORK IN PROGRESS!!!

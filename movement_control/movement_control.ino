#include <Servo.h>

//Horizontal and vertical servos
Servo servHor;
Servo servVer;

// pins to which servos are connected
const int servoHor = 2;
const int servoVer = 3;

//initial position
// !!! be careful if construction is fragile (would be good to know where servo is at before it is allowed to be set)
int pos1 = 90;
int pos2 = 90;

void setup() { // setting up  initial positions of servos
  servHor.attach(servoHor);
  moveTo(servHor, pos1, pos1, 5);

  servVer.attach(servoVer);
  moveTo(servVer, pos2, pos1, 5);

  // starting serial
  Serial.begin(9600);
}

void loop() {
  String testing = "";

  //if serial is available...
  if (Serial.available() == 0) {

    //read message from Serial
    String message = Serial.readStringUntil('\n');

    if (message == "Hello Arduino!") {
      testing = "Hello I'm Arduino!";
    }
    else {
      //conver message to char array for further use
      char convMessage[message.length() + 1];
      message.toCharArray(convMessage, message.length() + 1);

      //creating variables for: first control number, second control number, which number it is, error detection
      String firstPart = "";
      String secondPart = "";
      int part = 0;
      int fault = 0;

      //cycle through all the characters of message
      for (int i = 0; i < message.length(); i++) {

        //if character is digit and it's of first number
        if (isDigit(convMessage[i]) && part == 0) {
          firstPart = firstPart + convMessage[i];
        }

        //if character is digit and it's of second number
        else if (isDigit(convMessage[i]) && part == 1) {
          secondPart = secondPart + convMessage[i];
        }

        //if character is separator
        else if (convMessage[i] == '&') {
          part = part + 1;

          //if there were too many separators - error
          if (part > 2) {
            fault = 10;
            break;
          }
        }

        //if there was any other character appart new line or previous cases - error
        else {
          fault = 10;
          break;
        }
      }

      // if there wasn't an error, message wasn't empty (no message) and there were exacly 2 control numbers
      if (fault != 10 && message != "" && part == 2) {

        //output is made ready
        testing = firstPart + " " + secondPart;

        //create int to control Horizontal servo
        int temp = firstPart.toInt();

        //if number is within servo bounds
        if (temp >= 0 && temp <= 180) {
          //move servo
          moveTo(servHor, pos1, temp, 5);

          //create int to control Vertical servo
          temp = secondPart.toInt();

          //if number is within servo bounds
          if (temp >= 0 && temp <= 180) {
            //move servo
            moveTo(servVer, pos2, temp, 5);

          } else { //if Vertical servo number was out of bounds
            testing = "Number not right! (" + firstPart + " " + secondPart + ")";
          }
        }
        else {//if Horizontal servo number was out of bounds
          testing = "Number not right! (" + firstPart + " " + secondPart + ")";
        }

      } //if there was message but it was an error or there were more than 2 parts of the message (2 control numbers) - ERROR
      else if (message != "") {
        testing = "ERROR!";
      }
    }
  }

  //if response was created - print it back to Serial
  if (testing != "") {
    Serial.println(testing);
  }
}

//method to control servo movement with specified speed
void moveTo (Servo servo, int position, int destination, int speed) {
  int pos;
  int mapSpeed = map(speed, 0, 30, 30, 0);
  if (position < destination) {
    for (pos = position; pos <= destination; pos += 1) {
      servo.write(pos);
      pos1 = pos;
      delay(mapSpeed);
    }
  } else {
    for (pos = position; pos >= destination; pos -= 1) {
      servo.write(pos);
      pos2 = pos;
      delay(mapSpeed);
    }
  }
}

/**
 * Serial Duplex 
 * by Tom Igoe. 
 * 
 * Sends a byte out the serial port when you type a key
 * listens for bytes received, and displays their value. 
 * This is just a quick application for testing serial data
 * in both directions. 
 */


import processing.serial.*;

Serial serial;      // The serial port
int whichKey = -1;  // Variable to hold keystoke values
int inByte = -1;    // Incoming serial data

void setup() {
  size(400, 300);

  String portName = "COM5";
  serial = new Serial(this, portName, 115200);
  
  frameRate(40);
}

int cnt =0;
void draw() {
  println("send data");
  int current=0;
  for (int i=0; i<2; i++) {
    serial.write(doProtocol(new int[170], current++, 4));
    serial.write(doProtocol(new int[85],  current++, 4));
  }
  println(cnt+" done, "+frameRate);
  cnt++;

  while (serial.available() > 0) {
    println(serial.readString());
  } 
}




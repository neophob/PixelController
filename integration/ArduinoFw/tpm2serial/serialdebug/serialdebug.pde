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
  // create a font with the third font available to the system:

  // I know that the first port in the serial list on my mac
  // is always my  FTDI adaptor, so I open Serial.list()[0].
  // In Windows, this usually opens COM1.
  // Open whatever port is the one you're using.
  String portName = "COM5";
  serial = new Serial(this, portName, 115200);
  
  frameRate(40);
}

int cnt =0;
void draw() {
  println("send data");
  for (int i=0; i<2; i++) {
    serial.write(doProtocol(new int[170], i, 4));
    serial.write(doProtocol(new int[85], i+1, 4));
  }
  println(cnt+" done, "+frameRate);
  cnt++;
  //serial.flushAll();

  while (serial.available() > 0) {
    println(serial.readString());
  } 
}




/*
 * PixelInvaders tpm2.net implementation, Copyright (C) 2013 michael vogt <michu@neophob.com>
 * 
 * If you like this, make sure you check out http://www.pixelinvaders.ch
 *
 * This file is part of PixelController.
 *
 * PixelController is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * PixelController is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * 	
 *
 *
 */

#include <SPI.h>         
#include <Ethernet.h>
#include <EthernetUdp.h>  

//get the lib here: https://github.com/neophob/WS2801-Library
#include <WS2801.h>

//define some tpm constants
#define TPM2NET_LISTENING_PORT 65506
#define TPM2NET_HEADER_SIZE 5
#define TPM2NET_HEADER_IDENT 0x9c
#define TPM2NET_CMD_DATAFRAME 0xda
#define TPM2NET_CMD_COMMAND 0xc0
#define TPM2NET_CMD_ANSWER 0xaa
#define TPM2NET_FOOTER_IDENT 0x36

#define NR_OF_PANELS 1
#define PIXELS_PER_PANEL 64

//3 byte per pixel or 24bit (RGB)
#define BPP 3

//package size we expect. the footer byte is not included here!
#define EXPECTED_PACKED_SIZE (PIXELS_PER_PANEL*BPP+TPM2NET_HEADER_SIZE)

//use softspi as the spi line is used by the ethernet lib
#define DATA_PIN 3
#define CLOCK_PIN 2

//as the arduino ethernet has only 2kb ram
//we must limit the maximal udp packet size
//a 64 pixel matrix needs 192 bytes data
#define UDP_PACKET_SIZE 512


//some santiy checks here
#if EXPECTED_PACKED_SIZE > UDP_PACKET_SIZE
#error EXPECTED PACKED SIZE is bigger than UDP BUFFER! increase the buffer
#endif

//#if UDP_TX_PACKET_MAX_SIZE < EXPECTED_PACKED_SIZE
//#error UDP packet size to small - modify UDP_TX_PACKET_MAX_SIZE in the file EthernetUdp.h and set buffers to 64 bytes 
//#endif


// buffers for receiving and sending data
uint8_t packetBuffer[UDP_PACKET_SIZE]; //buffer to hold incoming packet,

//initialize pixels
//Neophob_LPD6803 strip = Neophob_LPD6803(PIXELS_PER_PANEL*NR_OF_PANELS);
WS2801 strip = WS2801(PIXELS_PER_PANEL*NR_OF_PANELS, DATA_PIN, CLOCK_PIN);

//network stuff, TODO: add dhcp/bonjour support
byte mac[] = { 0xBE, 0x00, 0xBE, 0x00, 0xBE, 0x01 };
IPAddress ip(192, 168, 111, 177);
EthernetUDP Udp;



void setup() {  
  Serial.begin(115200);
  Serial.println("Hello!");

  // start the Ethernet and UDP:
  Ethernet.begin(mac,ip);
  Udp.begin(TPM2NET_LISTENING_PORT);
  memset(packetBuffer, 0, UDP_PACKET_SIZE);

  strip.begin();
  showInitImage();      // display some colors
  
  Serial.println("Setup done");
}


void loop() {
  // if there's data available, read a packet
  int packetSize = Udp.parsePacket();
  Serial.print("Received packet of size ");
  Serial.println(packetSize);
  
  //tpm2 header size is 5 bytes
  if (packetSize>EXPECTED_PACKED_SIZE) {
    
    // read the packet into packetBufffer
    Udp.read(packetBuffer, UDP_PACKET_SIZE);
    
    // -- Header check
    
    //check header byte
    if (packetBuffer[0]!=TPM2NET_HEADER_IDENT) {
      Serial.print("Invalid header ident ");
      Serial.println(packetBuffer[0], HEX);
      return;
    }
    
    //check command
    if (packetBuffer[1]!=TPM2NET_CMD_DATAFRAME) {
      Serial.print("Invalid block type ");
      Serial.println(packetBuffer[1], HEX);
      return;
    }
    
    uint16_t frameSize = packetBuffer[2];
    frameSize = (frameSize << 8) + packetBuffer[3];
    Serial.print("Framesize ");
    Serial.println(frameSize, HEX);

    //use packetNumber to calculate offset
    uint8_t packetNumber = packetBuffer[4];
    Serial.print("packetNumber ");
    Serial.println(packetNumber, HEX);

    //check footer
    if (packetBuffer[frameSize+TPM2NET_HEADER_SIZE]!=TPM2NET_FOOTER_IDENT) {
      Serial.print("Invalid footer ident ");
      Serial.println(packetBuffer[frameSize+TPM2NET_HEADER_SIZE], HEX);
      return;
    }

    //calculate offset
    uint16_t currentLed = packetNumber*PIXELS_PER_PANEL;
    int x=TPM2NET_HEADER_SIZE;
    for (byte i=0; i < frameSize; i++) {
      strip.setPixelColor(currentLed++, packetBuffer[x], packetBuffer[x+1], packetBuffer[x+2]);
      x+=3;
    }
    
    //TODO maybe update leds only if we got all pixeldata?
    strip.show();   // write all the pixels out
  }
}

// --------------------------------------------
//     create initial image
// --------------------------------------------
void showInitImage() {
  //just create some boring colors
  for (int i=0; i < strip.numPixels(); i++) {
    strip.setPixelColor(i, (i)%255, (i*2)%255, (i*4)%255);
  }    
  // Update the strip
  strip.show();
}



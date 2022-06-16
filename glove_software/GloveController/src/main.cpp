#include "SensorValues.hpp"

#define I2C_SDA 5
#define I2C_SCL 18

const char* SSID = "GloveController";
const char* password = "eqis7nSI";
const char* host = "192.168.4.2";
const uint16_t port = 8080;


//const int SCLPin = 18;
//const int SDAPin = 15;

// just for testing
const uint8_t magicByte = 66;

//const uint16_t fingerOne = 999;
//const uint16_t fingerTwo = 1000;
//const uint16_t fingerThree = 1001;
//const uint16_t fingerFour = 1002;
//const uint16_t fingerFive = 1003;
const uint16_t axisX = 1004;
const uint16_t axisY = 1005;
const uint16_t axisZ = 1006;
/*
const uint8_t buffer[18] = {
  magicByte,
  fingerOne >> 8,
  fingerOne & 0xFF,
  fingerTwo >> 8,
  fingerTwo & 0xFF,
  fingerThree >> 8,
  fingerThree & 0xFF,
  fingerFour >> 8,
  fingerFour & 0xFF,
  fingerFive >> 8,
  fingerFive & 0xFF,
  axisX >> 8,
  axisX & 0xFF,
  axisY >> 8,
  axisY & 0xFF,
  axisZ >> 8,
  axisZ & 0xFF,
  magicByte
};
*/




TwoWire i2c = TwoWire(0);
WiFiServer server(80);
Adafruit_MPU6050 gyro;


void setup() 
{
  // setup sequence
  Serial.begin(115200);
  WiFi.softAP(SSID, password);
  i2c.begin(I2C_SDA,I2C_SCL, (uint32_t)100000);

  IPAddress IP = WiFi.softAPIP();
  Serial.print("IP Address: ");
  Serial.println(IP);
/*
  if (!gyro.begin((uint8_t)104U, &i2c)) 
  {
		Serial.println("Failed to find MPU6050 chip");
		while (1) 
    {
		  delay(10);
		}
	}
	Serial.println("MPU6050 Found!");
*/
  server.begin();
  delay(10000);
}



void loop() 
{
  SensorValues* values_ =  new SensorValues(36,33,39,32,34);
  WiFiClient client = server.available();

  FingerSensor indexFinger = FingerSensor(32, 5, 10);
  while(true)
  {
    Serial.println(indexFinger.getFilteredValue());
  }
  
  /*
  while(client.connected())
  {
    
    Serial.println("reeeeee");

    indexValue = analogRead(34);
    Serial.print(indexValue);
    indexValue1 = analogRead(32);
    Serial.print(indexValue1);
    indexValue2 = analogRead(39);
    Serial.print(indexValue2);
    indexValue3 = analogRead(33);
    Serial.print(indexValue3);
    indexValue4 = analogRead(36);
    Serial.println(indexValue4);

    
  }
  */
  if (!client.connect(host, port)) {
 
        Serial.println("Connection to host failed");

        delete values_;
        delay(100);
        return;
    }
 
    Serial.println("Connected to server successful!");


    while (client.availableForWrite()) 
    {
      //client.write(buffer, 18);
      client.flush();
    }


    Serial.println("Disconnecting...");
    client.stop();
    
    delete values_;
    delay(1000);
  //client.stop();
}

#include <Wifi.h>
#include "FingerSensor.hpp"
#include "MPU6050_Custom.hpp"

#define I2C_SDA 5
#define I2C_SCL 18

const char* SSID = "GloveController";
const char* password = "eqis7nSI";
const char* host = "192.168.4.2";
const uint16_t port = 8080;

// just for testing
const uint8_t magicByte = 42;
WiFiServer server(80);
TwoWire I2C_Bus_ = TwoWire(0);
MPU6050_Custom mpu6050_;


void setup() 
{
  // setup sequence
  Serial.begin(115200);
  WiFi.softAP(SSID, password);
  I2C_Bus_.begin(I2C_SDA, I2C_SCL, (uint32_t)100000);
  IPAddress IP = WiFi.softAPIP();

  if(!mpu6050_.begin((uint8_t)104U, &I2C_Bus_))
    {
        Serial.println("Failed to find MPU6050 chip :(");
        while (1) { delay(10); }
    }
  Serial.println("MPU6050 connected and configured :)");  

  Serial.print("IP Address: ");
  Serial.println(IP);
  server.begin();
  delay(1000);
}


void loop() 
{
    WiFiClient client = server.available();
    FingerSensor thumb = FingerSensor(34, 1, 10);
    FingerSensor indexFinger = FingerSensor(32, 1, 10);
    FingerSensor middleFinger = FingerSensor(39, 1, 10);
    FingerSensor ringFinger = FingerSensor(33, 1, 10);
    FingerSensor pinky = FingerSensor(36, 1, 10);

    uint8_t packet_id = 0;
    uint8_t dummy1 = 0;
    uint8_t dummy2 = 0;
    uint8_t dummy3 = 0;

    if (!client.connect(host, port)) 
    {
        Serial.println("Connection to host failed");
        delay(100);
        return;
    }
    Serial.println("Successfully connected to server!");

    while(client.availableForWrite())
    {
        if(packet_id < 255)
            packet_id++;
        else
            packet_id = 0;

        mpu6050_.readRawValues();

        uint8_t buffer[32]
        {
            magicByte,
            packet_id,
            indexFinger.getFilteredValue() >>  8,
            indexFinger.getFilteredValue() & 0xFF,
            middleFinger.getFilteredValue() >> 8,
            middleFinger.getFilteredValue() & 0xFF,
            ringFinger.getFilteredValue() >> 8,
            ringFinger.getFilteredValue() & 0xFF,
            pinky.getFilteredValue() >> 8,
            pinky.getFilteredValue() & 0xFF,
            thumb.getFilteredValue() >> 8,
            thumb.getFilteredValue() & 0xFF,
            mpu6050_.getRawAccX_0(),
            mpu6050_.getRawAccX_1(),
            mpu6050_.getRawAccY_0(),
            mpu6050_.getRawAccY_1(),
            mpu6050_.getRawAccZ_0(),
            mpu6050_.getRawAccZ_1(),
            mpu6050_.getRawGyroX_0(),
            mpu6050_.getRawGyroX_1(),
            mpu6050_.getRawGyroY_0(),
            mpu6050_.getRawGyroY_1(),
            mpu6050_.getRawGyroZ_0(),
            mpu6050_.getRawGyroZ_1(),
            (mpu6050_.getTime() >> 24) & 0xFF,
            (mpu6050_.getTime() >> 16) & 0xFF,
            (mpu6050_.getTime() >> 8) & 0xFF,
            mpu6050_.getTime() & 0xFF,
            dummy1,
            dummy2, 
            dummy3,
            magicByte,
        };

        client.write(buffer, 32);
        client.flush();
    }
  
    Serial.println("Disconnecting...");
    client.stop();
}

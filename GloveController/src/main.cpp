#include <Wifi.h>
#include "FingerSensor.hpp"
#include "MPU6050_Custom.hpp"

#define I2C_SDA 5
#define I2C_SCL 18

const static char* SSID = "GloveController";
const static char* password = "eqis7nSI";
const static char* host = "192.168.4.2";
const static uint16_t port = 8080;

const static WiFiClient client;

static FingerSensor thumb(34, 1, 10);
static FingerSensor indexFinger(32, 1, 10);
static FingerSensor middleFinger(39, 1, 10);
static FingerSensor ringFinger(33, 1, 10);
static FingerSensor pinky(36, 1, 10);

static TwoWire I2C_Bus_ = TwoWire(0);
static MPU6050_Custom mpu;

const static uint8_t magicByte = 42;
const static uint8_t dummy1 = 0;
const static uint8_t dummy2 = 0;
const static uint8_t dummy3 = 0;

void setup()
{
  // setup sequence
  Serial.begin(115200);
  WiFi.softAP(SSID, password);
  I2C_Bus_.begin(I2C_SDA, I2C_SCL, (uint32_t)100000);
  IPAddress IP = WiFi.softAPIP();

  if(!mpu.begin((uint8_t)104U, &I2C_Bus_))
    {
        Serial.println("Failed to find MPU6050 chip :(");
        while (1) { delay(10); }
    }
  Serial.println("MPU6050 connected and configured :)");

  Serial.print("IP Address: ");
  Serial.println(IP);

  delay(1000);
}

static uint8_t buffer[32];

void loop()
{
    if (!client.connect(host, port))
    {
        Serial.println("Connection to host failed");
        delay(1000);
        return;
    }

    client.setNoDelay(true);

    Serial.println("Successfully connected to server!");

    uint8_t packet_id = 0;

    while(client.connected())
    {
        mpu.readRawValues();
        buffer[0] = magicByte;
        buffer[1] = packet_id++;
        buffer[2] = indexFinger.getFilteredValue() >>  8;
        buffer[3] = indexFinger.getFilteredValue() & 0xFF;
        buffer[4] = middleFinger.getFilteredValue() >> 8;
        buffer[5] = middleFinger.getFilteredValue() & 0xFF;
        buffer[6] = ringFinger.getFilteredValue() >> 8;
        buffer[7] = ringFinger.getFilteredValue() & 0xFF;
        buffer[8] = pinky.getFilteredValue() >> 8;
        buffer[9] = pinky.getFilteredValue() & 0xFF;
        buffer[10] = thumb.getFilteredValue() >> 8;
        buffer[11] = thumb.getFilteredValue() & 0xFF;
        buffer[12] = mpu.getRawGyroX_0();
        buffer[13] = mpu.getRawGyroX_1();
        buffer[14] = mpu.getRawGyroY_0();
        buffer[15] = mpu.getRawGyroY_1();
        buffer[16] = mpu.getRawGyroZ_0();
        buffer[17] = mpu.getRawGyroZ_1();
        buffer[18] = mpu.getRawAccX_0();
        buffer[19] = mpu.getRawAccX_1();
        buffer[20] = mpu.getRawAccY_0();
        buffer[21] = mpu.getRawAccY_1();
        buffer[22] = mpu.getRawAccZ_0();
        buffer[23] = mpu.getRawAccZ_1();
        buffer[24] = (mpu.getTime() >> 24) & 0xFF;
        buffer[25] = (mpu.getTime() >> 16) & 0xFF;
        buffer[26] = (mpu.getTime() >> 8) & 0xFF;
        buffer[27] = mpu.getTime() & 0xFF;
        buffer[28] = dummy1;
        buffer[29] = dummy2;
        buffer[30] = dummy3;
        buffer[31] = magicByte;

        int wrote = client.write(buffer, 32);

        if (wrote != 32) {
            Serial.printf("We wrote %d bytes instead of 32!\n", wrote);
        }

        client.flush();
    }

    Serial.println("Disconnecting...");
    client.stop();
}

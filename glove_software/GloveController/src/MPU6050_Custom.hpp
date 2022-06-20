#ifndef MPU6050_CUSTOM_H
#define MPU6050_CUSTOM_H

#include <Arduino.h>
#include <Adafruit_BusIO_Register.h>
#include <Adafruit_I2CDevice.h>
#include <Adafruit_Sensor.h>

#define MPU6050_DEVICE_ID 0x68
#define MPU6050_WHO_AM_I 0x75
#define MPU6050_ACCEL_OUT 0x3B  ///< base address for sensor data reads
#define MPU6050_I2CADDR_DEFAULT 0x68 ///< MPU6050 default i2c address w/ AD0 high

class MPU6050_Custom
{
    public:
        bool begin(uint8_t i2c_address = MPU6050_I2CADDR_DEFAULT, TwoWire *wire = &Wire, int32_t sensor_id = 0);
        void readRawValues(void);
        uint32_t getTime();
        uint8_t getRawAccX_0();
        uint8_t getRawAccX_1();
        uint8_t getRawAccY_0();
        uint8_t getRawAccY_1();
        uint8_t getRawAccZ_0();
        uint8_t getRawAccZ_1();
        uint8_t getRawGyroX_0();
        uint8_t getRawGyroX_1();
        uint8_t getRawGyroY_0();
        uint8_t getRawGyroY_1();
        uint8_t getRawGyroZ_0();
        uint8_t getRawGyroZ_1();
    protected:
        Adafruit_I2CDevice *i2c_dev = NULL;
    private:
        uint32_t time;
        uint8_t rawAccX_0_;
        uint8_t rawAccX_1_;
        uint8_t rawAccY_0_;
        uint8_t rawAccY_1_;
        uint8_t rawAccZ_0_;
        uint8_t rawAccZ_1_;
        uint8_t rawGyroX_0_;
        uint8_t rawGyroX_1_;
        uint8_t rawGyroY_0_;
        uint8_t rawGyroY_1_;
        uint8_t rawGyroZ_0_;
        uint8_t rawGyroZ_1_;

};

#endif
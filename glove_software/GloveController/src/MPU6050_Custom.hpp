#ifndef MPU6050_CUSTOM_H
#define MPU6050_CUSTOM_H

#include <Arduino.h>
#include <Adafruit_BusIO_Register.h>
#include <Adafruit_I2CDevice.h>
#include <Adafruit_Sensor.h>

#define MPU6050_DEVICE_ID 0x68
#define MPU6050_WHO_AM_I 0x75
#define MPU6050_ACCEL_OUT 0x3B
#define MPU6050_I2CADDR_DEFAULT 0x68 
#define MPU6050_SMPLRT_DIV 0x19
#define MPU6050_CONFIG 0x1A 
#define MPU6050_GYRO_CONFIG 0x1B
#define MPU6050_ACCEL_CONFIG 0x1C
#define MPU6050_PWR_MGMT_1 0x6B

typedef enum {
  MPU6050_BAND_260_HZ, ///< Docs imply this disables the filter
  MPU6050_BAND_184_HZ, ///< 184 Hz
  MPU6050_BAND_94_HZ,  ///< 94 Hz
  MPU6050_BAND_44_HZ,  ///< 44 Hz
  MPU6050_BAND_21_HZ,  ///< 21 Hz
  MPU6050_BAND_10_HZ,  ///< 10 Hz
  MPU6050_BAND_5_HZ,   ///< 5 Hz
} mpu6050_bandwidth_t;

typedef enum {
  MPU6050_RANGE_250_DEG,  ///< +/- 250 deg/s 
  MPU6050_RANGE_500_DEG,  ///< +/- 500 deg/s (default value)
  MPU6050_RANGE_1000_DEG, ///< +/- 1000 deg/s
  MPU6050_RANGE_2000_DEG, ///< +/- 2000 deg/s
} mpu6050_gyro_range_t;

typedef enum {
  MPU6050_RANGE_2_G = 0b00,  ///< +/- 2g 
  MPU6050_RANGE_4_G = 0b01,  ///< +/- 4g
  MPU6050_RANGE_8_G = 0b10,  ///< +/- 8g (default value)
  MPU6050_RANGE_16_G = 0b11, ///< +/- 16g
} mpu6050_accel_range_t;


class MPU6050_Custom
{
    public:
        bool begin(uint8_t i2c_address = MPU6050_I2CADDR_DEFAULT, TwoWire *wire = &Wire, int32_t sensor_id = 0, 
                    uint8_t sample_rate_divisor = 0, mpu6050_bandwidth_t bandwidth = MPU6050_BAND_260_HZ,
                    mpu6050_gyro_range_t gyro_range = MPU6050_RANGE_500_DEG, mpu6050_accel_range_t acc_range = MPU6050_RANGE_8_G);
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
        void setFilterBandwidth(mpu6050_bandwidth_t bandwidth);
        void setSampleRateDivisor(uint8_t divisor);
        void setGyroRange(mpu6050_gyro_range_t new_range);
        void setAccelerometerRange(mpu6050_accel_range_t new_range);
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
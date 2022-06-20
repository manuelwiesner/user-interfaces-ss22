# include "MPU6050_Custom.hpp"


bool MPU6050_Custom::begin(uint8_t i2c_address, TwoWire *wire, int32_t sensor_id) 
{
  if (i2c_dev)
  {
    delete i2c_dev; // remove old interface
  }

  i2c_dev = new Adafruit_I2CDevice(i2c_address, wire);

  if (!i2c_dev->begin())
  {
    return false;
  }

  Adafruit_BusIO_Register chip_id = Adafruit_BusIO_Register(i2c_dev, MPU6050_WHO_AM_I, 1);

  // make sure we're talking to the right chip
  if (chip_id.read() != MPU6050_DEVICE_ID) 
  {
    return false;
  }

  return true;
}

void MPU6050_Custom::readRawValues(void)
{
    Adafruit_BusIO_Register data_reg =
      Adafruit_BusIO_Register(i2c_dev, MPU6050_ACCEL_OUT, 14);

    uint8_t buffer[14];
    data_reg.read(buffer, 14);

    time = millis();

    rawAccX_0_ = buffer[0];
    rawAccX_1_ = buffer[1];
    rawAccY_0_ = buffer[2];
    rawAccY_1_ = buffer[3];
    rawAccZ_0_ = buffer[4];
    rawAccZ_1_ = buffer[5];
    // buffer[6] and buffer[7] are the temperature values, we don't really need them
    rawGyroX_0_ = buffer[8];
    rawGyroX_1_ = buffer[9];
    rawGyroY_0_ = buffer[10];
    rawGyroY_1_ = buffer[11];
    rawGyroZ_0_ = buffer[12];
    rawGyroZ_1_ = buffer[13];
  
}

uint8_t MPU6050_Custom::getRawAccX_0()
{
  return rawAccX_0_;
}

uint8_t MPU6050_Custom::getRawAccX_1()
{
  return rawAccX_1_;
}

uint8_t MPU6050_Custom::getRawAccY_0()
{
  return rawAccY_0_;
}

uint8_t MPU6050_Custom::getRawAccY_1()
{
  return rawAccY_1_;
}

uint8_t MPU6050_Custom::getRawAccZ_0()
{
  return rawAccZ_0_;
}

uint8_t MPU6050_Custom::getRawAccZ_1()
{
  return rawAccZ_1_;
}

uint8_t MPU6050_Custom::getRawGyroX_0()
{
  return rawGyroX_0_;
}

uint8_t MPU6050_Custom::getRawGyroX_1()
{
  return rawGyroX_1_;
}

uint8_t MPU6050_Custom::getRawGyroY_0()
{
  return rawGyroY_0_;
}

uint8_t MPU6050_Custom::getRawGyroY_1()
{
  return rawGyroY_1_;
}

uint8_t MPU6050_Custom::getRawGyroZ_0()
{
  return rawGyroZ_0_;
}

uint8_t MPU6050_Custom::getRawGyroZ_1()
{
  return rawGyroZ_1_;
}

uint32_t MPU6050_Custom::getTime()
{
  return time;
}


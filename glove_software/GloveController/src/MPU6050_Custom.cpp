# include "MPU6050_Custom.hpp"


bool MPU6050_Custom::begin(uint8_t i2c_address, TwoWire *wire, int32_t sensor_id, uint8_t sample_rate_divisor, 
                            mpu6050_bandwidth_t bandwidth, mpu6050_gyro_range_t gyro_range, mpu6050_accel_range_t acc_range) 
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

  setSampleRateDivisor(sample_rate_divisor);
  setFilterBandwidth(bandwidth);
  setGyroRange(gyro_range);
  setAccelerometerRange(acc_range);

  Adafruit_BusIO_Register power_mgmt_1 = Adafruit_BusIO_Register(i2c_dev, MPU6050_PWR_MGMT_1, 1);
  power_mgmt_1.write(0x01);

  return true;
}


void MPU6050_Custom::setSampleRateDivisor(uint8_t divisor) 
{
  Adafruit_BusIO_Register sample_rate_div = Adafruit_BusIO_Register(i2c_dev, MPU6050_SMPLRT_DIV, 1);

  sample_rate_div.write(divisor);
}


void MPU6050_Custom::setFilterBandwidth(mpu6050_bandwidth_t bandwidth) 
{
  Adafruit_BusIO_Register config = Adafruit_BusIO_Register(i2c_dev, MPU6050_CONFIG, 1);
  Adafruit_BusIO_RegisterBits filter_config = Adafruit_BusIO_RegisterBits(&config, 3, 0);

  filter_config.write(bandwidth);
}


void MPU6050_Custom::setGyroRange(mpu6050_gyro_range_t new_range) 
{
  Adafruit_BusIO_Register gyro_config = Adafruit_BusIO_Register(i2c_dev, MPU6050_GYRO_CONFIG, 1);
  Adafruit_BusIO_RegisterBits gyro_range = Adafruit_BusIO_RegisterBits(&gyro_config, 2, 3);

  gyro_range.write(new_range);
}


void MPU6050_Custom::setAccelerometerRange(mpu6050_accel_range_t new_range) 
{
  Adafruit_BusIO_Register accel_config = Adafruit_BusIO_Register(i2c_dev, MPU6050_ACCEL_CONFIG, 1);
  Adafruit_BusIO_RegisterBits accel_range = Adafruit_BusIO_RegisterBits(&accel_config, 2, 3);

  accel_range.write(new_range);
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


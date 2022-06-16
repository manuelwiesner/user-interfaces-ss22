#ifndef SENSORVALUES_H
#define SENSORVALUES_H


#include <Adafruit_Sensor.h>
#include <Adafruit_MPU6050.h>
#include <WiFi.h>
#include "FingerSensor.hpp"

class SensorValues
{
private:
    uint8_t pinkyPin_;
    uint8_t ringFingerPin_;
    uint8_t middleFingerPin_;
    uint8_t indexFingerPin_;
    uint8_t thumbPin_;
    uint16_t thumbValue;
    uint16_t indexFingerValue;
    uint16_t middleFingerValue;
    uint16_t ringFingerValue;
    uint16_t pinkyValue;

public:
    SensorValues(uint8_t pinkyPin,  uint8_t ringFingerPin,  uint8_t middleFingerPin,  uint8_t indexFingerPin,  uint8_t thumbPin);
    ~SensorValues();
};

#endif
#ifndef FINGERSENSOR_H
#define FINGERSENSOR_H

#include <Arduino.h>
#include <Wire.h>
#include <EwmaT.h>

class FingerSensor
{
private:
    uint8_t sensorPin;
    uint16_t rawValue = 0;
    uint16_t filteredValue = 0;
    EwmaT<uint16_t> filter;
    void readSensor();
    void filterValue();

public:
    FingerSensor(uint8_t pin, uint16_t dividend, uint16_t divisor);
    ~FingerSensor();
    uint16_t getFilteredValue();
    uint16_t getRawValue();
    uint8_t get8BitFilteredValue();
};

#endif
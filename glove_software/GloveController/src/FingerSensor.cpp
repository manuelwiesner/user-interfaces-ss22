#include "FingerSensor.hpp"

FingerSensor::FingerSensor(uint8_t pin, uint16_t dividend, uint16_t divisor) : sensorPin(pin), filter(EwmaT<uint16_t>(dividend, divisor))
{
}

void FingerSensor::readSensor()
{
    rawValue = analogRead(sensorPin);
}

void FingerSensor::filterValue()
{
    readSensor();
    filteredValue = filter.filter(rawValue);
}

uint16_t FingerSensor::getFilteredValue()
{
    filterValue();
    return filteredValue;
}


FingerSensor::~FingerSensor() = default;
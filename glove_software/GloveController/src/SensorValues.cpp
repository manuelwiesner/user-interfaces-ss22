#include "SensorValues.hpp"

SensorValues::SensorValues( uint8_t pinkyPin,  uint8_t ringFingerPin,  uint8_t middleFingerPin,  uint8_t indexFingerPin,  uint8_t thumbPin)
    : pinkyPin_(pinkyPin), ringFingerPin_(ringFingerPin), middleFingerPin_(middleFingerPin), indexFingerPin_(indexFingerPin), thumbPin_(thumbPin)
{
}

SensorValues::~SensorValues()
{
    Serial.println("~~~deleted object~~~");
}



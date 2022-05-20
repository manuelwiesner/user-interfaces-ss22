#include <Arduino.h>
#include <WiFi.h>

const char* SSID = "";
const char* password = "";

const char* host = "";
const uint16_t port = 8080;

void setup() 
{
  // setup sequence
  Serial.begin(115200);
  WiFi.begin(SSID, password);

  while(WiFi.status() != WL_CONNECTED)
  {
    
  }
}

void loop() 
{

}
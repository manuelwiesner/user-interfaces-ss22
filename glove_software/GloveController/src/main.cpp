#include <Arduino.h>
#include <WiFi.h>

const char* SSID = "GloveController";
const char* password = "eqis7nSI";

WiFiServer server(80);

void setup() 
{
  // setup sequence
  Serial.begin(115200);
  WiFi.softAP(SSID, password);

  IPAddress IP = WiFi.softAPIP();
  Serial.print("IP Address: ");
  Serial.println(IP);

  server.begin();

}

void loop() 
{
  WiFiClient client = server.available();
  while(client.connected())
  {
    Serial.println("reeeeee");
  }

  client.stop();
  Serial.println("Client disconnected.");
}

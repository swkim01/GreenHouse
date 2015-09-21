#include <Timer.h>
#include <PubSubClient.h>
#include <SoftwareSerial.h>
#include <ESP8266.h>
#include <ESP8266Client.h>
#include <string.h>
#include <DHT.h>

const int illu_pin=A0;
int illu_value=0;

const int soil_pin = A1;    
int soil_value = 0;

const int dht22_pin=4;
#define DHT_TYPE     DHT22
DHT dht(dht22_pin, DHT_TYPE);

const int fan_enpin=5;
const int fan_inpin=6;

const int led_enpin=8;
const int led_inpin=9;

Timer ts;

#define MQTT_SERVER "<server ip>"
SoftwareSerial esp8266Serial = SoftwareSerial(2, 3);
ESP8266 wifi = ESP8266(esp8266Serial);
ESP8266Client wifiClient(wifi, ESP8266_SINGLE_CLIENT);
PubSubClient mqttClient(MQTT_SERVER, 1883, callback, wifiClient);
#define WLAN_SSID       "<SSID>"  // cannot be longer than 32 characters!
#define WLAN_PASS       "<PASSWORD>"
#define WLAN_SECURITY   WLAN_SEC_WPA2

void mqttConnect() {
  Serial.println(F("Connecting to MQTT Broker..."));
  if (mqttClient.connect("arduinoclient")) {
    Serial.println(F("Connected to MQTT"));
    mqttClient.subscribe("greenhouse/sensor/led");
    mqttClient.subscribe("greenhouse/sensor/fan");
    mqttClient.subscribe("greenhouse/sensor/ledpower"); 
  } else {
    Serial.println(F("Failed connecting to MQTT"));
  }
}

void ensureConnected() { 
  if (!wifiClient.connected()) {
    wifi.begin();
  
    // setWifiMode
    Serial.print("setWifiMode: ");
    Serial.println(getStatus(wifi.setMode(ESP8266_WIFI_STATION)));
  
    // joinAP
    Serial.print(F("\nAttempting to connect to ")); Serial.println(WLAN_SSID);
    Serial.println(getStatus(wifi.joinAP(WLAN_SSID, WLAN_PASS)));
    Serial.println(F("Connected!"));

    mqttConnect();
  } else {
  }
}

// Callback function
void callback(char* topic, byte* payload, unsigned int length) {
  char message_buff[length+1];
  Serial.print(F("topic:"));
  Serial.println(topic);  
  
  int i = 0;  
  for(i=0; i<length; i++) {
    message_buff[i] = payload[i];
  }
  message_buff[i] = '\0';
  
  Serial.print(F("payload:"));
  String value = String(message_buff);
  Serial.println(value);
  if (value == "ON") {
    analogWrite(fan_enpin, 255);
    digitalWrite(fan_inpin,HIGH);
  }
  else if (value == "OFF") {
    analogWrite(fan_enpin, 0);
    digitalWrite(fan_inpin,HIGH);
  }
  else if (value == "0") {
    analogWrite(led_enpin,0);    
    digitalWrite(led_inpin,HIGH);
  }
  else if (value == "1") {
    analogWrite(led_enpin,28);    
    digitalWrite(led_inpin,HIGH);
  }
  else if (value == "4") {
    analogWrite(led_enpin,113);    
    digitalWrite(led_inpin,HIGH);
  }
  else if (value == "9") {
    analogWrite(led_enpin,255);    
    digitalWrite(led_inpin,HIGH);
  }
}

void setup()
{
  Serial.begin(9600);
  Serial.println("\nStarting...");
  while(!Serial) { }

  Serial.println("Initializing DHT sensor.");
  dht.begin();
  
  pinMode(led_enpin, OUTPUT);
  pinMode(led_inpin, OUTPUT);
  pinMode(fan_enpin, OUTPUT);
  pinMode(fan_inpin, OUTPUT);
  
  ts.every(10000, publishtoRealTime);
  ts.every(60000, publishtoDB);

  // ESP8266
  Serial.println(F("\nInitializing..."));
  esp8266Serial.begin(9600);
}

void loop() {
  ensureConnected();
  mqttClient.loop();
  ts.update();
}

int getillu() {
  illu_value =analogRead(illu_pin);
  Serial.print("illu sensor Values : ");
  Serial.println(illu_value);
  return illu_value;
}

int getsoil() {
  soil_value =analogRead(soil_pin);
  Serial.print("soil sensor Values : ");
  Serial.println(soil_value);
  return soil_value;
}
 
void publishtotopic(String topic, float val) {
  float temp1 = val;
  String temp =String(temp1); // int to chararray
  int vallen =temp.length()+1;
  char char_val[vallen];
  temp.toCharArray(char_val, vallen);
      
  int topiclen=topic.length()+1; //string to chararray
  char char_topic[topiclen];
  topic.toCharArray(char_topic, topiclen);
      
  mqttClient.publish(char_topic,char_val);
}

void publishtoRealTime() {
  publishtotopic("greenhouse/sensor/illu", getillu());
  publishtotopic("greenhouse/sensor/humi", dht.readHumidity());
  delay(2000);
  publishtotopic("greenhouse/sensor/temp", dht.readTemperature());
  publishtotopic("greenhouse/sensor/soil", getsoil());
}
 
void publishtoDB() {
  publishtotopic("greenhouse/db/sensor/R1/put/illu", getillu());
  publishtotopic("greenhouse/db/sensor/R1/put/humi", dht.readHumidity());
  delay(2000);
  publishtotopic("greenhouse/db/sensor/R1/put/temp", dht.readTemperature());
  Serial.println(F("pubtoDB OK"));
}

String getStatus(bool status)
{
  if (status)
    return "OK";
  return "KO";
}

String getStatus(ESP8266CommandStatus status)
{
  switch (status) {
  case ESP8266_COMMAND_INVALID:
    return "INVALID";
    break;
  case ESP8266_COMMAND_TIMEOUT:
    return "TIMEOUT";
    break;
  case ESP8266_COMMAND_OK:
    return "OK";
    break;
  case ESP8266_COMMAND_NO_CHANGE:
    return "NO CHANGE";
    break;
  case ESP8266_COMMAND_ERROR:
    return "ERROR";
    break;
  case ESP8266_COMMAND_NO_LINK:
    return "NO LINK";
    break;
  case ESP8266_COMMAND_TOO_LONG:
    return "TOO LONG";
    break;
  case ESP8266_COMMAND_FAIL:
    return "FAIL";
    break;
  default:
    return "UNKNOWN COMMAND STATUS";
    break;
  }
}

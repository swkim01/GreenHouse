#include <Timer.h>
#include <rDHT11.h>
#include <SPI.h>
#include <PubSubClient.h>
#include <WiFi.h>
#include <WiFiClient.h>
 
#define MQTT_SERVER "192.168.0.11" //server ip
 
char ssid[] = "lab919";          //  your network SSID (name) 
char pass[] = "software@2014";   // your network password
int status = WL_IDLE_STATUS;
boolean wifi_connected = false;
const int illuSensorPin=A0;
int illuValue=0;
int rDHT11pin=3;
rDHT11 DHT11(rDHT11pin);
Timer ts;
 
// Callback function header
void callback(char* topic, byte* payload, unsigned int length);
 
WiFiClient netClient;
PubSubClient client(MQTT_SERVER, 1883, callback, netClient);
 
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
  String jsonResponse = String(message_buff);
  Serial.println(jsonResponse);
  
}
 
void setup() {
  // start serial port:
  Serial.begin(115200);
  ts.every(10000,totalpublish);
}
void dotest(){
Serial.println("test");
 
}
void loop() {
  ensure_connected();
  client.loop();
  ts.update();
}
 
void ensure_connected() { 
  if (!client.connected()) {   
    if (!wifi_connected) {
      initWiFi();
    }
    mqtt_connect();
  } else {
  }
}
 
int getillu(){
  illuValue =analogRead(illuSensorPin);
  Serial.print("illu sensor Values : ");
  Serial.println(illuValue);
return illuValue;
}
void publishtotopic(String topic,int val,PubSubClient client){
      int temp1 = val;
      String temp =String(temp1); // int to chararray
      int vallen =temp.length()+1;
      char char_val[vallen];
      temp.toCharArray(char_val, vallen);
      
      
      
      int topiclen=topic.length()+1; //string to chararray
      char char_topic[topiclen];
      topic.toCharArray(char_topic, topiclen);
      
      
      client.publish(char_topic,char_val);
     
}
 
void mqtt_connect() {
    Serial.println(F("Connecting to MQTT Broker..."));
    if (client.connect("arduinoclient")) {
      Serial.println(F("Connected to MQTT"));
      totalpublish();
     } else {
      Serial.println(F("Failed connecting to MQTT"));
    }
}
 
void totalpublish(){
      publishtotopic("hoyong/sensor/illu",getillu(),client);
      publishtotopic("hoyong/sensor/humi",dht11print(1),client);
      delay(2000);
      publishtotopic("hoyong/sensor/temp",dht11print(2),client);
     
 
 
}
 
 
 
 
 
 
void initWiFi() {
  Serial.println(F("Attempting to connect to WPA network..."));
  Serial.print(F("SSID:"));
  Serial.println(ssid);
  status = WiFi.begin(ssid, pass);
  if ( status != WL_CONNECTED) { 
    Serial.println(F("Couldn't get a wifi connection"));
    wifi_connected = false;
  } 
  else {
    Serial.print(F("Connected to wifi. My address:"));
    IPAddress myAddress = WiFi.localIP();
    Serial.println(myAddress);
    wifi_connected = true;
    delay(1000);
  }
}
float dht11print(int number){
int result = DHT11.update();
  // Comprobamos si la lectura ha sido exitosa
  switch (result)
  {
    case rDHT11Definitions::OK: 
		// Mostramos los valores recogidos
                  if(number==1){
                  Serial.print("Humidity (%): ");
                  Serial.println((float)DHT11.getHumidity(), 2);
                  return (float)DHT11.getHumidity();}
                  else if(number==2){
                  Serial.print("Temperature (oC): ");
                  Serial.println((float)DHT11.getCelsius(), 2);
                  return (float)DHT11.getCelsius();
		}
                break;
    case rDHT11Definitions::CHECKSUM_ERROR: 
		Serial.println("Checksum error"); 
		break;
    case rDHT11Definitions::TIMEOUT_ERROR: 
		Serial.println("Time out error");
		break;
    default: 
		Serial.println("Unknown error"); 
		break;
 
  }
}

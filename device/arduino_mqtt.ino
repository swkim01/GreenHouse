#include <Timer.h>
#include <rDHT11.h>
#include <SPI.h>
#include <PubSubClient.h>
#include <WiFi.h>
#include <WiFiClient.h>
 
#define MQTT_SERVER "<server ip>" //server ip
 
char ssid[] = "<SSID>";       //  your network SSID (name) 
char pass[] = "<PASSWORD>";   // your network password
int status = WL_IDLE_STATUS;
boolean wifi_connected = false;
const int illuSensorPin=A0;
int illuValue=0;
int rDHT11pin=3;
rDHT11 DHT11(rDHT11pin);
Timer ts;
const int led = 53;
 
const int led_enpin=6;
const int led_inpin=5;
const int fan_enpin=8;
const int fan_inpin=9;
 
int soil_Pin = A1;    
int soil_Value = 0;
 
 
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
  String value = String(message_buff);
  Serial.println(value);
  if(value=="true"){
    analogWrite(fan_enpin,255);
    digitalWrite(fan_inpin,HIGH);
 }  
  else if(value=="false"){
    analogWrite(fan_enpin,0);
    digitalWrite(fan_inpin,HIGH); 
  }
   else if(value=="0"){
    analogWrite(led_enpin,0);
    digitalWrite(led_inpin,HIGH);
  }
   else if(value=="1"){
    analogWrite(led_enpin,28);
    digitalWrite(led_inpin,HIGH);  
  }
   else if(value=="4"){
    analogWrite(led_enpin,113);
    digitalWrite(led_inpin,HIGH);  
  }
   else if(value=="9"){
    analogWrite(led_enpin,255);
    digitalWrite(led_inpin,HIGH);  
  }
}

void ledset(boolean value){
  if(value==true){
    digitalWrite(led, HIGH);
    return;
  }
  else
    digitalWrite(led, LOW); 
  return;
}

void setup() {
  // start serial port:
  Serial.begin(115200);
   pinMode(led, OUTPUT);  
  ts.every(10000,publishtoRealtime);
  ts.every(60000,publishtoDB);
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
int getsoil(){
  soil_Value =analogRead(soil_Pin);
  Serial.print("soil sensor Values : ");
  Serial.println(soil_Value);
return soil_Value;
}
 
void publishtotopic(String topic,int val){
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
      //totalpublish();
      client.subscribe("greenhouse/sensor/led");
      client.subscribe("greenhouse/sensor/fan");
      client.subscribe("greenhouse/sensor/ledpower");
      
     } else {
      Serial.println(F("Failed connecting to MQTT"));
    }
}
 
void publishtoRealtime(){
    publishtotopic("hoyong/sensor/illu",getillu());
    publishtotopic("hoyong/sensor/humi",dht11print(1));
    delay(2000);
    publishtotopic("hoyong/sensor/temp",dht11print(2));
    publishtotopic("hoyong/sensor/soil",getsoil());
}
 
void publishtoDB(){
    publishtotopic("greenhouse/db/sensor/R1/put/illu",getillu());
    publishtotopic("greenhouse/db/sensor/R1/put/humi",dht11print(1));
    delay(2000);
    publishtotopic("greenhouse/db/sensor/R1/put/temp",dht11print(2));
    Serial.println(F("pubtoDB OK"));
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
      return (float)DHT11.getHumidity();
    }
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

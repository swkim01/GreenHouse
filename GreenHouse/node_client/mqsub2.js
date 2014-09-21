var mqtt= require('mqtt')
  , client = mqtt.createClient();

client.subscribe('message/good');
//client.publish('messages', 'hello me!');
client.on('message', function(topic, message) {
  console.log("console : "+message);
});
client.options.reconnectPeriod = 0;  // disable automatic reconnect

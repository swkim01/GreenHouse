var mqtt =require('mqtt')
  , client = mqtt.createClient(1883,'127.0.0.1');
client.on('message', function(topic, message) {
    console.log("console : "+message);

});




//client.publish('7', 'hoyong');

//client.subscribe('good');

client.publish('hoyong/db/sensor/R2/put/temp', '32');

client.publish('hoyong/db/sensor/R1/put/humi', '40');


//client.subscribe('hoyong/sensor/R1/humi');
//client.publish('hoyong/db/sensor/R1/get/humi','goood');


//client.publish('messages/4', '4');i
//client.publish('messages/put', 'goodjob');
//client.publish('messages/1', '1');







//client.publish('messages', 'goodday1');

//client.publish('ggg', 'goodday2');

//client.publish('good', 'goodday1');

//client.publish('nice', 'goodday2');

//client.publish('good', 'helloMan');



//client.publish('messages', 'remember that!', {retain: true});
//client.end();
//client.publish('messages', 'remember that!', {retain: true});
//client.publish('message/good', 'remember that!');

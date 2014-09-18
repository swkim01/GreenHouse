var mqtt= require('mqtt')
  , client = mqtt.createClient();

var mongoose = require('mongoose');

var connection=mongoose.createConnection('mongodb://localhost/R1_db');

var Schema = mongoose.Schema;
var ObjectId = Schema.ObjectId;

var ThingSchema = new Schema({
  'key' : String,
  'value' : String,
  'date' :Date
});

var Thing = connection.model('thing', ThingSchema);

//var article = new Thing({name: "Titleaaa", age:3});
//    Thing.create({name:"ti",age:444,date:new Date()});


//client.subscribe('hoyong/sensor/illu');
client.subscribe('messages/+');
client.publish('messages', 'hello me!');
//client.subscribe('#');
client.on('message', function(topic, message) {
    console.log("console : "+message);
    console.log("topic : "+topic);
    
    //var token=topic;
    var token=topic.split('/');
    console.log(token[0]+','+token[1]+','+token.length);

    Thing.create({key : topic, value : message, date : new Date()},function(err){
    
    client.publish('good',message);
});
//    article.date=new Date();
//    article.save(function (err) {
//         if (err) {
//             console.log("fail");
//         }
//   client.publish('good','goodbye');
//    console.log("success");
//    });
});

client.options.reconnectPeriod = 0;  // disable automatic reconnect

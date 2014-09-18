var mqtt= require('mqtt')
  , client = mqtt.createClient();
var async = require('async');
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

client.subscribe('+/db/#');
//client.subscribe('#');
client.on('message', function(topic, message) {
   // console.log("topic : "+topic + "     message :" + message);
    
    var token=topic.split('/');
    async.series([
    function(callback) {
    for(var i=0;i<token.length ;i++){
      if(token[i]=='db'){
        token.splice(i,1);}
      if(token[i]=='put'){
        token.splice(i,1);
        var retopic =token.join('/');
        //console.log('sum ='+ retopic);
        var nowdate = new Date();
        Thing.create({key : retopic, value : message, date : nowdate},function(err){
        console.log('-TOPIC :'+topic);
        console.log('-MESSAGE : '+message);
        console.log('-Time : '+nowdate);
        console.log('');}
        );}
    }
      callback(null);
    },
    function(callback) {
    for(var j=0;j<token.length ;j++){
      if(token[j]=='db'){
       token.splice(j,1);
        console.log('tt');}
      if(token[j]=='get'){
        token.splice(j,1);
        var keyword= token.join('/');
        console.log('keyword :' +keyword);
        Thing.find({key:keyword},function(err,docs){
        docs.forEach(function(doc){
//         console.log(doc); });
        });});
        client.publish(keyword,"messageisgood");


    }
    }
    
      callback(null);
    }
    
    ],
    function(err, results) {
//      console.log(arguments);
    });
    //console.log(token[0]+','+token[1]+','+token.length);

//    Thing.create({key : topic, value : message, date : new Date()},function(err){
//    Thing.find({key:'messages/1'},function(err,doc){
//    });i
//    client.publish('good',message);
//});

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

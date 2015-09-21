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
var total=[];

client.subscribe('+/db/#');

client.on('message', function(topic, message) {
    console.log("TOPIC : "+topic + "     MESSAGE :" + message);
    
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
//        console.log('-TOPIC :'+topic);
//        console.log('-MESSAGE : '+message);
//        console.log('-Time : '+nowdate);
//       console.log('');
        }
        );}
    }
      callback(null);
    },
    function(callback) {
    for(var j=0;j<token.length ;j++){
      if(token[j]=='db'){
       token.splice(j,1);
      }
      if(token[j]=='get'){
        token.splice(j,1);
        var keyword= token.join('/');
        console.log('KEYWORD :' +keyword);
      


//        Thing.find({key:keyword},function(err,docs){
//          docs.forEach(function(doc){
         
//        Thing.find({key:keyword}).sort('date',-1).exec(function(err,docs){
//          docs.forEach(function(doc){
      Thing.find({key:keyword}).sort({date: -1}).exec(function(err,docs){ 
      
//     console.log(Object.keys(docs).length);//find length
         var limit=parseInt(message);
         var count=0;
         docs.forEach(function(doc){
         if(count++<limit){
           var arr={};
           arr ['value']=doc.value;  
           arr ['date']=doc.date;
           total.push(arr);
           }
         });
         var jsonObj={dataList:total}; 
         console.log(jsonObj);   
         var jsonObject=JSON.stringify(jsonObj); 
         //console.log(jsonObject);
         // client.publish(keyword,jsonObject);
           client.publish(keyword,jsonObject,function(err){total=[];});
        
      });
    
    }}
      callback(null);
    }
    ],
    function(err, results) {
//      console.log(arguments);
    });
});

client.options.reconnectPeriod = 0;  // disable automatic reconnect

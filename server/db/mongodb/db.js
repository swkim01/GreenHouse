var mongoose = require('mongoose');
var connection=mongoose.createConnection('mongodb://localhost/R1_db');
var Schema = mongoose.Schema;

var ThingSchema = new Schema({
  'key' : String,
  'value' : String,
  'date' :Date
});
module.exports = connection.model('thing', ThingSchema);
//module.export =Thing;

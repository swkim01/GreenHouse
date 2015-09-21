import datetime
import json
import paho.mqtt.client as mqtt
import sqlite3

db = sqlite3.connect("thing.db")
db.row_factory = sqlite3.Row
cur = db.cursor()
# table: R1 = {'key' : String, 'value' : String, 'date' : Date}

def on_connect(client, userdata, flags, rc):
    print("Connected with result code" + str(rc))
    client.subscribe('+/db/#')

def on_message(client, userdata, msg):
    print(msg.topic + " " + str(msg.payload))
    topic = msg.topic
    message = msg.payload
    token = topic.split('/')
    for i, word in enumerate(token):
        if word == 'db':
            token.pop(i)
        if word == 'put':
            token.pop(i)
            keyword = "/".join(token)
            nowdate = datetime.datetime.now()
            #Thing.create({'key' : keyword, 'value' : message, 'date' : nowdate},function(err){
            try:
                cur.execute("INSERT INTO R1(key,value,date) VALUES(?,?,?)",(keyword, message, nowdate))
                db.commit()
            except sqlite3.Error,e:
                # rollback on error
                if db:
                  db.rollback()
                print "Error %s:" % e.args[0]

        if word == 'get':
            token.pop(i)
            keyword = "/".join(token)
            print('KEYWORD :' +keyword)
            total = []
            row = cur.execute("select * from R1 where key is ? ORDER BY date ASC", (keyword,))
            if row:
                limit=int(message)
                count=0
                for data in row.fetchall():
                    if count < limit:
                        arr={}
                        arr['value']=data['value'] 
                        arr['date']=data['date']
                        total.append(arr)
                    count+=1
            jsonObj={'dataList':total}
            print(jsonObj)
            jsonObject=json.dumps(jsonObj)
            client.publish(keyword,jsonObject)

client = mqtt.Client()
client.on_connect = on_connect
client.on_message = on_message

client.connect("127.0.0.1", 1883, 60)

client.loop_forever()

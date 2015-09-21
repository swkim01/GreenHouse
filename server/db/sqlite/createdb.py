import sqlite3

db = sqlite3.connect("thing.db")
cur = db.cursor()

#cur.execute("CREATE TABLE R1 ( key TEXT PRIMARY KEY, value TEXT, date DATE )")
cur.execute("CREATE TABLE R1 ( key TEXT, value TEXT, date DATE )")

db.commit()
db.close()
exit()

import sqlite3
from flask import Flask, request, session, g, redirect, render_template
from contextlib import closing

app = Flask(__name__)
app.config.from_object('config')
def connect_db():
  return sqlite3.connect(app.config['DATABASE'])

def init_db():
    with closing(connect_db()) as db:
      with app.open_resource('schema.sql', mode='r') as f:
        db.cursor().executescript(f.read())
      db.commit()

@app.before_request
def before_request():
  g.db = connect_db()

@app.teardown_request
def teardown_request(exception):
  db = getattr(g, 'db', None)
  if db is not None:
    db.close()

@app.route('/index')
def index():
    return "Hello, World!"

@app.route('/')
def show_entries():
    print "ok"
    cur = g.db.execute('select title, text from entries order by id desc')
    entries = [dict(title=row[0], text=row[1]) for row in cur.fetchall()]
    return render_template('show_entries.html', entries=entries)

@app.route('/add')
def get_entry_page():
  return render_template('add_entry.html')

@app.route('/add', methods=['POST'])
def add_entry():
  g.db.execute('insert into entries (title, text) values (?, ?)',
    [request.form['title'], request.form['text']])
  g.db.commit()
  flash('New entry was successfully posted')
  return redirect(url_for('show_entries'))

# Run as a standalone
if __name__ == '__main__':
  app.run(debug=True)

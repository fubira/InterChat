let express = require('express');
let app = express();

let REDIS_URL = process.env.REDIS_URL || 'redis://127.0.0.1:6379'
let Redis = require('ioredis');
let redis = new Redis(REDIS_URL);

app.set('port', (process.env.PORT || 5000))

app.listen(app.get('port'), () => {
  console.log("Node app is running at localhost:" + app.get('port'));
});

let KEY = "logs";


// Post message
app.post('post', (req, res) => {
  let body = req.body;
  let now = DateTime.now();
  /// sync.zadd(key, time, jsonString);
  console.log(JSON.stringify(body));
  // redis.zadd(KEY, now, body)
  res.json({ result: 'ok' });
});

// Receive messages
app.get('receive', (req, res) => {
  let body = req.body;
  console.log(JSON.stringify(body));
  // List<ScoredValue<String>> scoredValue = sync.zrangebyscoreWithScores(key, Range.create(this.lastTime, Double.POSITIVE_INFINITY));
  res.json({ result: 'ok' });
});

// Expire old messages
app.get('expire', (req, res) => {
  // private long expireMillis = 1000 * 60 * 10;
  // sync.zremrangebyscore(key, Range.create(0, time - expireMillis));
  res.json({ result: 'ok' });
});

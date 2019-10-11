const express = require('express');
const bodyParser = require('body-parser');
const app = express();

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

const REDIS_URL = process.env.REDIS_URL || 'redis://127.0.0.1:6379'
const Redis = require('ioredis');
const redis = new Redis(REDIS_URL);

app.set('port', (process.env.PORT || 5000))

const server = app.listen(app.get('port'), function () {
  const host = server.address().address;
  const port = server.address().port;
  console.log('Example app listening at http://%s:%s', host, port);
});

const KEY = "messages";
const EXPIRESEC = 60 * 30;
redis.expire(KEY, EXPIRESEC);

app.get('/', (req, res) => {
  const time = Date.now();
  res.json({ result: 'ok', time });
});

// Post message
app.post('/post', (req, res) => {
  const time = Date.now();
  const body = req.body;
  console.log(body);

  redis.zadd(KEY, time, JSON.stringify(body)).then((result) => {
    console.log(result);
    res.json({ result: 'ok', time });
  });
});

// Get messages
app.get('/message', (req, res) => {
  const query = req.query;

  redis.zrangebyscore(KEY, query.from, "+inf", "WITHSCORES").then((result) => {
    let messages = [];
    let lastTime = query.from;
    while (result.length > 0) {
      let value = result.shift();
      lastTime = result.shift();
      messages.push(JSON.parse(value));
    }
    res.json({ result: 'ok', time: lastTime, messages });
  });
});

// Expire old messages
app.get('/expire', (req, res) => {
  const time = Date.now();
  redis.zremrangebyscore(KEY, 0, time - (EXPIRESEC * 1000)).then((err, result) => {
    res.json({ result: 'ok', time });
  });
});

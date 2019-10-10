const express = require('express');
const bodyParser = require('body-parser');
const app = express();

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

const REDIS_URL = process.env.REDIS_URL || 'redis://h:p7e3f731fa2fa7e59738c11534102195c276456cf3672539ff781042d66a8f91b@ec2-3-92-111-148.compute-1.amazonaws.com:22409'
const Redis = require('ioredis');
const redis = new Redis(REDIS_URL);

app.set('port', (process.env.PORT || 5000))

const server = app.listen(app.get('port'), function () {
  const host = server.address().address;
  const port = server.address().port;
  console.log('Example app listening at http://%s:%s', host, port);
});

const KEY = "messages";

app.get('/', (req, res) => {
  res.json({ result: 'ok', time });
});

// Post message
app.post('/post', (req, res) => {
  const time = Date.now();
  const body = req.body;

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
  const expireMillis = 1000 * 60 * 10;
  redis.zremrangebyscore(KEY, 0, time - expireMillis).then((err, result) => {
    res.json({ result: 'ok', time });
  });
});

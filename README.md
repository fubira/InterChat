# InterChat

*InteChat* is bukkit plugin to share chat between two or further Minecraft server mutually. This plugin does not need BungeeCord, it can share chat between independent servers.

This plugin uses Redis database to cache chat content temporarily.
Please install redis in your server, or examine the use of the cloud service.
(Redis server is using only for temporary cache, and the big capacity is not necessary.)

*InterChat* is licensed under the terms of the [MIT license](LICENSE.txt)

## Features

- Sharing chat message between multiple servers
- Sharing login and logout messages between multiple servers
- You can hide any messages by /ignore with [ChatCo](https://www.spigotmc.org/resources/chatco.38986/).

## Install backend server

Using docker compose (with redis image)

``` sh
git checkout https://github.com/fubira/InterChat.git
cd InterChat
docker-compose up
```

``` sh
git checkout https://github.com/fubira/InterChat.git
cd InterChat/server
docker build -t <yourname>/interchat-backend .
docker run -d -it \
  -v .:/var/www/app \
  -e REDIS_URL="redis://localhost:6379" -e PORT="5125" \
  -p 5125:5125 --name interchat <yourname>/interchat-backend
```

## Config

InterChat/config.yml

``` yaml
server:
  identify: "SERVER"
  color: GOLD

backend:
  url: "[REDIS URI]"
  authKey: "[AUTH KEY]"
```

- server.identify
  - Server name displayed with other server's chat.

- server.color
  - Display color of server name, [ChatColor](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/ChatColor.html)

- backend.url
  - Backend Server URL

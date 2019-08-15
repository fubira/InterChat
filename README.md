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

## Config

InterChat/config.yml

``` yaml
server:
  identify: "SERVER"
  color: GOLD

redis: 
  uri: "[REDIS URI]"
```

- server.identify
  - Server name displayed with other server's chat.

- server.color
  - Display color of server name, [ChatColor](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/ChatColor.html)

- redis.uri
  - Redis URI including id/password
  - "redis://[user:password@]host[:port]"

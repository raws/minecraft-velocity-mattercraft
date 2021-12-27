# Mattercraft

Mattercraft is a [Velocity](https://velocitypowered.com) Minecraft server proxy plugin that relays chat messages between a Velocity proxy server and [Matterbridge](https://github.com/42wim/matterbridge). It uses Matterbridge's HTTP API.

Mattercraft aims to be simple and lightweight. It relays all chat messages from every Minecraft server behind your Velocity proxy to Matterbridge, and vice-versa. Messages are sent and received on dedicated threads.

## Usage

To use Mattercraft, [install a release](https://github.com/raws/minecraft-velocity-mattercraft/releases) in Velocity's `plugins` folder. The next time you start Velocity, Mattercraft will automatically create `plugins/mattercraft/mattercraft.yml`. It'll look like this:

```yaml
---
api_key: s3cr3t
base_url: https://matterbridge.example.com
gateway: example
```

Configure Mattercraft to connect to your [Matterbridge API server](https://github.com/42wim/matterbridge/wiki/API). Then, fire up your Velocity server and start chatting!

## Contributing

Contributions are welcome! Feel free to [open a pull request](https://github.com/raws/minecraft-velocity-mattercraft) on GitHub.

## License

MIT
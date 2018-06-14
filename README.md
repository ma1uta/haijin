# haijin

## Run bot

```
java -jar haijin-X.X.jar -Xmx50m server config.yaml
```

## Configuration

### patterns

Patterns to serach quote

### homeserverUrl

Matrix's server url


### displayName

Initial display name of the bot

### username

Bot's username

### password

Bot's password

### prefix

Prefix of the commands.

### defaultCommand

If there not found command then invoke this one.

### commands

List of the bot's command

## Commands

All commands can be divided into two groups: first group is commands which can be executed only by owner, second groups
is commands which can be executed according to the access_policy setting.

### io.github.ma1uta.matrix.bot.command.Leave

Leave current room

### io.github.ma1uta.matrix.bot.command.NewName

Set new name

### io.github.ma1uta.matrix.bot.command.Pong

Ping-pong

### io.github.ma1uta.matrix.bot.command.SetAccessPolicy

Set or show access_policy settings. Possible values are 'ALL' and 'OWNER'

### io.github.ma1uta.matrix.bot.command.Join

Join new room

### io.github.ma1uta.matrix.bot.command.Prefix

Set new command prefix

### io.github.ma1uta.matrix.bot.command.DefaultCommand

Set new default command

### io.github.ma1uta.matrix.bot.command.Help

Show all commands

### io.github.ma1uta.haijin.command.Quote

Search quotes by alias

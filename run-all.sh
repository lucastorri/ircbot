#!/bin/bash

OUT_DIR=/Users/lucastorri/tmp/data
IRC_SERVER="irc.freenode.net"
BOT_NAME="logbot"
CHANNELS="scala scalatra playframework"

for CHANNEL in $CHANNELS
do
	java -cp target/ircbot-assembly-1.0.jar co.torri.ircbot.IRCBot "$BOT_NAME-${CHANNEL:0:5}" $IRC_SERVER "#$CHANNEL" $OUT_DIR > "$CHANNEL.log" &
done

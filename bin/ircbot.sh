#!/bin/bash

JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/1.6.0/Home/
MAIN=co.torri.ircbot.IRCBot
CLASSPATH=/Users/lucastorri/Projects/ircbot/target/ircbot-assembly-1.0.jar
OUT_DIR=/Users/lucastorri/tmp/data
LOG_FILE=/Users/lucastorri/tmp/logs/$SCRIPT.log
PID_FILE=/Users/lucastorri/tmp/pids/$SCRIPT.pid

case $1 in
    start)
        ps -p `cat $PID_FILE`
        if [ $? -ne 0 ]
        then
            exec 2>&1 $JAVA_HOME/bin/java -cp $CLASSPATH $MAIN $BOT_NAME $IRC_SERVER $IRC_CHANNEL $OUT_DIR 1> $LOG_FILE &
            echo $! > $PID_FILE;
        fi
        ;;
    stop)
        kill `cat $PID_FILE` ;;
    *)
        echo "usage: $SCRIPT {start|stop}" ;;
esac

exit 0

#!/bin/bash

# Variables that must be set:
  # JAVA_HOME
  # SCRIPT
  # BOT_NAME
  # IRCLOG_DATA_DIR
  # IRCLOG_LOGS_DIR
  # IRCLOG_PIDS_DIR
  # IRC_SERVER
  # IRC_CHANNELS

if [ -a env.sh ]
then
  	source env.sh
fi

  

MAIN=co.torri.ircbot.IRCBot
CLASSPATH=`dirname "$0"`/../lib/ircbot.jar
LOG_FILE=$IRCLOG_LOGS_DIR/$SCRIPT.log
PID_FILE=$IRCLOG_PIDS_DIR/$SCRIPT.pid

case $1 in
    start)
        ps -p `cat $PID_FILE`
        if [ $? -ne 0 ]
        then
            exec 2>&1 $JAVA_HOME/bin/java -cp $CLASSPATH $MAIN "$BOT_NAME" "$IRC_SERVER" "$IRC_CHANNELS" "$IRCLOG_DATA_DIR" 1> $LOG_FILE &
            echo $! > $PID_FILE;
        fi
        ;;
    stop)
        kill `cat $PID_FILE` ;;
    *)
        echo "usage: $SCRIPT {start|stop}" ;;
esac

exit 0

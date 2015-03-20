#!/bin/sh

SERVICE_NAME=imeji
BIN_DIR=$(dirname $(readlink -f $0))
export LANG=en_US.UTF-8

ShowLog ()
{
	tail -f $BIN_DIR/../logs/catalina.out
}

ShowLess ()
{
	less $BIN_DIR/../logs/catalina.out
}
ShowStatus ()
{
	ps -ef | grep $BIN_DIR | grep -v grep
}


case "$1" in
  start)
	echo "Starting $SERVICE_NAME..."
	sh $BIN_DIR/startup.sh
    ;;

  start-remote-debug)
	echo "Starting $SERVICE_NAME..."
	sh $BIN_DIR/catalina.sh jpda start
	ShowStatus
    ;;

   stop)
	echo "Shutting down $SERVICE_NAME..."
	sh $BIN_DIR/shutdown.sh
	sleep 5
	ShowStatus
    ;;

   kill)
	echo -n "Killing $SERVICE_NAME..."
	pkill -f ${BIN_DIR#/bin}
	sleep 2
	ShowStatus
    ;;


   restart)
	$0 stop
	sleep 5
	$0 start
    ;;

   log)
	ShowLog
    ;;

   less)
	ShowLess
    ;;

    status)
	ShowStatus
    ;;

    clean)
	rm -rf $BIN_DIR/../temp/*
	rm -rf $BIN_DIR/../work/*
    ;;
    *)
        echo "Usage: $0 {start|start-remote-debug|stop|kill|restart|log|status|less|clean}"

        exit 1
    ;;
esac

#!/bin/bash

SERVICE_NAME=imeji
BIN_DIR=$(dirname $(readlink -f $0))
BACKUP_ROOT_DIR=___PATH_TO_BACKUP_ROOT_DIR___
#see imeji.tdb.path in imeji.properties
IMEJI_TDB_DIR=___PATH_TO_IMEJI_TDB_DIR___
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

CheckStatus ()
{
	if [ -n "$(ShowStatus)" ]; then
	    echo "Please stop $SERVICE_NAME first"
	    exit 1
	fi
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

   backup)
	echo "Backup $SERVICE_NAME..."
	CheckStatus
	if [ -z $2 ]; then
	    echo "Please define backup directory in backup root $BACKUP_ROOT_DIR"
	    $0 *
	    exit 1
	fi
	pushd $BACKUP_ROOT_DIR >/dev/null

	#backup tdb files
	mkdir -p $2/tdb
	if [ $? -ne 0 ]; then
	    echo "Cannot create $BACKUP_ROOT_DIR/$2/tdb"
	    popd >/dev/null
	    exit 1
	fi
	cp -a $IMEJI_TDB_DIR/* $2/tdb
	if [ $? -eq 0 ]; then
	    echo "TDB files have been backuped in $BACKUP_ROOT_DIR/$2/tdb"
	fi
	#backup war file
	WAR="$BIN_DIR/../webapps/imeji.war"
	if [ ! -f "$WAR" ]; then
	    echo "Cannot find WAR file: $WAR"
	    popd >/dev/null
	    exit 1
	fi
	cp -a $WAR $2
	if [ $? -eq 0 ]; then
	    echo "WAR file has been backuped in $BACKUP_ROOT_DIR/$2"
	fi
	popd >/dev/null
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
	CheckStatus
	rm -rf $BIN_DIR/../temp/*
	rm -rf $BIN_DIR/../work/*
	rm -rf $BIN_DIR/../webapps/$SERVICE_NAME
    ;;

    usage)
        echo "Usage: $0 {start|start-remote-debug|stop|kill|restart|log|status|less|clean|backup <tag>}"
    ;;

    *)
        $0 usage
        exit 1
    ;;
esac
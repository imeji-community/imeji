#!/bin/bash

SERVICE_NAME=imeji
#context path in webapps
CONTEXT_PATH=___CONTEXT_PATH___
BIN_DIR=$(dirname $(readlink -f $0))
BACKUP_ROOT_DIR=___PATH_TO_BACKUP_ROOT_DIR___
#see imeji.tdb.path in imeji.properties
IMEJI_TDB_DIR=___PATH_TO_IMEJI_TDB_DIR___
#!!!!!!!!!!!!!!!!!!
#CATALINA_PID should point here to the same file as in setenv.sh
CATALINA_PID=$BIN_DIR/../logs/tomcat.pid
#!!!!!!!!!!!!!!!!!!
export LANG=en_US.UTF-8
#!!!!!!!!!!!!!!!!!!
#please add -Dimeji.is.running=true to the JAVA_OPTS in setenv.sh
#!!!!!!!!!!!!!!!!!!

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
	#ps -ef | grep $BIN_DIR | grep -v grep
    if [ -f "$CATALINA_PID" ]; then
        if [ -r "$CATALINA_PID" ]; then
          PID=`cat "$CATALINA_PID"`
          ps -p $PID >/dev/null 2>&1
          if [ $? -eq 0 ] ; then
            echo "Imeji is running with PID $PID."
          else
              ShowImejiProzessStatus
         fi
        else
           echo "Unable to read PID file. Start aborted."
           exit 1
        fi
    else
        ShowImejiProzessStatus
    fi
}
CheckImejiProzess()
{
    PID=`pgrep -f imeji.is.running`
    if [ -n "$PID" ]; then
        echo $PID > "$CATALINA_PID"
    elif [ -f "$CATALINA_PID" ]; then
        echo "Clean up $CATALINA_PID..."
        rm $CATALINA_PID
    fi
}
ShowImejiProzessStatus ()
{
    CheckImejiProzess
    if [ -z "$PID" ]; then
        echo "Imeji is not running."
    else
        echo "Imeji is running with PID $PID."
    fi
}
TryToStopImejiGracefully ()
{
    CheckImejiProzess
    if [ -n "$PID"  ]; then
        echo "Trying to stop $SERVICE_NAME ..."
        sh $BIN_DIR/catalina.sh stop 5 -force
        CheckImejiProzess
        if [ -n "$PID"  ]; then
            echo "Cannot stop $SERVICE_NAME. exit 1"
            exit 1
        fi
    fi
}

case "$1" in
    start)
        echo "Starting $SERVICE_NAME..."
        sh $BIN_DIR/startup.sh
        ShowStatus
        if [ "$2" = "log" ]; then
            $0 log
        fi
    ;;

    start-remote-debug)
        echo "Starting $SERVICE_NAME..."
        sh $BIN_DIR/catalina.sh jpda start
        ShowStatus
    ;;

    stop)
        echo "Shutting down $SERVICE_NAME..."
        sh $BIN_DIR/shutdown.sh
        ShowStatus
    ;;

    kill)
        echo "Killing $SERVICE_NAME..."
        CheckImejiProzess
        if [ -n "$PID" ]; then
            kill -9 $PID
            if [ $? -ne 0 ]; then
                echo "Cannot kill $SERVICE_NAME. exit 1"
                exit 1
            fi
        fi
        ShowStatus
    ;;

    backup)
        echo "Backup $SERVICE_NAME..."
        TryToStopImejiGracefully
        if [ -z $2 ]; then
            echo "Please define backup directory in backup root $BACKUP_ROOT_DIR "
            exit 1
        fi
        pushd $BACKUP_ROOT_DIR >/dev/null
        mkdir -p $2
        if [ $? -ne 0 ]; then
            echo "Cannot create $BACKUP_ROOT_DIR/$2"
            popd >/dev/null
            exit 1
        fi
        cp -a $IMEJI_TDB_PATH/* $2
        if [ $? -eq 0 ]; then
            echo "TDB files have been backuped in $BACKUP_ROOT_DIR/$2"
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
        echo "Clean up $SERVICE_NAME..."
        TryToStopImejiGracefully
        rm -rf $BIN_DIR/../temp/*
        rm -rf $BIN_DIR/../work/*
        rm -rf $BIN_DIR/../webapps/$CONTEXT_PATH
        echo "$SERVICE_NAME is cleaned up successfully"
    ;;

    *)
        echo "Usage: $0 {start|start-remote-debug|stop|kill|restart|log|status|less|clean|backup <tag>}"
        exit 1
    ;;
esac
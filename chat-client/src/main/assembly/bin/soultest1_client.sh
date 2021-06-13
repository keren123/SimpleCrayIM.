#!/bin/bash

#服务参数

PRO_NAME="chat-client-1.0-SNAPSHOT"
JAR_NAME="${PRO_NAME}.jar"
WORK_PATH="/work/${PRO_NAME}"
SERVER_PORT_COUNT=100
export log_chart_server_application_path=/work/logs/${PRO_NAME}

MAIN_CLASS="com.crazymakercircle.imServer.server.ServerApplication"
#JVM="-server -Xms1024m -Xmx2G"
JVM="-server -Xms4G -Xmx8G"
options=" soultest1 100 1000000 cdh1 "
##查看系统可以用的内存  free -h

REMOTE_CONFIG="-Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=n"

echo "PORT:$SERVER_PORT"
echo "JVM:$JVM"


RETVAL="0"

# See how we were called.
function start() {
    if [ ! -f ${LOG} ]; then
        touch ${LOG}
    fi
       #  nohup java ${JVM}   -jar ${WORK_PATH}/lib/${JAR_NAME} ${MAIN_CLASS} ${SERVER_PORT_COUNT} &
      nohup  java ${JVM}   -jar ${WORK_PATH}/lib/${JAR_NAME}  ${options}  &
#     java   -jar /work/chat-server-1.0-SNAPSHOT/lib/chat-server-1.0-SNAPSHOT.jar  soultest1 10
 #     java -server -Xms4G -Xmx8G  -jar /work/chat-server-1.0-SNAPSHOT/lib/chat-server-1.0-SNAPSHOT.jar   soultest1 10
    status
}

function stop() {
    pid=$(ps -ef | grep -v 'grep' | egrep $JAR_NAME| awk '{printf $2 " "}')
    if [ "$pid" != "" ]; then
        echo -n $"Shutting down boot: "
        kill -9 "$pid"
    else
        echo "${JAR_NAME} is stopped"
    fi
    status
}

function debug() {
    echo " start remote debug mode .........."
    if [ ! -f ${LOG} ]; then
        touch ${LOG}
    fi
      #  nohup java ${JVM}  ${REMOTE_CONFIG}  -jar ${WORK_PATH}/lib/${JAR_NAME} ${MAIN_CLASS} >> ${LOG} 2>&1 &
        nohup java ${JVM}   -jar ${WORK_PATH}/lib/${JAR_NAME} ${MAIN_CLASS}   &
}

function status(){
    pid=$(ps -ef | grep -v 'grep' | egrep $JAR_NAME| awk '{printf $2 " "}')
    #echo "$pid"
    if [ "$pid" != "" ]; then
        echo "${JAR_NAME} is running,pid is $pid"
    else
        echo "${JAR_NAME} is stopped"
    fi
}

function usage(){
    echo "Usage: $0 {start|debug|stop|restart|status}"
    RETVAL="2"
}

# See how we were called.
case "$1" in
    start)
        start
    ;;
    debug)
        debug
    ;;
    stop)
        stop
    ;;
    restart)
        stop
    	start
    ;;
    status)
        status
    ;;
    *)
        usage
    ;;
esac

exit ${RETVAL}
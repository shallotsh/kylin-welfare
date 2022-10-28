#!/bin/bash
# deploy the website from github.com using maven

NOW_DATE=`date +%Y%m%d%H%M`
branch=master
workplace=${HOME}/workplace
project_dir=${workplace}/kylin-welfare
app_dir=${workplace}/apps
app_name=kylin-welfare.jar
port=8080
LOG_PATH=/var/attachment/logs

jvm_args="-server
	 -XX:+UnlockDiagnosticVMOptions
   -XX:+UnlockExperimentalVMOptions
   -XX:-OmitStackTraceInFastThrow
   -Xlog:gc*=debug:file=${LOG_PATH}/gc%t.log:utctime,level,tags:filecount=50,filesize=100M
   -Xlog:jit+compilation=info:file=${LOG_PATH}/jit_compile%t.log:utctime,level,tags:filecount=10,filesize=10M
   -Xlog:safepoint=debug:file=${LOG_PATH}/safepoint%t.log:utctime,level,tags:filecount=10,filesize=10M
   -Dfile.encoding=UTF-8
   -Djava.security.egd=file:/dev/./urandom
   -Dnetworkaddress.cache.ttl=10 -Xms2048m -Xmx2048m -Xmn1280m -Xss512k
   -XX:MaxDirectMemorySize=1024m
   -XX:MetaspaceSize=384m
    -XX:ReservedCodeCacheSize=256m
    -XX:+DisableExplicitGC
    -XX:MaxGCPauseMillis=50
    -XX:-UseBiasedLocking
    -XX:GuaranteedSafepointInterval=0
    -XX:+UseCountedLoopSafepoints
    -XX:StartFlightRecording=disk=true,maxsize=4096m,maxage=3d
    -XX:FlightRecorderOptions=maxchunksize=128m --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/java.math=ALL-UNNAMED --add-opens java.base/java.net=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.security=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/jdk.internal.access=ALL-UNNAMED --add-opens java.base/jdk.internal.misc=ALL-UNNAMED
"
#if [ x$1 != x ];then
#	branch=$1
#fi
#
#if [ ! -d $workplace ]; then
#	  mkdir -p $workplace
#fi
#
#if [ ! -d $project_dir ]; then
#	cd $workplace
#	git clone git@github.com:shallotsh/kylin-welfare.git
#fi
#
if [ ! -d $app_dir ]; then
	mkdir -p $app_dir
fi

cd ${project_dir}

git fetch
echo "check out branch: "${branch}
git co ${branch}

echo "pull the latest branch code:"${branch}
git pull origin ${branch}

mvn clean package -DskipTests

cd target

oldProcess=`lsof -i:${port}|awk '{if (NR>1){print $2}}'`
if [ -n "$oldProcess" ]; then
	echo "Kill old process:"${oldProcess}
	kill -9 ${oldProcess}
fi

echo "update application jar..."
rm ${app_dir}/${app_name}
mv ${app_name} ${app_dir}/${app_name}

echo "ready to start application..."

cd ${app_dir}

if [ x$2 != x ];then
	echo "run as remote debug mode.."
	java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=12580 ${jvm_args} ${app_name} &
else
	java -jar ${jvm_args} ${app_name} --server.port=${port} &
fi

echo "complete."
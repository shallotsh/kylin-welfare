#!/bin/bash
# deploy the website from github.com using maven

NOW_DATE=`date +%Y%m%d%H%M`
branch=master
workplace=${HOME}/workplace
project_dir=${workplace}/kylin-welfare
app_dir=${workplace}/apps
app_name=kylin-welfare.jar
port=8080

jvm_args="-server
	-XX:SurvivorRatio=8
	-XX:NewRatio=4
	-XX:MetaspaceSize=128m
	-XX:MaxMetaspaceSize=128m
	-XX:+PrintGCDetails
	-XX:+PrintGCTimeStamps
	-XX:+PrintGCDateStamps
	-XX:+PrintTenuringDistribution
	-XX:+PrintGCApplicationStoppedTime
	-XX:+PrintGCApplicationConcurrentTime
	-Xloggc:${app_dir}/logs/${app_name}.gc.log.${NOW_DATE}"

if [ x$1 != x ];then
	branch=$1
fi

if [ ! -d $workplace ]; then
	  mkdir -p $workplace
fi

if [ ! -d $project_dir ]; then
	cd $workplace
	git clone git@github.com:shallotsh/kylin-welfare.git
fi

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
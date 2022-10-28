#!/bin/bash
# deploy the website from github.com using maven

NOW_DATE=`date +%Y%m%d%H%M`
branch=master
workplace=${HOME}/workplace
project_dir=${workplace}/kylin-welfare
deploy_dir=${project_dir}/deploy

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

cd ${project_dir}

git fetch
echo "check out branch: "${branch}
git co ${branch}

echo "pull the latest branch code:"${branch}
git pull origin ${branch}

mvn package docker:build -DskipTests

cd ${deploy_dir}

docker-compose up --force-recreate -d

echo "complete."
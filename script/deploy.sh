#!/bin/bash

# 기존 Java 프로세스 중지
echo "Stopping existing Java process..."
existing_pid=$(ps -ef | grep "SaveTime-0.0.1-SNAPSHOT.jar" | grep -v grep | awk '{print $2}')
if [ -n "$existing_pid" ]; then
    echo "Killing existing process with PID: $existing_pid"
    kill -9 $existing_pid
    echo "Existing Java process stopped."
else
    echo "No existing Java process found."
fi

# 최신 변경 사항을 가져옴
echo "Fetching latest changes from Git repository..."
cd /home/ubuntu/SaveTime
git pull origin master
echo "Git pull complete."

# 변경된 어플리케이션 빌드
echo "Building the application..."
./gradlew clean bootjar
echo "Build complete."

# 새로운 프로젝트 실행
echo "Starting the new Java process..."
cd /home/ubuntu/SaveTime/build/libs
nohup java -Dspring.profiles.active=dev -jar SaveTime-0.0.1-SNAPSHOT.jar > /dev/null 2>&1 &
echo "New Java process started."

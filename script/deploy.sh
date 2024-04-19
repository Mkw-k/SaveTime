#현재 위치 확인
pwd
# Git 저장소로 이동
cd /home/ubuntu/SaveTime
echo "위치 이동>>>>"
pwd

# 최신 변경 사항을 가져옴
git pull origin master
echo ">>>Git Pull Activate!!"

# 변경된 어플리케이션 빌드
./gradlew clean bootjar
echo ">>>clean build Activate"

# Spring Boot 어플리케이션 실행
nohup java -Dspring.profiles.active=dev -jar build/libs/SaveTime-0.0.1-SNAPSHOT.jar &
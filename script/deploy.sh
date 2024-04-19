# Git 저장소로 이동
cd /home/ubuntu/SaveTime

# 최신 변경 사항을 가져옴
git pull origin master

# 변경된 어플리케이션 빌드
./gradlew clean build

# Spring Boot 어플리케이션 실행
java -Dspring.profiles.active=dev -jar build/libs/SaveTime-0.0.1-SNAPSHOT.jar
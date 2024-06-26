# JDK11 이미지 사용
FROM adoptopenjdk/openjdk11

#VOLUME /tmp

CMD ["./gradlew", "clean", "bootjar"]

# JAR_FILE 변수에 값을 저장
#ARG JAR_FILE_PATH=build/libs/*.jar

# 변수에 저장된 것을 컨테이너 실행시 이름을 app.jar파일로 변경하여 컨테이너에 저장
COPY build/libs/SaveTime-0.0.1-SNAPSHOT.jar SaveTime-0.0.1-SNAPSHOT.jar

# 빌드된 이미지가 run될 때 실행할 명령어
ENTRYPOINT ["java","-Dspring.profiles.active=dev","-jar","SaveTime-0.0.1-SNAPSHOT.jar"]


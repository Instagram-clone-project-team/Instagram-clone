# FROM openjdk:11-jdk
# LABEL maintainer="gjdbs2597@gmail.com"
# ARG JAR_FILE=build/libs/Instagram-0.0.1-SNAPSHOT.jar
# ADD ${JAR_FILE} instagram.jar
# ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/instagram.jar"]


FROM openjdk:11-jdk AS builder
COPY . .
#RUN chmod +x ./gradlew
#RUN ./gradlew clean
#RUN ./gradlew bootJar

FROM openjdk:11-jdk
COPY --from=builder build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]



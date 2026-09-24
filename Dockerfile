########Maven build########
FROM maven:3.8.3-openjdk-17 AS build

WORKDIR /home/app
COPY pom.xml ./

RUN mvn -B dependency:go-offline

COPY src ./src

RUN mvn -B clean package -Dmaven.test.skip

########Run########
FROM amazoncorretto:17

COPY --from=build /home/app/target/*.jar app.jar

EXPOSE 9170

USER 1000:1000

ENTRYPOINT ["java", "-jar", "/app.jar"]

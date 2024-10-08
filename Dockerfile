########Maven build ########
FROM maven:3.8.3-openjdk-17 AS build

# we /app as a default working directory
WORKDIR /home/app
#copy source
COPY src ./src
#copy pom
COPY pom.xml ./

COPY configure_environment.sh ./


# Set executable permissions for the script
RUN chmod +x configure_environment.sh

# Run the configuration script to update application.properties
RUN ./configure_environment.sh

## Industry Portal Future Updates Part ## 
######## Uncomment ###########
# COPY config/settings.xml /usr/share/maven/conf/settings.xml
######## Uncomment ###########

#resolve maven dependencies
RUN mvn -f /home/app/pom.xml clean package -Dmaven.test.skip

########Run Project########
# we use a light-weight base image with JAVA17
FROM openjdk:17-alpine

ARG JAR_FILE=target/*.jar

ARG JAVA_OPTS

COPY --from=build /home/app/${JAR_FILE} app.jar

EXPOSE 9170

#ENTRYPOINT ["java", "-Xmx4g", "-Xms4g", "-jar", "/app.jar"]  //Expected_update { fix memory issues due to limited heap space.}
#ENTRYPOINT ["java","-jar","/app.jar"]
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -XX:+PrintFlagsFinal -jar /app.jar"]

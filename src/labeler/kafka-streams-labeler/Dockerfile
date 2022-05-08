FROM openjdk:11-ea-11-jre-slim

ADD build/libs/*.jar /opt/labeler.jar
ADD src/main/resources src/main/resources

ENTRYPOINT ["java", "-jar", "/opt/labeler.jar", "/src/main/resources/application.properties"]

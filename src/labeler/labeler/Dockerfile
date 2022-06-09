FROM openjdk:11

ADD build/libs/*.jar /opt/labeler.jar
# ADD src/main/resources src/main/resources
# "/src/main/resources/application.properties"
ENTRYPOINT ["java", "-jar", "/opt/labeler.jar"]
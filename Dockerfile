FROM openjdk:8-jdk-alpine
EXPOSE 8083
ADD /target/timesheet-1.1-RELEASE.jar timesheet.jar
ENTRYPOINT ["java","-jar","timesheet.jar"]
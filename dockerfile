FROM java:8
EXPOSE 8083
ADD target/timesheet-1.0.1-SNAPSHOT.jar timesheet-1.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/timesheet-1.0.1-SNAPSHOT.jar"]
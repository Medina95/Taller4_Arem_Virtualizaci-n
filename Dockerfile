FROM openjdk:17
ENV PORT=6000
EXPOSE 6000
ENTRYPOINT ["java","-jar","springboot-docker-compose.jar"]
WORKDIR /usrapp/bin
COPY /target/classes /usrapp/bin/classes
COPY /target/dependency /usrapp/bin/dependency
ADD target/springboot-docker-compose.jar /usrapp/bin/springboot-docker-compose.jar

CMD ["java","-cp","./classes:./dependency/*","org.example.RestServiceApplication"]
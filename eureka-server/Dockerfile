# Esta linea sirve por si la imagen se genera desde una MacOS la imagen se genera en arm64 y no amd64
# FROM --platform=linux/amd64 amazoncorretto:21-alpine-jdk
FROM amazoncorretto:21-alpine-jdk

WORKDIR /app
EXPOSE 8761
ADD ./target/eureka-server-0.0.1-SNAPSHOT.jar eureka-server.jar

ENTRYPOINT [ "java", "-jar", "eureka-server.jar" ]
FROM amazoncorretto:21-alpine-jdk

# El expose del puerto no se pone porque sera dinamico...
WORKDIR /app
ADD ./target/msvc-products-0.0.1-SNAPSHOT.jar msvc-products.jar

ENTRYPOINT [ "java", "-jar", "msvc-products.jar" ]
FROM maven:3.9-eclipse-temurin-25 AS maven

FROM container-registry.oracle.com/java/openjdk:27 AS build
COPY --from=maven /usr/share/maven /usr/share/maven
ENV PATH="/usr/share/maven/bin:${PATH}"
WORKDIR /app
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src ./src
RUN mvn -q -B verify

FROM container-registry.oracle.com/java/openjdk:27
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

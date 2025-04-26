FROM maven:3.9.9-amazoncorretto-21 AS builder

WORKDIR /app
COPY pom.xml ./
RUN mvn dependency:resolve

COPY src ./src

RUN mvn package -DskipTests

FROM openjdk:21-jdk
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]
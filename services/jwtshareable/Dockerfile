# Build jwtshareable
FROM maven:3.9.9-eclipse-temurin-21 AS jwt-build
WORKDIR /jwtshareable
COPY jwtshareable/pom.xml .
COPY jwtshareable/src ./src
RUN mvn clean install

# Build gateway service
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
COPY --from=jwt-build /root/.m2 /root/.m2   # bring the built jwtshareable into local repo
RUN mvn clean package -DskipTests

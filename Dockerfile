# Build stage
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests -Dcheckstyle.skip=true -Dpmd.skip=true -B

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="findme-team@findme.com"
LABEL description="FindMe Backend Spring Boot Application"

# Patch les paquets Alpine de l'image de base : une partie des CVE HIGH/CRITICAL
# remontees par Trivy sont deja corrigees en amont mais pas encore presentes
# dans l'image figee ; apk upgrade les recupere au moment du build.
RUN apk update && apk upgrade --no-cache

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Cree le dossier de logs et donne la main a l'utilisateur non-root AVANT de
# basculer USER (sinon /app/logs appartient a root et l'ecriture echoue).
RUN mkdir -p /app/logs && chown -R spring:spring /app

COPY --from=build /app/target/findme-backend-*.jar app.jar

USER spring:spring

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-XX:InitialRAMPercentage=50.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
    "-jar", \
    "app.jar"]

# Stage 1: Build (Construction)
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# On saute les tests pour accélérer le build de l'image (ils sont faits avant en CI)
RUN mvn clean package -DskipTests

# Stage 2: Runtime (Exécution)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Utilisation d'un wildcard précis pour éviter les erreurs
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Syntaxe correcte en tableau JSON
ENTRYPOINT ["java", "-jar", "app.jar"]
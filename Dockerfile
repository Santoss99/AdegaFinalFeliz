# Estágio 1: Build
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
# Entramos na pasta 'gestao' onde o seu projeto realmente vive
WORKDIR /gestao
RUN mvn clean package -DskipTests

# Estágio 2: Run
FROM eclipse-temurin:17-jre
# Buscamos o jar dentro da pasta target que está dentro de gestao
COPY --from=build /gestao/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
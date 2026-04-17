# Estágio 1: Build (Compilação)
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
# Rodamos o Maven direto na raiz, onde o pom.xml está
RUN mvn clean package -DskipTests

# Estágio 2: Run (Execução)
FROM eclipse-temurin:17-jre
# Copiamos o jar que o Maven acabou de criar na pasta target
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
# Estágio 1: Build (Compilação)
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
# Garante que o Maven gere o arquivo na raiz do container
RUN mvn clean package -DskipTests

# Estágio 2: Run (Execução)
FROM eclipse-temurin:17-jre
# Criamos uma pasta limpa para rodar o sistema
WORKDIR /app
# Buscamos o arquivo .jar dentro de target e renomeamos para app.jar aqui dentro
COPY --from=build /target/*.jar /app/app.jar
EXPOSE 8080
# Rodamos apontando o caminho completo
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
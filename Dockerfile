# Estágio 1: Compilação
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Estágio 2: Execução
FROM eclipse-temurin:17-jre
WORKDIR /app

# De acordo com seu POM, o arquivo sai na raiz com o nome gestao.jar
COPY --from=build /gestao.jar app.jar
# Precisamos da pasta lib que seu maven-dependency-plugin cria
COPY --from=build /lib ./lib

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
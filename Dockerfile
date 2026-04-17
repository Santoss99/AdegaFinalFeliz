# Estágio 1: Build
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
# Forçamos o Maven a compilar e listar o conteúdo para garantir que o jar existe
RUN mvn clean package -DskipTests

# Estágio 2: Run
FROM eclipse-temurin:17-jre
# Usamos um comando de busca para copiar qualquer .jar que esteja dentro de target
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
# Comando para rodar
ENTRYPOINT ["java", "-jar", "app.jar"]
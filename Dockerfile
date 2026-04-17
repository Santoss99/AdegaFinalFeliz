# Estágio 1: Build
FROM maven:3.8.5-openjdk-17 AS build
COPY . .

# Como o pom.xml está na raiz, rodamos o comando aqui mesmo.
# O Maven vai usar o <sourceDirectory>src</sourceDirectory> que você definiu no POM
RUN mvn clean package -DskipTests

# Estágio 2: Execução
FROM eclipse-temurin:17-jre
WORKDIR /app

# 1. O seu POM joga o jar na raiz do projeto durante o build
COPY --from=build /gestao.jar app.jar

# 2. O seu POM joga as dependências na pasta /lib da raiz
COPY --from=build /lib ./lib

# 3. A pasta WEB está dentro de SistemaGestao (visto na imagem)
COPY --from=build /SistemaGestao/web ./web

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar", "web"]
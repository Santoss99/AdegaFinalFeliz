# Estágio 1: Compilação
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Estágio 2: Execução
FROM eclipse-temurin:17-jre
WORKDIR /app

# 1. Copia o executável (conforme seu pom.xml)
COPY --from=build /gestao.jar app.jar

# 2. Copia as dependências
COPY --from=build /lib ./lib

# 3. COPIA A PASTA DO SEU SITE (HTML/CSS/JS)
# Se sua pasta se chamar "site" ou "public", mude o nome aqui:
COPY --from=build /web ./web

EXPOSE 8080

# Inicia em modo web
ENTRYPOINT ["java", "-jar", "app.jar", "web"]
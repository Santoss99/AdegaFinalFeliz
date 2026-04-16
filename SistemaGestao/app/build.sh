#!/bin/bash
# ============================================================
# Script de build - Sistema de Gestão
# Requer: JDK 11+ instalado
# ============================================================

set -e

GSON_URL="https://repo1.maven.org/maven2/com/google/code/gson/gson/2.10.1/gson-2.10.1.jar"
GSON_JAR="lib/gson-2.10.1.jar"

echo "================================================"
echo "  BUILD - Sistema de Gestão"
echo "================================================"

# 1. Criar pasta lib se não existir
mkdir -p lib dados web

# 2. Baixar Gson se necessário
if [ ! -f "$GSON_JAR" ]; then
    echo "[1/4] Baixando Gson..."
    if command -v curl &> /dev/null; then
        curl -L -o "$GSON_JAR" "$GSON_URL"
    elif command -v wget &> /dev/null; then
        wget -O "$GSON_JAR" "$GSON_URL"
    else
        echo "ERRO: curl ou wget não encontrado. Baixe manualmente:"
        echo "  $GSON_URL"
        echo "  e salve em: $GSON_JAR"
        exit 1
    fi
    echo "  Gson baixado."
else
    echo "[1/4] Gson já disponível."
fi

# 3. Criar pasta de classes
mkdir -p out

# 4. Compilar
echo "[2/4] Compilando..."
find src -name "*.java" > sources.txt
javac -encoding UTF-8 -cp "$GSON_JAR" -d out @sources.txt
rm sources.txt
echo "  Compilação concluída."

# 5. Empacotar JAR
echo "[3/4] Empacotando JAR..."
cp -r web out/
cd out
# Extrair Gson dentro do jar
jar xf "../$GSON_JAR"
META_INF_DIR="META-INF"

cd ..
jar cfe gestao.jar Main -C out .
echo "  JAR criado: gestao.jar"

echo "[4/4] Build concluído!"
echo ""
echo "Para executar:"
echo "  java -jar gestao.jar"
echo ""
echo "Ou via menu -> opção 7 para iniciar interface web em:"
echo "  http://localhost:8080"
echo "================================================"

#!/bin/bash
# Script de execução - Sistema de Gestão
# Execute após o build.sh

if [ ! -f "gestao.jar" ]; then
    echo "JAR não encontrado. Execute ./build.sh primeiro."
    exit 1
fi

echo "Iniciando Sistema de Gestão..."
java -jar gestao.jar

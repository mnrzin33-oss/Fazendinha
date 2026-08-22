#!/bin/bash

echo "=== Setup Fazendinha ==="

# Baixar gradle-wrapper.jar se não existir
WRAPPER_JAR="gradle/wrapper/gradle-wrapper.jar"
WRAPPER_URL="https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.jar"

if [ ! -f "$WRAPPER_JAR" ]; then
    echo "Baixando gradle-wrapper.jar..."
    curl -sL "$WRAPPER_URL" -o "$WRAPPER_JAR"
    if [ $? -eq 0 ]; then
        echo "gradle-wrapper.jar baixado com sucesso!"
    else
        echo "ERRO: Não foi possível baixar gradle-wrapper.jar"
        echo "Alternativa: instale o Gradle e rode: gradle wrapper"
        exit 1
    fi
fi

chmod +x gradlew

echo "Setup concluído!"
echo ""
echo "Para buildar o APK:"
echo "  ./gradlew android:assembleDebug"
echo ""
echo "Para buildar via GitHub Actions, faça push para o repositório."

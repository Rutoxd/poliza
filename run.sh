#!/usr/bin/env bash
# Compila y ejecuta el Sistema de Gestión de Pólizas (Linux/macOS)
set -e
cd "$(dirname "$0")"
rm -rf bin && mkdir -p bin
javac --release 17 -encoding UTF-8 -d bin -cp "lib/*" $(find src -name "*.java")
java -cp "bin:lib/*" Polizas.vista.Inicio

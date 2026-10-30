#!/usr/bin/env bash
# Compila y ejecuta el Sistema de Gestión de Pólizas
set -e
cd "$(dirname "$0")"
rm -rf bin && mkdir -p bin
javac -d bin -cp "lib/*" $(find src -name "*.java")
java -cp "bin:lib/*" Polizas.Inicio

@echo off
REM Compila y ejecuta el Sistema de Gestion de Polizas (Windows)
cd /d "%~dp0"
if exist bin rmdir /s /q bin
mkdir bin
dir /s /b src\*.java > sources.txt
javac -d bin -cp "lib/*" @sources.txt
del sources.txt
java -cp "bin;lib/*" Polizas.Inicio

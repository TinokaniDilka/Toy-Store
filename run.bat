@echo off
cd /d "%~dp0"
if not defined JAVA_HOME set "JAVA_HOME=C:\Users\USER\.jdks\temurin-17.0.18"
echo Using Java: %JAVA_HOME%
echo Starting Online Toy Store on http://localhost:8080/
call mvnw.cmd jetty:run

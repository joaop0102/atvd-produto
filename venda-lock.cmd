@echo off
setlocal
set "ID=%~1"
set "NOME=%~2"

curl -s -w "\n%NOME% HTTP=%%{http_code}\n" -X POST "http://localhost:8080/produtos/%ID%/venda" -H "Content-Type: application/json" -d "{\"qtd\":1}" > "%~dp0%NOME%.txt"

endlocal

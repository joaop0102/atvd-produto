@echo off
setlocal

set "BASE=http://localhost:8080"
set "ID=1"

if not exist "%~dp0venda-lock.cmd" (
    echo ERRO: arquivo venda-lock.cmd nao encontrado.
    pause
    exit /b 1
)

echo ============================================
echo TESTE DO PESSIMISTIC_WRITE - VENDA CONCORRENTE
echo ============================================
echo.
echo Produto usado: ID %ID%
echo.

echo [1] Colocando o estoque do produto em 1...
curl -s -X PUT "%BASE%/produtos/%ID%/estoque" -H "Content-Type: application/json" -d "{\"qtd\":1}"
echo.
echo.

echo [2] Conferindo estoque inicial...
curl -s "%BASE%/produtos/%ID%/estoque"
echo.
echo.

if exist "%~dp0VENDA-1.txt" del /q "%~dp0VENDA-1.txt"
if exist "%~dp0VENDA-2.txt" del /q "%~dp0VENDA-2.txt"

echo [3] Disparando DUAS vendas ao mesmo tempo...
start "" /b "%~dp0venda-lock.cmd" %ID% VENDA-1
start "" /b "%~dp0venda-lock.cmd" %ID% VENDA-2

echo Aguardando as duas respostas...
:aguarda
if not exist "%~dp0VENDA-1.txt" goto espera
if not exist "%~dp0VENDA-2.txt" goto espera
goto pronto
:espera
ping 127.0.0.1 -n 2 >nul
goto aguarda

:pronto
echo.
echo [4] Respostas das vendas:
echo.
type "%~dp0VENDA-1.txt"
type "%~dp0VENDA-2.txt"

echo.
echo [5] Conferindo estoque final...
curl -s "%BASE%/produtos/%ID%/estoque"
echo.
echo.
echo RESULTADO ESPERADO:
echo - Uma venda retorna HTTP 200.
echo - A outra retorna HTTP 409 por estoque insuficiente.
echo - O estoque final fica em 0.
echo.
echo Observacao: deixe o console do Spring Boot aberto para visualizar a SQL gerada pela venda.
echo.
pause
endlocal

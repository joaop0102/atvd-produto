@echo off
setlocal
set "BASE=http://localhost:8080"
set "ID=1"

echo ============================================
echo TESTE DOS EVENTOS - ESTOQUE + RABBITMQ
echo ============================================
echo.
echo Este teste executa as operacoes que alteram o estoque:
echo 1 - PUT /produtos/1/estoque
echo 2 - POST /produtos/1/venda
echo.
echo Deixe o Spring Boot aberto e o RabbitMQ em execucao.
echo.

for /f "delims=" %%A in ('curl -s -o nul -w "%%{http_code}" "%BASE%/produtos/%ID%/estoque"') do set "STATUS=%%A"
if not "%STATUS%"=="200" (
echo ERRO: API nao respondeu HTTP 200.
echo Inicie o projeto com iniciar-projeto.cmd
echo.
pause
exit /b 1
)

echo [1] Atualizando estoque para 2...
curl -s -X PUT "%BASE%/produtos/%ID%/estoque" -H "Content-Type: application/json" -d "{\"qtd\":2}"
echo.
echo.

echo Evento esperado:
echo   operacao=ATUALIZACAO_ESTOQUE
echo.

echo [2] Realizando uma venda de 1 unidade...
curl -s -X POST "%BASE%/produtos/%ID%/venda" -H "Content-Type: application/json" -d "{\"qtd\":1}"
echo.
echo.
echo Evento esperado:
echo   operacao=VENDA
echo.

echo [3] Consultando estoque final...
curl -s "%BASE%/produtos/%ID%/estoque"
echo.
echo.

echo ============================================
echo TESTE CONCLUIDO
echo ============================================
echo.
echo Verifique no console do Spring Boot algo como:
echo   Evento de produto publicado no RabbitMQ
echo.
echo Para confirmar a mensagem no RabbitMQ, verifique a fila:
echo   produto.eventos
echo.
pause
endlocal

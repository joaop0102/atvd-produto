@echo off
setlocal
cd /d "%~dp0"

echo ============================================
echo EXECUTAR PROJETO + TESTE DE EVENTOS
echo ============================================
echo.

echo [1] Abrindo o Spring Boot em outra janela...
start "Spring Boot" "%ComSpec%" /k call "%~dp0iniciar-projeto.cmd"

echo [2] Aguardando a API ficar disponivel...
set "TENTATIVAS=0"
:aguarda
set /a TENTATIVAS+=1
for /f "delims=" %%A in ('curl -s -o nul -w "%%{http_code}" "http://localhost:8080/produtos/1/estoque"') do set "STATUS=%%A"
if "%STATUS%"=="200" goto pronto
if %TENTATIVAS% GEQ 60 goto timeout
ping 127.0.0.1 -n 2 >nul
goto aguarda

:pronto
echo API disponivel.
echo.
call "%~dp0testar-eventos.cmd"
goto fim

:timeout
echo.
echo ERRO: a API nao ficou disponivel em aproximadamente 60 segundos.
echo Verifique a janela do Spring Boot.
echo.
pause

:fim
endlocal

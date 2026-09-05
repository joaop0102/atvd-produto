# API de Estoque - Maven + Spring Boot + JPA

Projeto para a atividade de estoque, com entidade `Produto`, repository JPA, serviço de baixa de estoque e endpoints REST.

O projeto está preparado para gerar um único `.jar` e executar várias instâncias do mesmo arquivo em paralelo, sem Docker.

## Requisitos

- Java 21
- Maven 3.6.3 ou superior

## 1. Gerar o JAR

Na pasta que contém o `pom.xml`:

```cmd
mvn clean package
```

O arquivo será gerado em:

```text
target/estoque-api.jar
```

## 2. Executar uma instância

```cmd
java -jar target/estoque-api.jar
```

Por padrão, a aplicação usa a porta `8080`.

## 3. Executar várias instâncias em paralelo

Abra terminais diferentes.

Terminal 1:

```cmd
java -jar target/estoque-api.jar --server.port=8080
```

Terminal 2:

```cmd
java -jar target/estoque-api.jar --server.port=8081
```

Terminal 3:

```cmd
java -jar target/estoque-api.jar --server.port=8082
```

Também é possível usar a variável `PORT`:

```cmd
set PORT=8083
java -jar target/estoque-api.jar
```

Cada processo roda na sua própria porta, mas as instâncias compartilham o mesmo banco H2 em arquivo (`./data/estoquedb`).

## 4. Configuração

A configuração principal está em:

```text
src/main/resources/application.yaml
```

A porta está parametrizada assim:

```yaml
server:
  port: ${PORT:8080}
```

As dependências continuam sendo definidas no:

```text
pom.xml
```

## 5. Endpoints

### Consultar estoque

```http
GET http://localhost:8080/produtos/1/estoque
```

Outra instância:

```http
GET http://localhost:8081/produtos/1/estoque
```

### Realizar venda

```http
POST http://localhost:8080/produtos/1/venda
Content-Type: application/json

{
  "qtd": 2
}
```

A mesma venda pode ser enviada para outra instância:

```http
POST http://localhost:8081/produtos/1/venda
Content-Type: application/json

{
  "qtd": 2
}
```

A baixa foi implementada com atualização condicional no banco para evitar que duas instâncias vendam mais unidades do que existem em estoque.

## 6. H2 Console

Com uma instância em execução:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:file:./data/estoquedb
```

Usuário:

```text
sa
```

Senha: em branco.

## 7. Testes

```cmd
mvn test
```


## 8. Gerenciador de estoque

O gerenciador permite **somente atualizar a quantidade em estoque** de um produto.

### Atualizar quantidade do estoque

```http
PUT http://localhost:8080/produtos/1/estoque
Content-Type: application/json

{
  "qtd": 50
}
```

A resposta será o produto com a quantidade atualizada. A quantidade pode ser `0`, mas não pode ser negativa.

### Consultar a quantidade

```http
GET http://localhost:8080/produtos/1/estoque
```

> Observação: nesta atividade, o gerenciamento de estoque altera apenas o campo `qtd` do produto. O nome do produto não é alterado por esse endpoint.

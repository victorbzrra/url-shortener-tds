
# Desafio Backend Encurtador de URLs - TDS Company

## Descrição

Este projeto é uma API RESTful desenvolvida para o desafio técnico da TDS Company. A aplicação consiste em um serviço de encurtamento de URLs, permitindo cadastrar, acessar e consultar estatísticas de acesso das URLs encurtadas.

## Funcionalidades

- **Cadastrar URL**: Recebe uma URL original e retorna uma URL encurtada.
- **Acessar URL encurtada**: Redireciona para a URL original ao acessar o código encurtado.
- **Visualizar estatísticas**: Exibe total de acessos e média de acessos por dia.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.5.4
- Spring Data JPA
- PostgreSQL
- Maven
- Docker
- Swagger/OpenAPI (springdoc-openapi 2.8.9)
- JUnit 5 / Mockito

## Como executar

### Pré-requisitos

- Docker e Docker Compose instalados na máquina.
- (Opcional) Java 17 e Maven, caso deseje rodar localmente fora do container.

### Execução com Docker Compose

1. Clone o repositório:
   ```bash
   git clone https://github.com/victorbzrra/url-shortener-tds.git
   cd url-shortener-tds
   ```

2. Construa e inicie os containers:
   ```bash
   docker-compose up --build
   ```

3. A aplicação estará disponível em `http://localhost:8080`

4. O Swagger (documentação da API) estará disponível em:
   ```
   http://localhost:8080/swagger-ui.html
   ```

### Testes

Os testes automatizados são executados automaticamente durante o processo de build do Docker. Para rodar localmente:

```bash
./mvnw clean test
```

### Configurações (Docker Compose)

O serviço utiliza as seguintes credenciais para o banco de dados PostgreSQL:
- Database: tds
- User: tds
- Password: tds123

As configurações podem ser ajustadas no arquivo `docker-compose.yml`.

## Endpoints

- `POST /api/v1/urls` - Cria uma nova URL encurtada.
- `GET /api/v1/urls/{shortened}` - Redireciona para a URL original.
- `GET /api/v1/urls/{shortened}/stats` - Consulta estatísticas da URL encurtada.

A documentação completa está disponível no Swagger. O Swagger UI não segue redirecionamentos, então teste acessando a URL encurtada diretamente no navegador.

## Diferenciais implementados

- Documentação Swagger/OpenAPI
- Tratamento global de exceções
- Testes unitários com cobertura dos principais cenários

## Autor

Victor Bezerra  
[LinkedIn](https://www.linkedin.com/in/victor-bzrra)  
[GitHub](https://github.com/victorbzrra)

---

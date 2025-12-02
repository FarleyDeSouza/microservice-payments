# Payment Service

Microserviço responsável pelo processamento de pagamentos do e-commerce. Este serviço é parte do sistema de e-commerce da Clickbait e foi desenvolvido seguindo os princípios da Clean Architecture e implementa validações rigorosas para cada método de pagamento.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.2.0
- MongoDB
- OpenAPI (Swagger)
- JUnit 5
- Testcontainers
- Maven

## Pré-requisitos

- JDK 17 ou superior
- MongoDB
- Docker (para testes de integração)
- Maven 3.8+

## Como Executar

1. Clone o repositório:
```bash
git clone <repository-url>
```

2. Navegue até o diretório do projeto:
```bash
cd microservice-payments
```

3. Execute o projeto com Maven:
```bash
./mvnw spring-boot:run
```

O serviço estará disponível em `http://localhost:8080`

## Documentação da API

A documentação da API está disponível através do Swagger UI em:
```
http://localhost:8080/swagger-ui.html
```

### Endpoints Principais

1. **Criar Pagamento**
   - Método: POST
   - URL: `/api/v1/payments`
   - Descrição: Processa um novo pagamento

2. **Buscar Pagamento por ID**
   - Método: GET
   - URL: `/api/v1/payments/{id}`
   - Descrição: Retorna os detalhes de um pagamento específico

3. **Buscar Pagamento por Order ID**
   - Método: GET
   - URL: `/api/v1/payments/order/{orderId}`
   - Descrição: Retorna os detalhes do pagamento associado a um pedido

## Métodos de Pagamento Suportados

- Cartão de Crédito
- PIX
- Boleto Bancário

## Arquitetura

O projeto segue os princípios da Clean Architecture, com as seguintes camadas:

- **Domain**: Contém as entidades de negócio e regras de negócio centrais
- **Application**: Contém os casos de uso e regras de negócio específicas da aplicação
- **Infrastructure**: Contém a implementação de adaptadores (REST controllers, persistência, etc.)

## Testes

O projeto inclui:
- Testes unitários
- Testes de integração usando Testcontainers

Para executar os testes:
```bash
./mvnw test
```

## Status dos Endpoints

Todos os endpoints retornam as seguintes informações de status:

- `201 Created`: Quando um novo pagamento é criado com sucesso
- `200 OK`: Quando uma consulta é realizada com sucesso
- `400 Bad Request`: Quando os dados da requisição são inválidos
- `404 Not Found`: Quando um pagamento não é encontrado
- `500 Internal Server Error`: Em caso de erro interno do servidor

## Exemplos de Requisições

### Criar um Pagamento com Cartão de Crédito

```json
POST /api/v1/payments
{
  "orderId": "123",
  "amount": 100.00,
  "paymentMethod": "CREDIT_CARD",
  "paymentDetails": {
    "cardNumber": "4111111111111111",
    "cardHolderName": "John Doe",
    "expirationDate": "12/25",
    "cvv": "123"
  }
}
```

### Criar um Pagamento com PIX

```json
POST /api/v1/payments
{
  "orderId": "123",
  "amount": 100.00,
  "paymentMethod": "PIX",
  "paymentDetails": {
    "pixKey": "123e4567-e89b-12d3-a456-426614174000",
    "pixKeyType": "random"
  }
}
```

### Criar um Pagamento com Boleto

```json
POST /api/v1/payments
{
  "orderId": "123",
  "amount": 100.00,
  "paymentMethod": "BANK_SLIP",
  "paymentDetails": {
    "customerName": "John Doe",
    "customerDocument": "123.456.789-00",
    "dueDate": "2025-12-31T23:59:59"
  }
}
```
# To-Do — Lista de tarefas (backend)

Projeto de Laboratório de Desenvolvimento Multiplataforma — 6º DSM, Fatec Franca.
API REST em Java 17, Spring Boot, Spring Data JPA e PostgreSQL. Sem login e sem usuários.

## Requisitos atendidos

- Criar, listar, consultar, alterar e excluir tarefas.
- Nome, descrição, status, observações, data de criação e data de atualização.
- Scripts de criação do banco e da tabela em `database/`.
- Separação em modelo, DTO, repositório, serviço e controller.
- Testes unitários com Mockito e de integração HTTP/JPA com H2.
- Validação e respostas de erro com Problem Detail.

## Rodar com Docker

Instale e inicie o Docker Desktop. Dentro desta pasta:

```powershell
docker compose up --build -d
docker compose logs -f api
```

A API fica em http://localhost:8080/api/tarefas. O build executa os testes.
O PostgreSQL cria o banco e executa `database/01-schema.sql` no primeiro início.
As tarefas ficam no volume `todo-data` entre reinícios.
Para parar mantendo os dados: `docker compose down`.
As credenciais padrão são somente para desenvolvimento local.

## Rodar sem Docker

Pré-requisitos: JDK 17 ou superior, Maven 3.6.3 ou superior e PostgreSQL.

1. No pgAdmin ou psql, conectado a `postgres` como administrador, execute as instruções de
   `database/00-create-database.sql` fora de uma transação.
2. Conecte ao banco `todo`, com usuário `todo`, e execute `database/01-schema.sql`.
3. Dentro desta pasta, execute:

```powershell
mvn clean verify
mvn spring-boot:run
```

Configuração padrão: banco `todo`, usuário `todo`, senha `todo`, porta 5432.
Para alterar, defina as variáveis de ambiente antes de executar:

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/todo"
$env:DB_USER = "todo"
$env:DB_PASSWORD = "sua-senha"
$env:PORT = "8080"
```

O Hibernate valida o schema; não cria nem apaga tabelas automaticamente.
As datas são geradas pelo servidor e retornadas em UTC no formato ISO 8601.

## Endpoints

| Método | Rota | Resultado |
| --- | --- | --- |
| GET | /api/tarefas | 200, lista ordenada por id |
| GET | /api/tarefas/{id} | 200, tarefa |
| POST | /api/tarefas | 201, tarefa e cabeçalho Location |
| PUT | /api/tarefas/{id} | 200, tarefa atualizada |
| DELETE | /api/tarefas/{id} | 204, sem corpo |

POST e PUT recebem:

```json
{
  "nome": "Estudar Java",
  "descricao": "Revisar Spring Boot",
  "status": "PENDENTE",
  "observacoes": "Resolver exercícios"
}
```

Nome é obrigatório, não pode conter somente espaços e tem limite de 120 caracteres.
Status é obrigatório: `PENDENTE`, `EM_ANDAMENTO` ou `CONCLUIDA`.
Descrição e observações são opcionais, com limite de 2000 caracteres cada.
PUT substitui os campos editáveis; campos opcionais omitidos ficam nulos.
O id e as datas são controlados pelo servidor. A data de criação é preservada;
a data de atualização muda quando os dados são alterados.

Dados inválidos retornam 400. Uma tarefa inexistente retorna 404.
Uma lista vazia retorna `[]`. Não existe autenticação.

## Experimentar no PowerShell

```powershell
$corpo = @{
    nome = "Fazer trabalho"
    descricao = "Implementar CRUD"
    status = "PENDENTE"
    observacoes = "Revisar antes de entregar"
} | ConvertTo-Json
$tarefa = Invoke-RestMethod -Method Post -Uri "http://localhost:8080/api/tarefas" -ContentType "application/json; charset=utf-8" -Body ([System.Text.Encoding]::UTF8.GetBytes($corpo))
Invoke-RestMethod -Uri "http://localhost:8080/api/tarefas"
Invoke-RestMethod -Uri "http://localhost:8080/api/tarefas/$($tarefa.id)"
Invoke-RestMethod -Method Delete -Uri "http://localhost:8080/api/tarefas/$($tarefa.id)"
```

O arquivo `requests.http` também contém exemplos para clientes HTTP compatíveis.

## Testes

`mvn clean verify` executa os testes unitários do serviço e de integração da API.
A integração usa H2 em modo PostgreSQL com o mesmo script de tabela, sem depender
de um servidor externo. Isso verifica controller, validação, serviço, persistência,
datas e erros, mas não substitui uma execução com PostgreSQL real.
Relatórios ficam em `target/surefire-reports/`.

## Organização

- `model`: entidade JPA e enum de status.
- `dto`: contrato de entrada e saída.
- `repository`: acesso ao banco.
- `service`: operações e transações.
- `controller`: endpoints HTTP.
- `exception`: respostas de erro.
- `database`: scripts SQL.
- `src/test`: testes automatizados.

## Entrega

Revise o código para conseguir explicar o funcionamento. Adicione sua identificação
conforme a orientação do professor, publique esta pasta em um repositório no GitHub
e envie o link na tarefa do Teams. Inclua os scripts SQL; não inclua `.tools/`,
`target/`, senhas pessoais ou arquivos `.env`.
Prazo informado no enunciado: 04/10/2026 às 23h59.

Referência: https://docs.spring.io/spring-boot/3.5/system-requirements.html
## Atalho neste computador

Java 17 e Maven foram baixados em `.tools/`, sem mudar o Java do Windows.
Para usar essas ferramentas no PowerShell:

```powershell
powershell -ExecutionPolicy Bypass -File .\executar.ps1 testar
powershell -ExecutionPolicy Bypass -File .\executar.ps1 iniciar
```

A segunda opcao exige PostgreSQL configurado conforme as instrucoes acima.
A pasta `.tools/` fica fora do Git; em outro computador use os pre-requisitos ou Docker.
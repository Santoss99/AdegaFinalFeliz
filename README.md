# Sistema de Gestão para Pequenos Negócios

Sistema completo de gestão com controle de produtos, estoque, comandas, pagamentos, caixa e relatórios.

---

## Tecnologias
- **Backend:** Java 11+ puro (sem frameworks)
- **Servidor HTTP:** `com.sun.net.httpserver.HttpServer`
- **Persistência:** Arquivos JSON (via Gson)
- **Frontend:** HTML + CSS + JavaScript puro (fetch API)

---

## Pré-requisitos

- **JDK 11 ou superior** instalado
  - Windows: https://adoptium.net/
  - Linux: `sudo apt install openjdk-21-jdk`
  - Mac: `brew install openjdk`
- Conexão com internet apenas no primeiro build (para baixar o Gson)

---

## Como executar

### Linux / Mac

```bash
# 1. Dar permissão de execução aos scripts
chmod +x build.sh run.sh

# 2. Compilar (só precisa fazer uma vez)
./build.sh

# 3. Executar
./run.sh
```

### Windows

```
# 1. Compilar (duplo clique ou via cmd)
build_windows.bat

# 2. Executar
run_windows.bat
```

---

## Iniciando o servidor web

Após iniciar o sistema, no menu principal escolha:

```
7 - Iniciar servidor web
```

Acesse no navegador: **http://localhost:8080**

Pressione ENTER no terminal para parar o servidor.

---

## Estrutura do projeto

```
gestao/
├── src/
│   ├── Main.java                    ← Ponto de entrada + menu CLI
│   ├── model/
│   │   ├── Produto.java
│   │   ├── MovimentacaoEstoque.java
│   │   ├── ItemComanda.java
│   │   ├── Comanda.java
│   │   ├── Pagamento.java
│   │   ├── Venda.java
│   │   └── Caixa.java
│   ├── repository/
│   │   ├── ProdutoRepository.java
│   │   ├── EstoqueRepository.java
│   │   ├── ComandaRepository.java
│   │   ├── VendaRepository.java
│   │   └── CaixaRepository.java
│   ├── service/
│   │   ├── ProdutoService.java
│   │   ├── EstoqueService.java
│   │   ├── ComandaService.java
│   │   ├── CaixaService.java
│   │   └── RelatorioService.java
│   ├── http/
│   │   ├── BaseHandler.java
│   │   ├── ProdutoHandler.java
│   │   ├── EstoqueHandler.java
│   │   ├── ComandaHandler.java
│   │   ├── CaixaHandler.java
│   │   ├── RelatorioHandler.java
│   │   ├── StaticHandler.java
│   │   └── Servidor.java
│   └── util/
│       ├── IdUtil.java
│       ├── JsonUtil.java
│       └── CsvUtil.java
├── web/
│   └── index.html                   ← Interface web completa
├── dados/                           ← Criado automaticamente (JSON)
│   ├── produtos.json
│   ├── estoque.json
│   ├── comandas.json
│   ├── vendas.json
│   └── caixa.json
├── lib/
│   └── gson-2.10.1.jar              ← Baixado automaticamente no build
├── build.sh                         ← Build Linux/Mac
├── run.sh                           ← Execução Linux/Mac
├── build_windows.bat                ← Build Windows
└── run_windows.bat                  ← Execução Windows
```

---

## Endpoints da API REST

### Produtos
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/produtos` | Listar todos |
| GET | `/produtos?nome=X` | Buscar por nome |
| GET | `/produtos?codigo=X` | Buscar por código |
| GET | `/produtos?estoque-baixo` | Produtos com estoque baixo |
| GET | `/produtos/{id}` | Buscar por ID |
| POST | `/produtos` | Cadastrar produto |
| PUT | `/produtos/{id}` | Editar produto |
| DELETE | `/produtos/{id}` | Remover produto |

### Estoque
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/estoque` | Histórico de movimentações |
| GET | `/estoque/produto/{id}` | Movimentações por produto |
| POST | `/estoque/entrada` | Registrar entrada |
| POST | `/estoque/saida` | Registrar saída |
| POST | `/estoque/ajuste` | Ajuste manual |

### Comandas
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/comandas` | Listar todas |
| GET | `/comandas?abertas` | Listar abertas |
| GET | `/comandas/{id}` | Buscar por ID |
| POST | `/comandas` | Criar comanda |
| POST | `/comandas/adicionar-produto` | Adicionar item |
| POST | `/comandas/remover-produto` | Remover item |
| POST | `/comandas/pagamento` | Adicionar pagamento |
| POST | `/comandas/limpar-pagamentos` | Limpar pagamentos |
| POST | `/comandas/fechar` | Fechar comanda |
| POST | `/comandas/juntar` | Juntar duas comandas |

### Caixa
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/caixa` | Caixa atual |
| GET | `/caixa/historico` | Histórico de caixas |
| POST | `/caixa/abrir` | Abrir caixa |
| POST | `/caixa/fechar` | Fechar caixa |
| POST | `/caixa/entrada` | Entrada manual |
| POST | `/caixa/saida` | Saída manual |

### Relatórios
| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/relatorios/hoje` | Vendas do dia |
| GET | `/relatorios/mes` | Vendas do mês |
| GET | `/relatorios/geral` | Total geral |
| GET | `/relatorios/estoque` | Relatório de estoque |

---

## Funcionalidades

### ✅ Produtos
- Cadastro com código único, preços de compra/venda, estoque inicial e mínimo
- Edição e remoção
- Busca por nome ou código
- Alerta automático de estoque baixo

### ✅ Estoque
- Registro de entrada, saída e ajuste manual
- Histórico completo de movimentações
- Proteção contra estoque negativo
- Baixa automática ao fechar comanda

### ✅ Comandas
- Criação por mesa ou nome do cliente
- Adição/remoção de produtos
- Junção de duas comandas
- Cálculo automático de total

### ✅ Pagamentos
- Múltiplas formas na mesma comanda: Dinheiro, PIX, Cartão
- Divisão de pagamento
- Cálculo automático de troco
- Validação de pagamento completo antes de fechar

### ✅ Caixa
- Abertura com saldo inicial
- Registro automático de vendas
- Entradas e saídas manuais
- Fechamento com resumo completo
- Histórico de movimentações

### ✅ Relatórios
- Vendas do dia, mês e total geral
- Lucro baseado em (preço venda - preço compra)
- Relatório de estoque com valor e custo total

### ✅ Exportação CSV
- Estoque → `estoque_export.csv`
- Vendas → `vendas_export.csv`
- Disponível no menu CLI (opção 6)

---

## Persistência

Os dados são salvos automaticamente em arquivos JSON na pasta `dados/` a cada alteração. O sistema carrega os dados ao iniciar, garantindo que nada seja perdido ao reiniciar.

---

## Observações para o desenvolvedor iniciante

- **Sem banco de dados:** tudo fica em arquivos `.json` dentro da pasta `dados/`
- **Sem login:** o sistema não tem controle de usuários
- **Porta:** o servidor web usa a porta `8080`; se estiver em uso, altere `PORTA` em `src/http/Servidor.java`
- **Backup:** para fazer backup, basta copiar a pasta `dados/`
- **Logs de erro:** aparecem no terminal onde o sistema está sendo executado

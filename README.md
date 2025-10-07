# JBoard - Orchestrator API

Uma API REST robusta e escalável desenvolvida em Java com Spring Boot, que atua como orquestrador central para o sistema de gerenciamento de vagas de emprego, focada especialmente em oportunidades "Brazilian Friendly" para desenvolvedores brasileiros.

## 🎯 Objetivo da Aplicação

O JBoard Orchestrator API é uma aplicação backend que funciona como gateway central do sistema, oferecendo:
- Orquestração inteligente de comunicação entre microserviços
- Sistema de autenticação e autorização baseado em JWT
- Gerenciamento de usuários com diferentes níveis de permissão (FREE/PREMIUM)
- Análise de compatibilidade entre perfis de usuário e vagas de emprego
- Cache inteligente para otimização de performance
- Arquitetura preparada para deploy em containers Azure

## 🚀 Funcionalidades

### Endpoints da API

#### **Autenticação e Autorização**
- **POST** `/auth/login` - Autenticação de usuário com JWT
- **POST** `/auth/register` - Registro de nova conta no sistema
- **PUT** `/auth/change-password` - Alteração de senha (usuários logados)
- **DELETE** `/auth/delete-account` - Exclusão de conta (usuários logados)

**Níveis de Acesso:**
- **FREE**: Funcionalidades básicas de cadastro e busca
- **PREMIUM**: Recursos avançados incluindo análise de compatibilidade com IA

#### **Gerenciamento de Habilidades (Premium)**
- **GET** `/skills` - Listar todas as habilidades do usuário autenticado
- **POST** `/skills` - Adicionar nova habilidade ao perfil
- **PUT** `/skills/remove` - Remover habilidade específica
- **DELETE** `/skills/clear` - Limpar todas as habilidades do usuário

**Características:**
- Integração com microserviço especializado em CRUD
- Cache inteligente para otimização de consultas
- Validação de dados em múltiplas camadas

#### **Análise de Compatibilidade (Premium)**
- **POST** `/analysis/match` - Análise de compatibilidade entre usuário e vaga

**Recursos Avançados:**
- **Análise com IA**: Processamento inteligente via microserviço especializado
- **Cache Otimizado**: Armazenamento de análises para consultas rápidas
- **Relatórios Detalhados**: Métricas de compatibilidade e recomendações

#### **Administração do Sistema**
- **PUT** `/roles/change` - Alteração de nível de acesso de usuários
- **GET** `/jobs` - Listagem de vagas disponíveis (orquestração)

**Controles Administrativos:**
- Validação de credenciais administrativas especiais
- Logs detalhados de operações sensíveis
- Monitoramento de alterações de permissões

### Características Técnicas

#### **Arquitetura de Microserviços**
- **Controllers**: Handlers HTTP para processamento de requisições
- **Services**: Lógica de negócio e orquestração
- **Clients**: Comunicação com microserviços externos
- **Models**: DTOs e entidades de dados
- **Configs**: Configurações de segurança e integração

#### **Recursos Avançados**
- **JWT Authentication**: Sistema seguro de tokens
- **Spring Security**: Configuração robusta de segurança
- **Cache Management**: Otimização de performance
- **Exception Handling**: Tratamento padronizado de erros
- **Logging Estruturado**: Monitoramento e auditoria

## 🔧 Tecnologias Utilizadas

- **Linguagem**: Java 21
- **Framework**: Spring Boot 3.5.5
- **Segurança**: Spring Security + JWT (Auth0)
- **Cache**: Spring Cache
- **Validação**: Spring Validation
- **Build Tool**: Maven
- **Containerização**: Docker Multi-stage
- **Monitoramento**: Lombok + SLF4J
- **Testes**: Spring Boot Test + JUnit

## 📦 Instalação e Execução

### Pré-requisitos
- Java 21 ou superior
- Maven 3.6+
- Docker e Docker Compose
- Acesso aos microserviços CRUD e Analysis
- Git

### Instalação Local

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/Renato556/jboard-java-backend.git
   cd jboard-java-backend
   ```

2. **Configurar variáveis de ambiente:**
   ```bash
   # Criar arquivo .env na raiz do projeto
   CRUD_URL=http://localhost:8080
   ANALYSIS_URL=http://localhost:8082
   JWT_SECRET=your-super-secret-key-here
   FRONTEND_URLS=http://localhost:5173,http://jboard-frontend
   ADMIN_CREDENTIALS=admin:admin123
   ```

3. **Instalar dependências:**
   ```bash
   ./mvnw clean install
   ```

4. **Executar em modo desenvolvimento:**
   ```bash
   ./mvnw spring-boot:run
   ```

5. **Acessar a aplicação:**
   ```
   http://localhost:8081
   ```

### Execução com Docker

```bash
# Build da imagem
docker build -t jboard-orchestrator .

# Executar container
docker run -p 8081:8081 --env-file .env jboard-orchestrator
```

### Execução com Docker Compose

```yaml
# compose.yaml
version: '3.8'
services:
  orchestrator:
    build: .
    ports:
      - "8081:8081"
    environment:
      - CRUD_URL=http://crud-service:8080
      - ANALYSIS_URL=http://analysis-service:8082
      - JWT_SECRET=${JWT_SECRET}
    depends_on:
      - crud-service
      - analysis-service
```

## 🧪 Execução de Testes

### Testes Unitários

```bash
# Executar todos os testes
./mvnw test

# Executar testes com relatório de cobertura
./mvnw test jacoco:report

# Executar testes específicos
./mvnw test -Dtest=AnalysisServiceTest
```

### Testes por Módulo

```bash
# Testes dos controllers
./mvnw test -Dtest=**/*ControllerTest

# Testes dos services
./mvnw test -Dtest=**/*ServiceTest

# Testes dos clients
./mvnw test -Dtest=**/*ClientTest

# Testes de integração
./mvnw test -Dtest=**/*IntegrationTest
```

### Cobertura de Testes

```bash
# Gerar relatório de cobertura
./mvnw clean test jacoco:report

# Visualizar relatório
# Abrir target/site/jacoco/index.html no navegador
```

### Testes de Performance

```bash
# Executar testes de carga (se configurados)
./mvnw test -Dtest=**/*PerformanceTest

# Testes de stress com JMeter
jmeter -n -t performance-tests/load-test.jmx
```

## 🔗 Integração com Microserviços

### Arquitetura de Comunicação

**Microserviços Integrados:**
- **CRUD Service**: Operações de dados (usuários, vagas, skills)
- **Analysis Service**: Processamento de IA para análise de compatibilidade
- **Frontend Application**: Interface web React/Vue.js

### Endpoints de Integração

#### **CRUD Service (Port 8080)**
```bash
# Skills Management
GET /v1/skills?username={username}
POST /v1/skills
PUT /v1/skills
DELETE /v1/skills?username={username}

# Jobs Management  
GET /v1/jobs
```

#### **Analysis Service (Port 8082)**
```bash
# AI Analysis
POST /analyse
```

## 🔄 Deploy e Workflows

### Azure Deploy Pipeline

O projeto utiliza Azure DevOps para deploy automático em Azure Container Apps.

#### Triggers de Deploy
- **Push**: Branches `main` e `develop`
- **Pull Request**: Validação automática
- **Manual**: `workflow_dispatch` para deploys específicos

#### Etapas do Pipeline

##### Test and Quality Gate
- Checkout do código fonte
- Setup Java 21 e Maven cache
- Execução de testes unitários (`./mvnw test`)
- Análise de cobertura de código
- SonarQube quality gate (se configurado)
- Validação de segurança (OWASP dependency check)

##### Build and Package
- Build otimizado com Maven (`./mvnw clean package`)
- Análise estática de código
- Geração de artefatos JAR
- Criação de imagem Docker multi-stage

##### Deploy and Infrastructure
- Login no Azure Container Registry
- Push da imagem para registry
- Deploy no Azure Container Apps
- Configuração de variáveis de ambiente
- Health checks e validação de deploy
- Rollback automático em caso de falha

#### Variáveis de Ambiente Azure
- **AZURE_CONTAINER_REGISTRY**: `jboardregistry`
- **CONTAINER_APP_NAME**: `jboard-orchestrator`
- **RESOURCE_GROUP**: `jboard-microservices`
- **IMAGE_NAME**: `jboard-java-orchestrator`

#### Secrets Necessários
- **AZURE_CREDENTIALS**: Service principal credentials
- **ACR_USERNAME**: Container Registry username
- **ACR_PASSWORD**: Container Registry password
- **JWT_SECRET**: Token de autenticação JWT
- **ADMIN_CREDENTIALS**: Credenciais administrativas

#### Características Avançadas do Deploy
- **Blue-Green Deployment**: Deploy sem downtime
- **Auto-scaling**: Escalabilidade baseada em CPU/memória
- **Health Monitoring**: Monitoramento contínuo de saúde
- **Log Aggregation**: Centralização de logs no Azure Monitor
- **Metrics Dashboard**: Dashboards em tempo real

### 🔒 Segurança em Produção

**⚠️ ARQUITETURA ENTERPRISE DE SEGURANÇA**

**Características de Segurança:**
- ✅ **JWT Authentication**: Tokens seguros com expiração
- ✅ **CORS Configuration**: Controle rigoroso de origens
- ✅ **Input Validation**: Validação em múltiplas camadas
- ✅ **HTTPS Enforcement**: Criptografia obrigatória
- ✅ **Security Headers**: Headers de segurança padrão

## 🤝 Colaboração e Desenvolvimento

### Padrões de Código
- **Checkstyle**: Validação de estilo de código Java
- **Clean Architecture**: Separação clara de responsabilidades

### Estrutura de Pastas
```
src/main/java/br/com/jboard/orchestrator/
├── controllers/          # Endpoints REST
├── services/            # Lógica de negócio
├── clients/             # Integração com microserviços
├── models/              # DTOs e entidades
│   ├── dtos/           # Data Transfer Objects
│   └── exceptions/     # Exceções customizadas
├── configs/            # Configurações Spring
├── handlers/           # Exception handlers globais
└── utils/              # Utilitários e helpers
```

### Contribuindo
1. Fork o projeto
2. Crie uma branch feature (`git checkout -b feature/amazing-feature`)
3. Commit suas mudanças (`git commit -m 'Add some amazing-feature'`)
4. Push para a branch (`git push origin feature/amazing-feature`)
5. Abra um Pull Request

### Code Review
- Todos os PRs devem passar nos testes
- Cobertura mínima de 80%
- Aprovação de pelo menos 1 reviewer
- Validação automática do CI/CD

## 📄 Licenciamento

### Licença MIT

Este projeto está licenciado sob a Licença MIT - veja o arquivo [LICENSE](LICENSE) para detalhes.

**Resumo da Licença:**
- ✅ Uso comercial
- ✅ Modificação
- ✅ Distribuição
- ✅ Uso privado
- ❌ Responsabilidade
- ❌ Garantia

### Direitos de Uso
- Permitido uso em projetos comerciais
- Permitida modificação do código
- Créditos aos autores originais apreciados
- Não há garantias de funcionamento
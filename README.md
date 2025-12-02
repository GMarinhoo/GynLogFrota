# 🚚 GynLog - Sistema de Controle de Frota & Análise Matricial

> **Projeto Integrador - Engenharia de Software (2º Período - 2025/2)**
> **SENAI FATESG**

Sistema Desktop desenvolvido em **Java** para gestão de frotas veiculares, controle financeiro e análise matemática de custos operacionais. O projeto integra conceitos de Engenharia de Software, Programação Orientada a Objetos e Álgebra Linear.

---

## 🚀 Tecnologias e Arquitetura

O projeto utiliza uma arquitetura híbrida robusta:

- **Linguagem:** Java 17.
- **Framework:** Spring Boot (Utilizado para *Injeção de Dependência* e inversão de controle).
- **Interface Gráfica:** Java Swing (Design moderno com componentes customizados).
- **Persistência:** Sistema de Arquivos (`.txt`) com manipulação direta (NIO), sem uso de SGBD, conforme requisito do edital.
- **Build:** Maven.

---

## ⚙️ Funcionalidades Principais

### 🚗 Gestão de Veículos
- Cadastro completo com categorização por **Tipo** (Carro, Moto, Caminhão, Van, Caminhonete).
- Controle de Status (Ativo/Inativo).
- Persistência em arquivo CSV (`veiculos.txt`).

### 💰 Gestão Financeira
- Lançamento de despesas e movimentações.
- Categorização via Enum (Combustível, IPVA, Multa, Manutenção, etc.).
- Vínculo automático com a frota cadastrada.

### 👥 Controle de Acesso e Segurança
- Sistema de Login com criptografia básica.
- Níveis de acesso:
    - **Gerente:** Acesso total (incluindo Relatórios e Matrizes).
    - **Funcionário:** Acesso restrito (apenas lançamentos).
- Bloqueio de auto-exclusão para segurança.

### 📊 Relatórios Gerenciais (Exportação Automática)
O sistema gera e exporta arquivos `.txt` automaticamente para:
1. Extrato de Despesas por Veículo.
2. Total Mensal da Frota.
3. Gastos exclusivos com Combustível.
4. Somatório de IPVA anual.
5. Listagem de Veículos Inativos.
6. Relatório de Multas.

---

## 🧮 Módulo de Fundamentos Matemáticos (Matrizes)

Para atender aos requisitos interdisciplinares de **Álgebra Linear**, o sistema implementa um algoritmo de multiplicação de matrizes para projeção de custos:

- **Matriz A ($m \times n$):** Quantidade de Abastecimentos (Veículos x Meses).
- **Matriz B ($n \times p$):** Custo Médio por Abastecimento (Meses x Marcas).
- **Matriz C ($m \times p$):** Resultado de $A \times B$.

> **Resultado:** A Matriz C apresenta o **Gasto Total Estimado** de cada veículo ponderado pela média de custo da sua marca ao longo do ano.

---

## 🛠️ Como Rodar o Projeto

### Pré-requisitos
- Java JDK 17 instalado.
- Maven.

### Execução
1. Clone o repositório.
2. Abra na sua IDE (IntelliJ IDEA recomendado).
3. Execute a classe principal:
   `src/main/java/br/com/gynlog/app/GynLogApp.java`

> **Nota:** O sistema criará automaticamente os arquivos de banco de dados (`veiculos.txt`, `movimentacoes.txt`, `usuarios.txt`) e a pasta `relatorios_automaticos` na primeira execução.

---

## 🔑 Acesso Padrão

Para o primeiro acesso (Admin):

- **Usuário:** `admin`
- **Senha:** `admin`

---

## 📂 Estrutura de Pacotes (MVC)

- `br.com.gynlog.view`: Telas Swing e componentes visuais.
- `br.com.gynlog.service`: Regras de negócio, validações e lógica matemática.
- `br.com.gynlog.repository`: Camada de acesso a dados (Leitura/Escrita de arquivos).
- `br.com.gynlog.model`: Entidades (Veiculo, Movimentacao, Usuario).
- `br.com.gynlog.enums`: Tipagens fortes (TipoVeiculo, TipoDespesaEnum).

---
*Desenvolvido em Novembro/2025.*
# 🚚 GynLog - Sistema de Controle de Frota

> Projeto Integrador - Engenharia de Software (2º Período - 2025/2)
> **SENAI FATESG**

Sistema Desktop desenvolvido em **Java** para gestão de frotas veiculares, controle de despesas e geração de relatórios gerenciais, utilizando persistência em arquivos de texto.

---

## 🚀 Tecnologias Utilizadas

- **Java 17**: Linguagem principal.
- **Spring Boot**: Injeção de Dependência e estruturação do Backend.
- **Java Swing**: Interface Gráfica (GUI) moderna e responsiva.
- **Maven**: Gerenciamento de dependências.
- **File Persistence**: Banco de dados baseado em arquivos `.txt` (CSV).

---

## ⚙️ Funcionalidades

### 🚗 Gestão de Veículos
- Cadastro completo (Placa, Marca, Modelo, Ano).
- Controle de Status (Ativo/Inativo).
- Listagem e Edição.

### 💰 Gestão de Despesas
- Lançamento de movimentações financeiras.
- Categorização via Enum (Combustível, IPVA, Multa, Manutenção, etc.).
- Vinculação automática com veículos cadastrados.

### 👥 Controle de Acesso
- Sistema de Login seguro.
- Perfis de acesso: **Gerente** (Acesso total) e **Funcionário** (Restrito).
- Bloqueio de funcionalidades sensíveis para funcionários.

### 📊 Relatórios Gerenciais (Exportáveis)
O sistema gera e exporta automaticamente arquivos `.txt` para:
1. Despesas por Veículo.
2. Total Mensal da Frota.
3. Gastos com Combustível.
4. Somatório de IPVA anual.
5. Listagem de Veículos Inativos.
6. Relatório de Multas.

---

## 🛠️ Como Rodar o Projeto

### Pré-requisitos
- Java JDK 17 instalado.
- Maven instalado (ou usar o da IDE).

### Passo a Passo
1. Clone o repositório:
   ```bash
   git clone [https://github.com/SEU-USUARIO/GynLogFrota.git](https://github.com/SEU-USUARIO/GynLogFrota.git)

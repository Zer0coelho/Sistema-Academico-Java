# Sistema Acadêmico - Java + Swing + MySQL

Sistema acadêmico desktop desenvolvido em Java utilizando Swing para interface gráfica e MySQL para persistência de dados.

O projeto foi desenvolvido com foco em Programação Orientada a Objetos (POO), CRUD completo, integração com banco de dados e interface gráfica utilizando `JTabbedPane`.

---

# Tecnologias Utilizadas

* Java
* Java Swing
* JDBC
* MySQL
* Eclipse IDE

---

# Funcionalidades

## Cadastro de Alunos

* Cadastro de alunos
* Alteração de dados
* Consulta por RGM
* Exclusão de alunos

## Gerenciamento de Cursos

* Associação do aluno a um curso
* Seleção de campus
* Seleção de período

## Notas e Faltas

* Cadastro de notas
* Cadastro de faltas
* Alteração de registros
* Exclusão de registros
* Consulta por aluno

## Boletim

* Visualização completa do histórico acadêmico
* Situação do aluno:

  * Aprovado
  * Reprovado
  * Reprovado por falta

---

# Estrutura do Projeto

```text
src/
│
├── Aluno.java
├── AlunoDAO.java
├── Curso.java
├── CursoDAO.java
├── NotaFalta.java
├── NotaFaltaDAO.java
├── ConexaoBD.java
└── SistemaAcademico.java
```

---

# Estrutura do Banco de Dados

## Banco

```sql
CREATE DATABASE sistema_academico;
```

---

## Tabela Curso

```sql
CREATE TABLE curso (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    campus VARCHAR(100),
    periodo VARCHAR(50)
);
```

---

## Tabela Aluno

```sql
CREATE TABLE aluno (
    rgm VARCHAR(20) PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    data_nascimento VARCHAR(20),
    cpf VARCHAR(20),
    email VARCHAR(100),
    endereco VARCHAR(200),
    municipio VARCHAR(100),
    uf VARCHAR(2),
    celular VARCHAR(20),
    id_curso INT,
    FOREIGN KEY (id_curso) REFERENCES curso(id)
);
```

---

## Tabela Nota/Falta

```sql
CREATE TABLE nota_falta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    rgm_aluno VARCHAR(20),
    disciplina VARCHAR(100),
    semestre VARCHAR(20),
    nota DOUBLE,
    faltas INT,
    FOREIGN KEY (rgm_aluno)
        REFERENCES aluno(rgm)
        ON DELETE CASCADE
);
```

---

# Configuração do Banco de Dados

Na classe `ConexaoBD.java`, configure:

```java
private static final String URL =
"jdbc:mysql://localhost:3306/sistema_academico?useSSL=false&serverTimezone=UTC";

private static final String USUARIO = "root";
private static final String SENHA = "SUA_SENHA";
```

---

# Dependência JDBC

É necessário adicionar o MySQL Connector/J ao projeto.

Download oficial:

https://dev.mysql.com/downloads/connector/j/

Após baixar:

```text
Build Path
→ Configure Build Path
→ Libraries
→ Add External JARs
```

Selecione o arquivo `.jar` do connector.

---

# Como Executar

1. Clone ou baixe o projeto
2. Crie o banco de dados MySQL
3. Execute os scripts SQL
4. Configure usuário e senha do banco
5. Adicione o MySQL Connector/J no projeto
6. Execute a classe:

```text
SistemaAcademico.java
```

---

# Interface do Sistema

O sistema possui 4 abas principais:

* Dados Pessoais
* Curso
* Notas e Faltas
* Boletim

Também possui:

* Menu superior
* CRUD completo
* JTable
* Máscaras de campos
* Mensagens de validação

---

# Conceitos Aplicados

* Programação Orientada a Objetos
* Encapsulamento
* JDBC
* DAO (Data Access Object)
* Swing
* JTable
* JTabbedPane
* PreparedStatement
* Relacionamento entre tabelas
* CRUD

---

# Melhorias Futuras

* Login de usuários
* Relatórios em PDF
* Exportação para Excel
* Uso de LocalDate
* Arquitetura MVC
* Uso de Maven
* Melhor tratamento de exceções
* Filtro de cursos por campus
* Dashboard acadêmico

---

# Autor

Projeto desenvolvido para fins acadêmicos na disciplina de Programação Orientada a Objetos.

Java + Swing + MySQL

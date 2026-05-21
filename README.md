📚 Sistema Acadêmico — UNICID
Sistema desktop de gerenciamento acadêmico desenvolvido em Java + Swing com persistência em MySQL, criado como projeto da disciplina de Programação Orientada a Objetos da UNICID.

🗂️ Funcionalidades

Cadastro de alunos com RGM, dados pessoais, endereço e vínculo a curso
Gestão de notas e faltas por disciplina e semestre
Boletim com situação por disciplina (Aprovado / Reprovado / Rep. Falta), cabeçalho colorido por situação e exibição do período selecionado
Consulta, alteração e exclusão de alunos e registros de notas
Validações de entrada: RGM numérico, CPF único, data de nascimento via calendário nativo (JSpinner), máscaras para CPF e celular
Período (Matutino / Vespertino / Noturno) selecionado na interface — não é persistido no banco


🏗️ Estrutura do Projeto
SistemaAcademico/
│
├── SistemaAcademico.java   # Classe principal — JFrame com menus e abas (Swing)
│
├── model/
│   ├── Aluno.java          # Entidade Aluno
│   ├── Curso.java          # Entidade Curso
│   └── NotaFalta.java      # Entidade Nota/Falta por disciplina
│
├── dao/
│   ├── AlunoDAO.java       # CRUD de alunos (com conversão de datas BR ↔ SQL)
│   ├── CursoDAO.java       # Listagem e busca de cursos
│   └── NotaFaltaDAO.java   # CRUD de notas e faltas
│
├── util/
│   └── ConexaoBD.java      # Conexão com MySQL via JDBC
│
└── Novo-novo.sql           # Script de criação do banco de dados

Os arquivos estão atualmente no pacote padrão (sem subpastas). A estrutura acima é uma sugestão de organização.


🗃️ Banco de Dados
Schema
sqlcurso (id PK, nome, campus)

aluno (rgm PK, nome, data_nascimento, cpf UNIQUE, email,
       endereco, municipio, uf, celular, id_curso FK → curso)

nota_falta (id PK, rgm_aluno FK → aluno, disciplina,
            semestre, nota, faltas)

A exclusão de um aluno remove suas notas e faltas em cascata (ON DELETE CASCADE).
O campo periodo não existe no banco — é gerenciado apenas na interface.


⚙️ Pré-requisitos
FerramentaVersão mínimaJava (JDK)11MySQL Server8.0MySQL Connector/J8.x (mysql-connector-java-8.x.x.jar)IDE (opcional)Eclipse / IntelliJ IDEA

🚀 Como Executar
1. Criar o banco de dados
Abra o MySQL Workbench (ou outro cliente) e execute o script:
Novo-novo.sql
Isso cria o banco sistema_academico com as três tabelas e insere os cursos iniciais.
2. Configurar a conexão
Abra ConexaoBD.java e ajuste as credenciais:
javaprivate static final String URL     = "jdbc:mysql://localhost:3306/sistema_academico";
private static final String USUARIO = "root";       // seu usuário MySQL
private static final String SENHA   = "sua_senha";  // sua senha MySQL
3. Adicionar o driver JDBC ao Build Path
Baixe o mysql-connector-java-8.x.x.jar e adicione ao projeto:

Eclipse: clique com o botão direito no projeto → Build Path → Add External Archives
IntelliJ: File → Project Structure → Modules → Dependencies → + → JARs

4. Compilar e rodar
Execute a classe SistemaAcademico (método main).

🖥️ Interface
A janela principal possui quatro abas:
AbaConteúdoDados PessoaisRGM, nome, data de nascimento, CPF, e-mail, endereço, UF, celularCursoSeleção de curso, campus e período (Matutino/Vespertino/Noturno)Notas e FaltasDisciplina, semestre, nota e faltas — tabela com histórico do alunoBoletimResumo por disciplina com situação final colorida por aprovação
O menu superior (Aluno e Notas e Faltas) oferece atalhos de teclado:

Ctrl+S — Salvar aluno
Shift+R — Sair
Ctrl+A — Alterar nota selecionada


📐 Decisões de Design

Sem surrogate keys em aluno: o RGM é a chave primária natural (String).
Período fora do banco: o atributo periodo foi removido do schema para evitar redundância; o usuário seleciona na interface e o valor aparece apenas no boletim.
Conversão de datas: AlunoDAO converte entre dd/MM/yyyy (interface) e java.sql.Date (banco), com setLenient(false) para rejeitar datas inválidas como 32/13/2000.
Cascata: a FK nota_falta.rgm_aluno → aluno.rgm tem ON DELETE CASCADE, garantindo integridade sem lógica manual na camada Java.
Segunda linha de defesa para CPF: além da verificação em Java, a coluna cpf tem UNIQUE no banco; SQLIntegrityConstraintViolationException é capturada como fallback.


👥 Autores
Projeto desenvolvido para a disciplina de Programação Orientada a Objetos — UNICID.

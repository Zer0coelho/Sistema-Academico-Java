-- SCRIPT DE CRIAÇÃO DO BANCO - SISTEMA ACADÊMICO
-- Execute este script no MySQL antes de rodar o projeto

CREATE DATABASE IF NOT EXISTS sistema_academico;
USE sistema_academico;

-- Tabela de Cursos (periodo removido — gerenciado só na interface)
CREATE TABLE IF NOT EXISTS curso (
    id     INT          AUTO_INCREMENT PRIMARY KEY,
    nome   VARCHAR(100) NOT NULL,
    campus VARCHAR(100) NOT NULL
    -- 'periodo' não é mais persistido no banco;
    -- o usuário seleciona o período na interface e ele aparece no boletim
);

-- Tabela de Alunos
CREATE TABLE IF NOT EXISTS aluno (
    rgm             VARCHAR(20)  PRIMARY KEY,
    nome            VARCHAR(100) NOT NULL,
    data_nascimento DATE         NOT NULL,
    cpf             VARCHAR(14)  NOT NULL UNIQUE,
    email           VARCHAR(100),
    endereco        VARCHAR(200),
    municipio       VARCHAR(100),
    uf              VARCHAR(2),
    celular         VARCHAR(15),
    id_curso        INT,
    FOREIGN KEY (id_curso) REFERENCES curso(id) ON DELETE SET NULL
);

-- Tabela de Notas e Faltas
CREATE TABLE IF NOT EXISTS nota_falta (
    id         INT          AUTO_INCREMENT PRIMARY KEY,
    rgm_aluno  VARCHAR(20)  NOT NULL,
    disciplina VARCHAR(100) NOT NULL,
    semestre   VARCHAR(10)  NOT NULL,
    nota       DOUBLE       NOT NULL DEFAULT 0.0,
    faltas     INT          NOT NULL DEFAULT 0,
    FOREIGN KEY (rgm_aluno) REFERENCES aluno(rgm) ON DELETE CASCADE
);

-- Dados iniciais de cursos (sem periodo)
INSERT IGNORE INTO curso (nome, campus) VALUES
('Analise e Desenvolvimento de Sistemas', 'Tatuapé'),
('Engenharia de Software',                'Tatuapé'),
('Ciencia da Computacao',                 'Tatuapé'),
('Sistemas de Informacao',                'Tatuapé'),
('Redes de Computadores',                 'Tatuapé');

SELECT * FROM aluno;
SELECT * FROM nota_falta;
SELECT * FROM curso;
-- DROP DATABASE sistema_academico;
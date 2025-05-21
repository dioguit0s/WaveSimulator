-- Pode executar a query inteira.

-- Criar banco de dados e suas tabelas
create database simulacao
go


-- Usar banco de dados
use simulacao
go


-- Criar tabela usuario
create table usuario (
	nome_usuario varchar(20) primary key not null,
	email varchar(20) not null,
	senha varchar(20) not null
)


-- Criar a tabela valores_simulacao
create table valores_simulacao (
	Cod_sim int identity(1,1) primary key,
	nome_usuario varchar(20),
	frequencia decimal(10,2) null,
	comprimento decimal(10,2) null,
	duracao integer null,
	erroMax decimal(10,2) null,
	diaSimulacao smalldatetime
)


-- Associar as chaves estrangeiras
alter table valores_simulacao ADD CONSTRAINT FK_valores_simulacao_1
	foreign key(nome_usuario)
	references usuario(nome_usuario)


-- Realizar insert de teste
insert into usuario values ('nomeTeste', 'email@teste.com', 'senha123456')

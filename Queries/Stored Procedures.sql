-- Executar as stored procedures individualmente!


-- Utilizar novo banco
use simulacao;


--SP: Inserir um usuário no banco
create procedure sp_insereUsuario
	@nome varchar(50), @email varchar(100), @senha varchar(50)
AS
BEGIN
		insert into usuario values (@nome, @email, @senha)
END;


--SP: Selecionar um usuário do banco
create procedure sp_selecionaUsuario
    @nome VARCHAR(50)
AS
BEGIN

    SELECT nome_usuario,  senha
    FROM usuario
    WHERE nome_usuario = @nome;

END;


--SP: Inserir os valores da onda e associá-los com um usuário
create procedure sp_NovaOnda
@nomeUsuario varchar(50), @frequencia decimal(10,2), @comprimento decimal(10,2), @duracao integer, @ErroMax decimal(10,2), @data smalldatetime
AS
BEGIN
	insert into valores_simulacao values (@nomeUsuario, @frequencia, @comprimento, @duracao, @ErroMax, @data)

END;


--SP: Pegar os valores das simulacoes por usuario
create  procedure sp_PegaHistorico
    @nomeUsuario NVARCHAR(255)
AS
BEGIN
    SELECT frequencia, comprimento, duracao, erroMax, diaSimulacao
    FROM valores_simulacao
    WHERE nome_usuario = @nomeUsuario
    ORDER BY diaSimulacao DESC;
END;
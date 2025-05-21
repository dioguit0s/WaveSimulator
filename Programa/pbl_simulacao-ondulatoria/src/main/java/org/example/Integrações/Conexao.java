package org.example.Integrações;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    public Connection getConexao() {
        //throws ClassNotFoundException, SQLException
        Connection conexao = null;

        try {
            // Endereço do host onde o banco de dados está sendo executado
            String hostname = "localhost:1433";
            // Nome da instância do SQL Server (pode ser o nome do computador - alterar para o seu)
            String sqlInstanceName = "HPFS-CE-LAB6712";
            // Nome do banco de dados
            String sqlDatabase = "simulacao";
            // Nome de usuário do banco de dados
            String sqlUser = "sa";
            // Senha do usuário do banco de dados
            String sqlPassword = "123456";

            // Carrega o driver JDBC do SQL Server
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Define as informações de conexão com o banco de dados
            // jdbc:sqlserver://localhost:1433;instance=HPFS-CE-LAB6741;databaseName=loja;
            String connectURL = "jdbc:sqlserver://" + hostname + ";instance=" + sqlInstanceName
                    + ";databaseName=" + sqlDatabase + ";encrypt=true;trustServerCertificate=true;";
            /*encrypt = true indica a criptografia da comunicação e trustServerCertificate = true confia no
            certificado do servidor sem validação. Este último é recomendável 'false' em ambiente de produção
             */

            // Estabele a conexão com o BD
            conexao = DriverManager.getConnection(connectURL, sqlUser, sqlPassword);

            // Informação se a conexão foi realizada ou não
            if (conexao != null) {
                System.out.println("Conexão bem-sucedida!");
            } else {
                System.out.println("Não foi possível conectar ao banco de dados.");
            }

            // Exceção para o caso do driver JDBC não ser encontrado
        } catch (ClassNotFoundException e) {
            System.out.println("Driver JDBC não encontrado.");
            /*Mostra o rastreamento da pilha da exceção atual para a saída padrão,
            geralmente usada para depuração ou registro de erros.
             */
            e.printStackTrace();

            // Exceção para o caso de ocorrer um erro durante a conexão com o banco de dados
        } catch (SQLException e) {
            System.out.println("Erro ao conectar ao banco de dados.");
            e.printStackTrace();
        }
        // Retorna a conexão estabelecida
        return conexao;
    }
}

package org.example.Integrações;

import org.example.Objetos.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {

    private final Connection connection;

    public UsuarioDAO(Connection connection) {
        this.connection = connection;
    }

    // Executar sp de inserir usuários no banco
    public void inserir(Usuario usuario) throws SQLException {
        String sql = "Exec sp_insereUsuario ?, ?, ?";

        try ( PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, usuario.getNome());
            statement.setString(2, usuario.getEmail());
            statement.setString(3, usuario.getSenha());
            statement.executeUpdate();
        }
    }

    // Executar sp de buscar usuários no banco
    public Usuario buscarPorNome(String nome) throws SQLException {
        String sql = "Exec sp_selecionaUsuario ?";
        try ( PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome); // Busca por nome
            ResultSet resultSet = statement.executeQuery();
            String senhaUser = null;
            String nomeUser = null;
            if(resultSet.next()) {
                nomeUser = resultSet.getString("nome_usuario");
                 senhaUser = resultSet.getString("senha");

            }
            Usuario usuario = new Usuario(nomeUser, senhaUser);
            return usuario;
        }
    }
}

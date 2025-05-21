package org.example.Integrações;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


public class SimulacaoDAO {

    private final Connection connection;

    public SimulacaoDAO(Connection connection) {
        this.connection = connection;
    }

    // Executar sp de inserir uma nova onda no histórico
    public void inserir(String nome, double freq, double comprimento, double duracao, double erro) throws SQLException {
        String sql = "exec sp_NovaOnda ?, ?, ?, ?, ?, ?";
        try ( PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nome);
            statement.setDouble(2, freq);
            statement.setDouble(3, comprimento);
            statement.setDouble(4, duracao);
            statement.setDouble(5, erro);
            statement.setDate(6, Date.valueOf(LocalDate.now()));
            statement.executeUpdate();
        }

    }

    // Executar sp de resgatar histórico de um determinado usuário
    public List<String> getHistoricoSimulacoes(String nomeUsuario) throws SQLException {
        List<String> historico = new ArrayList<>();
        String sql = "exec sp_PegaHistorico ?";

        try (CallableStatement stmt = connection.prepareCall(sql)) {
            stmt.setString(1, nomeUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String frequencia = rs.getString("frequencia");
                    String comprimento = rs.getString("comprimento");
                    String duracao = rs.getString("duracao");
                    String erroMax = rs.getString("erroMax");
                    String diaSimulacao = rs.getString("diaSimulacao");

                    historico.add(String.format("Frequência: %s Hz |  Comprimento de onda: %s m  |  Duração da simulação: %s s |  Erro Máximo: %s  |  Data: %s",
                            frequencia, comprimento, duracao, erroMax, diaSimulacao.substring(0, diaSimulacao.length() - 11)));
                }
            }
        }

        return historico;
    }
}


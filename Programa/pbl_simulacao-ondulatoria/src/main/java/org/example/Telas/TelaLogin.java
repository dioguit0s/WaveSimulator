package org.example.Telas;

import org.example.Integrações.Conexao;
import org.example.Integrações.UsuarioDAO;
import org.example.Objetos.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class TelaLogin extends JFrame {
    private JTextField tfUsuario;
    private JPasswordField pfSenha;
    private JButton btnLogin, btnCadastro;


    public TelaLogin() {
        // Configurações básicas da janela
        setTitle("Tela de Login");
        setSize(400, 300);  // Tamanho da janela
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela na tela
        setUndecorated(false);  // Deixa a janela com a borda padrão

        // Definir o layout principal como BorderLayout
        setLayout(new BorderLayout());

        // Criando um painel para adicionar margem
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40)); // Margem de 40px em todos os lados

        // Criando os componentes
        JLabel lblUsuario = new JLabel("Usuário:");
        JLabel lblSenha = new JLabel("Senha:");

        tfUsuario = new JTextField(20);
        pfSenha = new JPasswordField(20);

        btnLogin = new JButton("Login");
        btnCadastro = new JButton("Cadastrar");

        // Estilizando os componentes
        Font font = new Font("Arial", Font.PLAIN, 14);
        lblUsuario.setFont(font);
        lblSenha.setFont(font);
        tfUsuario.setFont(font);
        pfSenha.setFont(font);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnCadastro.setFont(new Font("Arial", Font.BOLD, 16));

        // Adicionando bordas aos campos de texto
        tfUsuario.setBackground(new Color(245, 245, 245)); // Fundo suave
        pfSenha.setBackground(new Color(245, 245, 245));
        tfUsuario.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        pfSenha.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Estilizando os botões
        btnLogin.setBackground(new Color(40, 120, 200));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder());

        btnCadastro.setBackground(new Color(240, 240, 240));  // Fundo claro
        btnCadastro.setForeground(new Color(40, 120, 200));  // Texto azul
        btnCadastro.setFocusPainted(false);
        btnCadastro.setBorder(BorderFactory.createEmptyBorder());

        // Painel central onde os campos e botões ficam
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new GridLayout(4, 2, 10, 10)); // 4 linhas, 2 colunas
        panelCenter.setBackground(Color.WHITE);

        // Adicionando os componentes ao painel
        panelCenter.add(lblUsuario);
        panelCenter.add(tfUsuario);
        panelCenter.add(lblSenha);
        panelCenter.add(pfSenha);
        panelCenter.add(new JLabel());  // Espaço vazio para ajustar o botão
        panelCenter.add(btnLogin);
        panelCenter.add(new JLabel());  // Espaço vazio para o botão de cadastro
        panelCenter.add(btnCadastro);

        // Adiciona o painel central ao painel principal
        contentPanel.add(panelCenter, BorderLayout.CENTER);

        // Coloca o painel de conteúdo no JFrame
        getContentPane().add(contentPanel);

        // Ação do botão de login
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String nome = tfUsuario.getText();
                String nome_user = tfUsuario.getText();
                String senha = new String(pfSenha.getPassword());

                //instanciando classes necessarias para conexao com banco
                Conexao conx = new Conexao();
                Connection conexao = conx.getConexao();
                UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);

                Usuario user = null;

                try {
                    user = usuarioDAO.buscarPorNome(nome);

                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                if(Objects.equals(user.getSenha(), senha)){
                    JOptionPane.showMessageDialog(null, "Login realizado com sucesso!\nUsuário: " + nome);
                    SwingUtilities.invokeLater(() -> {
                        TelaSimulacao simulacao = new TelaSimulacao();
                        simulacao.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        simulacao.setVisible(true);
                        simulacao.setNome_user(tfUsuario.getText());
                        dispose();

                    });
                } else JOptionPane.showMessageDialog(null, "Usuário ou senha incorreto");





            }
        });


        // Ação do botão de cadastro
        btnCadastro.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Cria a nova tela de cadastro
                TelaCadastro telaCadastro = new TelaCadastro();
                telaCadastro.setVisible(true);


                // Fecha a tela de login
                dispose();

            }
        });

    }



}

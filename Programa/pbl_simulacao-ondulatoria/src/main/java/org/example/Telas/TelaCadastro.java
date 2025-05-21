package org.example.Telas;

import org.example.Integrações.Conexao;
import org.example.Integrações.UsuarioDAO;
import org.example.Objetos.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;

public class TelaCadastro extends JFrame {
    private JTextField tfUsuarioCadastro;
    private JPasswordField pfSenhaCadastro;
    private JTextField tfEmailCadastro;
    private JButton btnCadastrar;
    private JButton btnVoltar;


    public TelaCadastro() {
        // Configurações básicas da janela
        setTitle("Tela de Cadastro");
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
        JLabel lblUsuarioCadastro = new JLabel("Usuário:");
        JLabel lblSenhaCadastro = new JLabel("Senha:");
        JLabel lblEmailCadastro = new JLabel("E-mail:");

        tfUsuarioCadastro = new JTextField(20);
        pfSenhaCadastro = new JPasswordField(20);
        tfEmailCadastro = new JTextField(20);

        btnCadastrar = new JButton("Cadastrar");
        btnVoltar = new JButton("Voltar");

        // Estilizando os componentes
        Font font = new Font("Arial", Font.PLAIN, 14);
        lblUsuarioCadastro.setFont(font);
        lblSenhaCadastro.setFont(font);
        lblEmailCadastro.setFont(font);
        tfUsuarioCadastro.setFont(font);
        pfSenhaCadastro.setFont(font);
        tfEmailCadastro.setFont(font);
        btnCadastrar.setFont(new Font("Arial", Font.BOLD, 16));
        btnVoltar.setFont(new Font("Arial", Font.BOLD, 16));

        // Adicionando bordas aos campos de texto
        tfUsuarioCadastro.setBackground(new Color(245, 245, 245)); // Fundo suave
        pfSenhaCadastro.setBackground(new Color(245, 245, 245));
        tfEmailCadastro.setBackground(new Color(245, 245, 245));
        tfUsuarioCadastro.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        pfSenhaCadastro.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        tfEmailCadastro.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        // Estilizando os botões
        btnCadastrar.setBackground(new Color(40, 120, 200)); // Azul mais forte
        btnCadastrar.setForeground(Color.WHITE);
        btnCadastrar.setFocusPainted(false);
        btnCadastrar.setBorder(BorderFactory.createEmptyBorder());

        btnVoltar.setBackground(new Color(240, 240, 240));  // Fundo claro
        btnVoltar.setForeground(new Color(40, 120, 200));  // Texto azul
        btnVoltar.setFocusPainted(false);
        btnVoltar.setBorder(BorderFactory.createEmptyBorder());

        // Painel central onde os campos e botões ficam
        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new GridLayout(4, 2, 10, 10)); // 4 linhas, 2 colunas
        panelCenter.setBackground(Color.WHITE);

        // Adicionando os componentes ao painel
        panelCenter.add(lblUsuarioCadastro);
        panelCenter.add(tfUsuarioCadastro);
        panelCenter.add(lblSenhaCadastro);
        panelCenter.add(pfSenhaCadastro);
        panelCenter.add(lblEmailCadastro);
        panelCenter.add(tfEmailCadastro);
        panelCenter.add(new JLabel());  // Espaço vazio para ajustar o botão
        panelCenter.add(btnCadastrar);

        // Adicionando o botão "Voltar" abaixo do botão "Cadastrar"
        JPanel panelBotoes = new JPanel();
        panelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER)); // Centraliza os botões
        panelBotoes.setBackground(Color.WHITE);
        panelBotoes.add(btnVoltar); // Adiciona o botão "Voltar"

        // Adiciona o painel central e o painel de botões ao painel principal
        contentPanel.add(panelCenter, BorderLayout.CENTER);
        contentPanel.add(panelBotoes, BorderLayout.SOUTH); // Coloca o painel de botões na parte inferior

        // Coloca o painel de conteúdo no JFrame
        getContentPane().add(contentPanel);

        // Ação do botão de cadastro
        btnCadastrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TelaLogin Login = new TelaLogin();

                //instanciando classes necessarias para conexao com banco
                Conexao conx = new Conexao();
                Connection conexao = conx.getConexao();
                UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);

                //Armazenando valores em variaveis
                String nome = tfUsuarioCadastro.getText();
                String email = tfEmailCadastro.getText();
                String senha = pfSenhaCadastro.getText();

                Usuario usuario = new Usuario(nome, email, senha);

                //Executando o codigo no sql para inserir os valores no banco
                try {
                    usuarioDAO.inserir(usuario);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Usuario ja existente no sistema");
                    throw new RuntimeException(ex);
                }

                JOptionPane.showMessageDialog(null, "Cadastro realizado com sucesso!");
                Login.setVisible(true); // Abre a tela de login
                dispose(); // Fecha a tela de cadastro atual
            }
        });

        // Ação do botão "Voltar"
        btnVoltar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TelaLogin Login = new TelaLogin();
                Login.setVisible(true); // Abre a tela de login
                dispose(); // Fecha a tela de cadastro atual
            }
        });
    }
}

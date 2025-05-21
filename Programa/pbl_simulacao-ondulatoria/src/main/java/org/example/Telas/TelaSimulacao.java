package org.example.Telas;

// Importação de bibliotecas para interface e simulação
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Importação de biblioteca para criação de gráficos
import org.example.Cálculos.Calculos;
import org.example.Integrações.Conexao;
import org.example.Integrações.SimulacaoDAO;
import org.example.Objetos.Onda;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

// Importação de biblioteca para criação de documento pdf
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

// Classe Simulacao
public class TelaSimulacao extends JFrame {

    // Declaração de objetos
    Onda obj_onda = new Onda();
    Calculos obj_calculos = new Calculos();
    private String nome_user;

    // Declaração de variáveis iniciais
    private int duracaoSimulacao = 0;
    private double erroMaximo = 0;

    public TelaSimulacao() {

        // Título da janela
        setTitle("Wave Simulator");
        setSize(1360, 1024);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Painéis principais
        JPanel painelSuperior = new JPanel(new BorderLayout()); // Apenas um painel superior
        TelaSimulacao.PainelParametros painelParametros = new TelaSimulacao.PainelParametros();
        TelaSimulacao.PainelSimulacao painelSimulacao = new TelaSimulacao.PainelSimulacao();

        // Configuração dos painéis
        painelSuperior.add(painelParametros, BorderLayout.NORTH); // Parâmetros no topo
        add(painelSuperior, BorderLayout.NORTH);
        add(painelSimulacao, BorderLayout.CENTER); // Simulação no restante da tela

        // Passar referência do painel de simulação ao painel de parâmetros
        painelParametros.setPainelSimulacao(painelSimulacao);
        painelSimulacao.setPainelParametros(painelParametros);
    }

    // Painel de parâmetros
    private class PainelParametros extends JPanel {

        // Declaração de campos para receber os parâmetros
        private JTextField campoFrequencia;
        private JTextField campoComprimentoOnda;
        private JTextField campoDuracaoSimulacao;
        private JTextField campoErroMaximo;
        private JLabel cronometroLabel;

        // Declaração dos botões
        private JButton botaoNovaOnda;
        private JButton botaoPause;
        private JButton botaoGerarPDF;
        private JButton botaoHistorico;

        // Declaração painelSimulacao
        private TelaSimulacao.PainelSimulacao painelSimulacao;

        // Variável booleana para pausar a simulação ou não
        private boolean pausado = false;
        private JScrollPane scrollPaneHistorico; // Painel scrollável para histórico
        private JPanel painelHistorico; // Painel que contém os dados do histórico
        private boolean historicoAberto = false; // Controle de estado (aberto ou fechado)

        public PainelParametros() {
            setLayout(new BorderLayout());

            // Título do painel parâmetros
            setBorder(BorderFactory.createTitledBorder("Parâmetros"));

            // Painel para os campos de entrada
            JPanel painelCampos = new JPanel(new GridLayout(4, 2, 10, 10));

            // Parâmetro frequência
            JLabel freqLabel = new JLabel("Frequência (Hz):");
            campoFrequencia = new JTextField(String.valueOf(obj_onda.getFrequencia()));

            // Parâmetro comprimento
            JLabel comprimentoLabel = new JLabel("Comprimento de onda (cm):");
            campoComprimentoOnda = new JTextField(String.valueOf(obj_onda.getComprimentoOnda()));

            // Parâmetro duracao
            JLabel duracaoLabel = new JLabel("Duração (s):");
            campoDuracaoSimulacao = new JTextField(String.valueOf(duracaoSimulacao));

            // Parâmetro erro máximo
            JLabel erroLabel = new JLabel("Erro máximo:");
            campoErroMaximo = new JTextField("2"); // Valor padrão

            painelCampos.add(freqLabel);
            painelCampos.add(campoFrequencia);
            painelCampos.add(comprimentoLabel);
            painelCampos.add(campoComprimentoOnda);
            painelCampos.add(duracaoLabel);
            painelCampos.add(campoDuracaoSimulacao);
            painelCampos.add(erroLabel);
            painelCampos.add(campoErroMaximo);

            // Painel scrollável para o histórico
            painelHistorico = new JPanel();
            painelHistorico.setLayout(new BoxLayout(painelHistorico, BoxLayout.Y_AXIS));
            painelHistorico.add(new JLabel("Histórico de parâmetros:")); // Título da aba

            scrollPaneHistorico = new JScrollPane(painelHistorico);
            scrollPaneHistorico.setPreferredSize(new Dimension(300, 150));
            scrollPaneHistorico.setVisible(false); // Inicialmente escondido

            // Painel para botão "Histórico" alinhado à direita
            JPanel painelBotaoHistorico = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            botaoHistorico = criarBotaoEstilizado("Histórico", new Color(40, 120, 200), Color.WHITE);
            painelBotaoHistorico.add(botaoHistorico);

            // Adiciona a ação do botão "Histórico"
            // Substituir o exemplo estático pelo carregamento dinâmico
            botaoHistorico.addActionListener(e -> {
                try {
                    Conexao conx = new Conexao();
                    Connection conexao = conx.getConexao();
                    SimulacaoDAO historicoDAO = new SimulacaoDAO(conexao);
                    String usuarioLogado = nome_user;

                    // Obtém o histórico de simulações do banco
                    List<String> historico = historicoDAO.getHistoricoSimulacoes(usuarioLogado);

                    // Verifica se o histórico está vazio
                    if (historico.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Nenhuma simulação encontrada para o usuário.",
                                "Histórico Vazio", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    // Limpa o painel antes de adicionar novos elementos
                    painelHistorico.removeAll();
                    painelHistorico.add(new JLabel("Histórico de Simulações:"));

                    // Adiciona os dados do histórico como botões
                    for (String simulacaoTexto : historico) {
                        System.out.println("Simulação retornada: " + simulacaoTexto);

                        JButton botaoSimulacao = new JButton(simulacaoTexto);

                        botaoSimulacao.addActionListener(ev -> {
                            try {
                                // A string do histórico já está formatada, mas precisamos obter os valores brutos.
                                // Assumimos que os valores numéricos estão na ordem da string formatada:
                                // Frequência: X Hz | Comprimento de onda: Y m | Duração da simulação: Z s | Erro Máximo: W | Data: T

                                String[] partes = simulacaoTexto.trim().split("\\|");
                                if (partes.length >= 4) {
                                    // Extraindo valores brutos diretamente
                                    String frequencia = partes[0].split(":")[1].trim().split(" ")[0]; // Valor numérico da frequência
                                    String comprimento = partes[1].split(":")[1].trim().split(" ")[0]; // Valor numérico do comprimento
                                    String duracao = partes[2].split(":")[1].trim().split(" ")[0]; // Valor numérico da duração
                                    String erroMax = partes[3].split(":")[1].trim(); // Valor numérico do erro máximo


                                    // Preenchendo os campos com os valores extraídos
                                    campoFrequencia.setText(frequencia);
                                    campoComprimentoOnda.setText(String.format("%.2f", comprimento));;
                                    campoDuracaoSimulacao.setText(duracao);
                                    campoErroMaximo.setText(erroMax);

                                    JFrame frame = new JFrame();
                                    frame.setAlwaysOnTop(true);
                                    frame.setLocationRelativeTo(null);
                                    frame.setVisible(false);

                                    JOptionPane.showMessageDialog(frame, "Parâmetros da simulação selecionada aplicados.");
                                } else {
                                    JFrame frame = new JFrame();
                                    frame.setAlwaysOnTop(true);
                                    frame.setLocationRelativeTo(null);
                                    frame.setVisible(false);

                                    JOptionPane.showMessageDialog(frame, "Formato de simulação inválido: " + simulacaoTexto,
                                            "Erro", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (Exception ex) {
                                JFrame frame = new JFrame();
                                frame.setAlwaysOnTop(true);
                                frame.setLocationRelativeTo(null);
                                frame.setVisible(false);

                                JOptionPane.showMessageDialog(frame, "Erro ao processar a simulação: " + ex.getMessage(),
                                        "Erro", JOptionPane.ERROR_MESSAGE);
                            }
                        });

                        painelHistorico.add(botaoSimulacao);
                    }

                    // Atualiza o layout do painel
                    painelHistorico.revalidate();
                    painelHistorico.repaint();

                    historicoAberto = !historicoAberto;
                    scrollPaneHistorico.setVisible(historicoAberto);

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao carregar histórico: " + ex.getMessage(),
                            "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Painel principal com campos e botão "Histórico"
            JPanel painelPrincipal = new JPanel(new BorderLayout());
            painelPrincipal.add(painelCampos, BorderLayout.CENTER);
            painelPrincipal.add(painelBotaoHistorico, BorderLayout.SOUTH);

            // Painel principal do "Histórico" e botões
            JPanel painelHistoricoComBotoes = new JPanel(new BorderLayout());
            painelHistoricoComBotoes.add(painelPrincipal, BorderLayout.NORTH);
            painelHistoricoComBotoes.add(scrollPaneHistorico, BorderLayout.CENTER);

            add(painelHistoricoComBotoes, BorderLayout.CENTER);

            // Painel para os botões principais
            JPanel painelBotoes = new JPanel(new GridLayout(1, 3, 20, 0));
            botaoPause = criarBotaoEstilizado("Pausar/Continuar", new Color(40, 120, 200), Color.WHITE);
            botaoPause.addActionListener(e -> {
                if (painelSimulacao != null) {
                    if (pausado) {
                        painelSimulacao.resumeSimulacao();
                    } else {
                        painelSimulacao.pauseSimulacao();
                    }
                    pausado = !pausado;
                }
            });

            // Botão responsavel pela crição de uma nova simulação
            botaoNovaOnda = criarBotaoEstilizado("Nova Onda", new Color(40, 120, 200), Color.WHITE);
            botaoNovaOnda.addActionListener(e -> {
                try {
                    double frequencia = Double.parseDouble(campoFrequencia.getText());
                    double comprimentoOnda = Double.parseDouble(campoComprimentoOnda.getText());
                    int duracao = Integer.parseInt(campoDuracaoSimulacao.getText());
                    double erro = Double.parseDouble(campoErroMaximo.getText());

                    // Validação de valores
                    if (frequencia <= 0 || frequencia > 0.5) {
                        throw new IllegalArgumentException("A frequência deve ser maior que 0 e menor que 0.5 Hz.");
                    }
                    if (comprimentoOnda < 20 || comprimentoOnda > 200) {
                        throw new IllegalArgumentException("O comprimento de onda deve ser maior que 20 cm e menor que 200 cm.");
                    }
                    if (duracao < 1 || duracao > 10) {
                        throw new IllegalArgumentException("A duração deve estar entre 1 e 10 segundos.");
                    }
                    if (erro < 0 || erro > 10) {
                        throw new IllegalArgumentException("O erro máximo não pode ser negativo e deve ser menor que 10.");
                    }

                    // Atualizar os valores na onda e painel de simulação
                    obj_onda.setFrequencia(frequencia);
                    obj_onda.setComprimentoOnda(comprimentoOnda);
                    duracaoSimulacao = duracao * 1000; // Converte duração para milissegundos
                    erroMaximo = erro;
                    painelSimulacao.resetSimulacao(frequencia, comprimentoOnda, duracaoSimulacao, erroMaximo);
                    botaoPause.setEnabled(true);

                    // Adicionando os valores da onda no banco
                    Conexao conx = new Conexao();
                    Connection conexao = conx.getConexao();
                    SimulacaoDAO simulacaoDAO = new SimulacaoDAO(conexao);

                    try {
                        simulacaoDAO.inserir(nome_user, obj_onda.getFrequencia(), obj_onda.getComprimentoOnda(), (duracaoSimulacao / 1000), erroMaximo);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Erro ao salvar a simulação no banco: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
                        throw new RuntimeException(ex);
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Por favor, insira valores numéricos válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Valor Inválido", JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro inesperado: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }


            });


            // Geração do botão de gerar pdf no painel
            botaoGerarPDF = criarBotaoEstilizado("Gerar relatório com gráficos", new Color(240, 240, 240), new Color(40, 120, 200));
            botaoGerarPDF.addActionListener(e -> {
                try {
                    obj_onda.setFrequencia(Double.parseDouble(campoFrequencia.getText()));
                    obj_onda.setComprimentoOnda(Double.parseDouble(campoComprimentoOnda.getText()));
                    duracaoSimulacao = Integer.parseInt(campoDuracaoSimulacao.getText());
                    gerarPDF(obj_onda.getFrequencia(), obj_onda.getComprimentoOnda(), duracaoSimulacao);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erro ao gerar PDF: " + ex.getMessage());
                }
            });

            // Adicionar os botões no painel
            painelBotoes.add(botaoNovaOnda);
            painelBotoes.add(botaoPause);
            painelBotoes.add(botaoGerarPDF);

            // Posicionar os botões no painel
            add(painelPrincipal, BorderLayout.NORTH);
            add(painelBotoes, BorderLayout.SOUTH);
        }

        // Estilização dos botões
        private JButton criarBotaoEstilizado(String texto, Color corFundo, Color corTexto) {
            JButton botao = new JButton(texto);
            botao.setFont(new Font("Arial", Font.BOLD, 16));
            botao.setBackground(corFundo);
            botao.setForeground(corTexto);
            botao.setFocusPainted(false);
            botao.setBorder(BorderFactory.createEmptyBorder());
            botao.setPreferredSize(new Dimension(200, 50));
            return botao;
        }

        // Encapsulamento do painel
        public void setPainelSimulacao(TelaSimulacao.PainelSimulacao painelSimulacao) {
            this.painelSimulacao = painelSimulacao;
        }

        // Conversão do valor do cronômetro
        public void atualizarCronometro(double tempo) {
            cronometroLabel.setText(String.format("Tempo: %.1f s", tempo));
        }

        private void gerarPDF(double frequencia, double comprimento, int duracaoSimulacao) throws Exception {
            // Definir o diretório base dentro do projeto
            String diretorioBase = "Graficos";
            java.io.File pasta = new java.io.File(diretorioBase);

            // Cria o diretório caso ele não exista
            if (!pasta.exists()) {
                pasta.mkdirs();
            }

            // Obter a data e hora atuais no formato desejado
            java.time.LocalDateTime agora = java.time.LocalDateTime.now();
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            String dataHoraFormatada = agora.format(formatter);

            // Construir o nome do arquivo com o nome do usuário e a data/hora
            String caminhoPDF = String.format("%s/Graficos_Onda_%s_%s.pdf", diretorioBase, nome_user, dataHoraFormatada);

            // Criação de array para posterior geração dos gráficos
            List<String> imagens = new ArrayList<>();
            int numGraficos = (int) Math.ceil(duracaoSimulacao * 20);
            double tempoIncremento = 0.05;

            // Geração dos gráficos com a chamada da função "gerarGrafico"
            for (int i = 0; i < numGraficos; i++) {
                double tempo = i * tempoIncremento;
                String caminhoImagem = diretorioBase + "/grafico_" + i + ".png";
                gerarGrafico(frequencia, comprimento, tempo, caminhoImagem);
                imagens.add(caminhoImagem);
            }

            // Adiciona o último gráfico se não estiver incluso no loop principal
            if ((numGraficos - 1) * tempoIncremento < duracaoSimulacao) {
                double tempo = duracaoSimulacao;
                String caminhoImagem = diretorioBase + "/grafico_final.png";
                gerarGrafico(frequencia, comprimento, tempo, caminhoImagem);
                imagens.add(caminhoImagem);
            }

            // Cria o PDF com o nome personalizado
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(caminhoPDF));
            document.open();

            // Laço para colocar as imagens no pdf
            for (String imagem : imagens) {
                Image img = Image.getInstance(imagem);

                // Ajusta o tamanho da imagem no pdf
                img.scaleToFit(500, 300);

                // Centraliza as imagens no pdf
                img.setAlignment(Image.ALIGN_CENTER);
                document.add(img);

                // Apagar o arquivo de imagem após adicioná-lo ao PDF
                new java.io.File(imagem).delete();
            }
            document.close();

            JFrame frame = new JFrame();
            frame.setAlwaysOnTop(true);
            frame.setLocationRelativeTo(null);
            frame.setVisible(false);

            JOptionPane.showMessageDialog(frame, "PDF gerado com sucesso no caminho do projeto: " + caminhoPDF, "PDF Gerado", JOptionPane.INFORMATION_MESSAGE);
        }

        // Função para geração dos gráficos
        private void gerarGrafico(double frequencia, double comprimento, double tempo, String caminhoImagem) throws Exception {

            // Legenda do gráfico
            XYSeries series = new XYSeries("Onda no tempo t = " + String.format("%.2f", tempo) + "s");

            double comprimentoMetros = Calculos.convert.cm_m(comprimento);

            // Traçar linha do gráfico
            for (double x = 0; x <= 1; x += 0.01) {
                double y = Math.sin(2 * Math.PI * frequencia * tempo - (2 * Math.PI / comprimentoMetros) * x);
                series.add(x, y);
            }

            XYSeriesCollection dataset = new XYSeriesCollection(series);
            JFreeChart chart = ChartFactory.createXYLineChart(
                    // Título do gráfico e nomes das linhas x e y
                    "Onda - t = " + String.format("%.2f", tempo) + "s",
                    "Posição (m)",
                    "Amplitude",
                    dataset
            );

            ChartUtils.saveChartAsPNG(new java.io.File(caminhoImagem), chart, 800, 600);
        }
    }

    // Painel simulação
    private class PainelSimulacao extends JPanel implements ActionListener {

        // Declaração de variáveis
        private Timer timer;
        private double frequencia;
        private double comprimento;
        private int duracaoSimulacao;
        private double erroMaximo;
        private long tempoInicio;
        private long tempoDecorridoPausado = 0;
        private boolean pausado = false;

        // Receber os valores dos parâmetros
        private TelaSimulacao.PainelParametros painelParametros;
        private JLabel velocidadeLabel;
        private JLabel comprimentoLabel;
        private JLabel frequenciaLabel;
        private JLabel amplitudeLabel;
        private JLabel erroMaximoLabel;
        private JLabel cronometroLabel;

        // Controle do deslocamento da onda
        private double phaseShift = 0;
        // Margem do painel
        private final int margem = 50;

        // Cria o painel de simulação
        public PainelSimulacao() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createTitledBorder("Simulação"));
            setBackground(Color.WHITE);

            // Painel para exibir informações
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new GridLayout(6, 1, 10, 10));
            infoPanel.setBackground(Color.WHITE);

            // Fonte para as informações
            Font infoFont = new Font("Arial", Font.BOLD, 14);

            velocidadeLabel = criarLabel("Velocidade: 0 cm/s", infoFont);
            comprimentoLabel = criarLabel("Comprimento de onda: 0 cm", infoFont);
            frequenciaLabel = criarLabel("Frequência: 0 Hz", infoFont);
            amplitudeLabel = criarLabel("Amplitude: 0 m", infoFont);
            erroMaximoLabel = criarLabel("Erro máximo: 10^-0", infoFont);
            cronometroLabel = criarLabel("Tempo: 0.0 s", infoFont);

            // Adiciona as informações ao painel
            infoPanel.add(velocidadeLabel);
            infoPanel.add(comprimentoLabel);
            infoPanel.add(frequenciaLabel);
            infoPanel.add(amplitudeLabel);
            infoPanel.add(erroMaximoLabel);
            infoPanel.add(cronometroLabel);

            add(infoPanel, BorderLayout.NORTH);

            timer = new Timer(50, this);
        }

        //Função para criação de labels
        private JLabel criarLabel(String texto, Font font) {
            JLabel label = new JLabel(texto);
            label.setFont(font);
            label.setHorizontalAlignment(SwingConstants.LEFT);
            return label;
        }

        // Método para associar o painel de parâmetros
        public void setPainelParametros(TelaSimulacao.PainelParametros painelParametros) {
            this.painelParametros = painelParametros;
        }

        // Função para resetar a simulação
        public void resetSimulacao(double frequencia, double comprimento, int duracaoSimulacao, double erroMaximo) {
            this.frequencia = obj_onda.getFrequencia();
            this.comprimento = Calculos.convert.cm_m(obj_onda.getComprimentoOnda());
            this.duracaoSimulacao = duracaoSimulacao;
            this.erroMaximo = erroMaximo;
            this.tempoInicio = System.currentTimeMillis();
            this.tempoDecorridoPausado = 0;
            this.phaseShift = 0;
            this.pausado = false;

            atualizarInformacoes();
            timer.start();
            repaint();
        }

        // Função para atualizar o valor das varíáveis pós-criação de uma nova onda
        private void atualizarInformacoes() {
            double velocidade = obj_calculos.calcularVelocidade(obj_onda.getFrequencia(), obj_onda.getComprimentoOnda());
            velocidadeLabel.setText(String.format("Velocidade: %.2f cm/s", velocidade));
            comprimentoLabel.setText(String.format("Comprimento de onda: %.2f cm ", obj_onda.getComprimentoOnda()));
            frequenciaLabel.setText(String.format("Frequência: %.2f Hz", obj_onda.getFrequencia()));
            amplitudeLabel.setText("Amplitude: 1 m");
            erroMaximoLabel.setText(String.format("Erro máximo: 10^-%.0f", erroMaximo));
            cronometroLabel.setText("Tempo: 0.0 s");
        }

        // Função para pausar simulação
        public void pauseSimulacao() {
            if (!pausado) {
                timer.stop();
                tempoDecorridoPausado = System.currentTimeMillis() - tempoInicio;
                pausado = true;
            }
        }

        // Função para despausar simulação
        public void resumeSimulacao() {
            if (pausado) {
                tempoInicio = System.currentTimeMillis() - tempoDecorridoPausado;
                timer.start();
                pausado = false;
            }
        }

        // Realiza o desenrolar do cronômetro e a da velocidade da onda
        @Override
        public void actionPerformed(ActionEvent e) {
            if (!pausado) {
                long tempoDecorrido = System.currentTimeMillis() - tempoInicio;

                if (tempoDecorrido >= duracaoSimulacao) {
                    timer.stop();
                } else {
                    double comprimentoEmMetros = Calculos.convert.cm_m(obj_onda.getComprimentoOnda());

                    // Atualiza o deslocamento da onda
                    phaseShift += obj_calculos.calcularVelocidade(obj_onda.getFrequencia(), comprimentoEmMetros);
                    cronometroLabel.setText(String.format("Tempo: %.1f s", tempoDecorrido / 1000.0));
                    repaint();
                }
            }
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;

            int offsetY = 100;

            // Configurações de dimensão para responsividade
            int width = getWidth() - 2 * margem; // Largura útil do painel
            int height = getHeight() - 2 * margem - offsetY; // Altura útil do painel
            int centerY = getHeight() / 2 + offsetY; // Posição central no eixo Y
            int amplitudePixels = (int) (0.1 * getHeight()); // Amplitude fixada como 0.1m (convertida para pixels)

            // Fundo do painel
            g2d.setColor(Color.WHITE);
            g2d.fillRect(margem, margem, width, height);

            // Converte o comprimento de onda para pixels
            double comprimentoPixels = comprimento * width;

            // Desenha linhas de referência
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 12));

            // Desenha barra fixa vertical à esquerda
            int barraX = margem; // Posição fixa no eixo X (margem à esquerda)
            int barraYInicial = margem * 6; // Ajusta o início da barra conforme o tamanho desejado
            int barraYFinal = getHeight() - (margem + 50); // Ajusta o final da barra com um limite proporcional

            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(barraX, barraYInicial, barraX, barraYFinal);

            // Linha superior da amplitude
            g2d.drawLine(margem, centerY - amplitudePixels, margem + width, centerY - amplitudePixels);
            g2d.drawString("1 m", margem + 5, centerY - amplitudePixels - 5);

            // Linha do centro
            g2d.drawLine(margem, centerY, margem + width, centerY);
            g2d.drawString("0 m", margem + 5, centerY - 5);

            // Linha inferior da amplitude
            g2d.drawLine(margem, centerY + amplitudePixels, margem + width, centerY + amplitudePixels);
            g2d.drawString("-1 m", margem + 5, centerY + amplitudePixels + 15);

            // Desenha régua no eixo X
            for (int i = 0; i <= 10; i++) {
                int x = margem + i * width / 10;
                g2d.drawLine(x, centerY - 5, x, centerY + 5);
                g2d.drawString(String.format("%.1f m", i * 0.1), x - 15, centerY + 30);
            }

            // Desenha a onda
            if (obj_onda.getFrequencia() > 0 && obj_onda.getComprimentoOnda() > 0) {
                g2d.setColor(new Color(40, 120, 200)); // Azul para a onda
                g2d.setStroke(new BasicStroke(2)); // Linha da onda
                int lastX = -1;
                int lastY = 0;

                for (int x = margem; x < margem + width; x++) {

                    double angulo = (x * 2 * Math.PI / comprimentoPixels);
                    int y = (int) (centerY + amplitudePixels * Calculos.SenoTaylor.calcularSeno((angulo - phaseShift), erroMaximo));
                    if (lastX != -1) {
                        g2d.drawLine(lastX, lastY, x, y);
                    }
                    lastX = x;
                    lastY = y;
                }

                // Desenha a bolinha no centro do painel
                int fixedX = (width / 2) + margem;
                int ballY = (int) (centerY + amplitudePixels * Calculos.SenoTaylor.calcularSeno(((fixedX * 2 * Math.PI / comprimentoPixels) - phaseShift), erroMaximo));
                g2d.setColor(Color.RED);
                g2d.fillOval(fixedX - 8, ballY - 8, 16, 16);

                // Exibe as coordenadas da bolinha
                g2d.setColor(Color.BLACK);
                g2d.drawString(String.format("(%.2f m; %.2f m)", (fixedX - margem) / (double) width, (centerY - ballY) / (double) amplitudePixels),
                        fixedX + 20, ballY - 10);
            }
        }
    }

    public int getDuracaoSimulacao() {
        return duracaoSimulacao;
    }

    public void setDuracaoSimulacao(int duracaoSimulacao) {
        this.duracaoSimulacao = duracaoSimulacao;
    }

    public double getErroMaximo() {
        return erroMaximo;
    }

    public void setErroMaximo(double erroMaximo) {
        this.erroMaximo = erroMaximo;
    }

    public Onda getObj_onda() {
        return obj_onda;
    }

    public void setObj_onda(Onda obj_onda) {
        this.obj_onda = obj_onda;
    }

    public String getNome_user() {
        return nome_user;
    }

    public void setNome_user(String nome_user) {
        this.nome_user = nome_user;
    }
}
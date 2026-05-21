import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
 
/*
  Classe principal do Sistema Acadêmico.
  - 'periodo' não é mais persistido no banco; é selecionado via RadioButton na aba Curso
  - O período selecionado aparece no cabeçalho do Boletim
  - Botões com ícones do sistema (UIManager) nas abas Curso e Notas e Faltas
  - Botões posicionados na parte INFERIOR das abas
  - Aba Boletim com tabela + cabeçalho colorido por situação
  - RGM valida: apenas números permitidos
  - Data de Nascimento: JSpinner com SpinnerDateModel (calendário nativo, sem .jar externo)
*/
public class SistemaAcademico extends JFrame {
 
    // DAOs
    private final AlunoDAO     alunoDAO     = new AlunoDAO();
    private final CursoDAO     cursoDAO     = new CursoDAO();
    private final NotaFaltaDAO notaFaltaDAO = new NotaFaltaDAO();
 
    // Estado
    private Aluno alunoAtual     = null;
    private int   idNotaEditando = -1;
 
    // Componentes: Dados Pessoais
    private JTextField          txtRgm;
    private JTextField          txtNome;
    private JSpinner            spnDataNasc;   // ← JSpinner com calendário nativo
    private JFormattedTextField txtCpf;
    private JTextField          txtEmail;
    private JTextField          txtEndereco;
    private JTextField          txtMunicipio;
    private JComboBox<String>   cmbUf;
    private JFormattedTextField txtCelular;
 
    // Componentes: Curso
    private JComboBox<Curso>  cmbCurso;
    private JComboBox<String> cmbCampus;
    // RadioButtons de período — seleção apenas na interface, NÃO persiste no banco
    private JRadioButton      rbMatutino, rbVespertino, rbNoturno;
 
    // Componentes: Notas e Faltas
    private JTextField        txtRgmNota;
    private JLabel            lblNomeNota;
    private JLabel            lblCursoNota;
    private JComboBox<String> cmbDisciplina;
    private JComboBox<String> cmbSemestre;
    private JComboBox<Double> cmbNota;
    private JTextField        txtFaltas;
    private JTable            tabelaNotas;
    private DefaultTableModel modeloTabelaNotas;
 
    // Componentes: Boletim
    private JTable            tabelaBoletim;
    private DefaultTableModel modeloBoletim;
    private JLabel            lblBoletimRgm, lblBoletimNome, lblBoletimCurso, lblBoletimPeriodo;
 
    // TabbedPane
    private JTabbedPane tabbedPane;
 
 
    // CONSTRUTOR
    public SistemaAcademico() {
        super("Sistema Acadêmico - UNICID");
        configurarJanela();
        criarMenu();
        criarTabbedPane();
        carregarDadosIniciais();
    }
 
 
    // CONFIGURAÇÃO DA JANELA
    private void configurarJanela() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(620, 480);
        setLocationRelativeTo(null);
        setResizable(true);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
    }
 
 
    // CRIAÇÃO DO MENU
    private void criarMenu() {
        JMenuBar menuBar = new JMenuBar();
 
        // Menu Aluno
        JMenu menuAluno = new JMenu("Aluno");
        menuAluno.setMnemonic(KeyEvent.VK_A);
 
        JMenuItem miSalvar    = new JMenuItem("Salvar");
        miSalvar.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        JMenuItem miAlterar   = new JMenuItem("Alterar");
        JMenuItem miConsultar = new JMenuItem("Consultar");
        JMenuItem miExcluir   = new JMenuItem("Excluir");
        JMenuItem miSair      = new JMenuItem("Sair");
        miSair.setAccelerator(KeyStroke.getKeyStroke("shift R"));
 
        miSalvar.addActionListener(e    -> salvarAluno());
        miAlterar.addActionListener(e   -> alterarAluno());
        miConsultar.addActionListener(e -> consultarAluno());
        miExcluir.addActionListener(e   -> excluirAluno());
        miSair.addActionListener(e      -> System.exit(0));
 
        menuAluno.add(miSalvar);
        menuAluno.add(miAlterar);
        menuAluno.add(miConsultar);
        menuAluno.add(miExcluir);
        menuAluno.addSeparator();
        menuAluno.add(miSair);
 
        // Menu Notas e Faltas
        JMenu menuNotas = new JMenu("Notas e Faltas");
 
        JMenuItem miSalvarNota    = new JMenuItem("Salvar");
        JMenuItem miAlterarNota   = new JMenuItem("Alterar");
        miAlterarNota.setAccelerator(KeyStroke.getKeyStroke("ctrl A"));
        JMenuItem miExcluirNota   = new JMenuItem("Excluir");
        JMenuItem miConsultarNota = new JMenuItem("Consultar");
 
        miSalvarNota.addActionListener(e    -> salvarNota());
        miAlterarNota.addActionListener(e   -> prepararAlteracaoNota());
        miExcluirNota.addActionListener(e   -> excluirNota());
        miConsultarNota.addActionListener(e -> consultarNotas());
 
        menuNotas.add(miSalvarNota);
        menuNotas.add(miAlterarNota);
        menuNotas.add(miExcluirNota);
        menuNotas.add(miConsultarNota);
 
        // Menu Ajuda
        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem miSobre = new JMenuItem("Sobre");
        miSobre.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Sistema Acadêmico v1.0\nProgramação Orientada a Objetos - UNICID\n\nDesenvolvido em Java com Swing + MySQL",
                "Sobre", JOptionPane.INFORMATION_MESSAGE));
        menuAjuda.add(miSobre);
 
        menuBar.add(menuAluno);
        menuBar.add(menuNotas);
        menuBar.add(menuAjuda);
        setJMenuBar(menuBar);
    }
 
 
    // CRIAÇÃO DO TABBEDPANE
    private void criarTabbedPane() {
        tabbedPane = new JTabbedPane();
 
        tabbedPane.addTab("Dados Pessoais", criarPainelDadosPessoais());
        tabbedPane.addTab("Curso",          criarPainelCurso());
        tabbedPane.addTab("Notas e Faltas", criarPainelNotasFaltas());
        tabbedPane.addTab("Boletim",        criarPainelBoletim());
 
        // Ao entrar na aba Boletim, atualiza os dados incluindo o período selecionado
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 3 && alunoAtual != null) {
                atualizarBoletim(alunoAtual.getRgm());
            }
        });
 
        add(tabbedPane, BorderLayout.CENTER);
    }
 
 
    // ABA 1: DADOS PESSOAIS
    private JPanel criarPainelDadosPessoais() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill   = GridBagConstraints.HORIZONTAL;
 
        // RGM + Nome
        g.gridx=0; g.gridy=0; g.weightx=0; p.add(new JLabel("RGM"), g);
        txtRgm = new JTextField(10);
        g.gridx=1; g.weightx=0.3; p.add(txtRgm, g);
 
        g.gridx=2; g.weightx=0; p.add(new JLabel("Nome"), g);
        txtNome = new JTextField(20);
        g.gridx=3; g.weightx=0.7; p.add(txtNome, g);
 
        // Data de Nascimento com JSpinner (nativo, sem .jar externo) + CPF
        g.gridx=0; g.gridy=1; g.weightx=0; p.add(new JLabel("Data de Nascimento"), g);
 
        // SpinnerDateModel começa em 01/01/2000, permite qualquer data
        SpinnerDateModel modeloData = new SpinnerDateModel(
            new java.util.Date(), null, null, Calendar.DAY_OF_MONTH
        );
        spnDataNasc = new JSpinner(modeloData);
        // Editor com formato brasileiro
        JSpinner.DateEditor editorData = new JSpinner.DateEditor(spnDataNasc, "dd/MM/yyyy");
        spnDataNasc.setEditor(editorData);
        g.gridx=1; g.weightx=0.3; p.add(spnDataNasc, g);
 
        g.gridx=2; g.weightx=0; p.add(new JLabel("CPF"), g);
        txtCpf = criarCampoMascarado("###.###.###-##");
        g.gridx=3; g.weightx=0.7; p.add(txtCpf, g);
 
        // Email
        g.gridx=0; g.gridy=2; g.weightx=0; p.add(new JLabel("Email"), g);
        txtEmail = new JTextField();
        g.gridx=1; g.gridwidth=3; g.weightx=1; p.add(txtEmail, g);
        g.gridwidth=1;
 
        // Endereço
        g.gridx=0; g.gridy=3; g.weightx=0; p.add(new JLabel("End."), g);
        txtEndereco = new JTextField();
        g.gridx=1; g.gridwidth=3; g.weightx=1; p.add(txtEndereco, g);
        g.gridwidth=1;
 
        // Município + UF
        g.gridx=0; g.gridy=4; g.weightx=0; p.add(new JLabel("Município"), g);
        txtMunicipio = new JTextField(15);
        g.gridx=1; g.weightx=0.5; p.add(txtMunicipio, g);
 
        g.gridx=2; g.weightx=0; p.add(new JLabel("UF"), g);
        cmbUf = new JComboBox<>(obterUFs());
        cmbUf.setPreferredSize(new Dimension(55, 25));
        g.gridx=3; g.weightx=0; p.add(cmbUf, g);
 
        // Celular
        g.gridx=0; g.gridy=5; g.weightx=0; p.add(new JLabel("Celular"), g);
        txtCelular = criarCampoMascarado("(##)#####-####");
        g.gridx=1; g.weightx=0.3; p.add(txtCelular, g);
 
        return p;
    }
 
 
    // ABA 2: CURSO
    private JPanel criarPainelCurso() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
 
        // Formulário
        JPanel pForm = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(8, 8, 8, 8);
        g.fill   = GridBagConstraints.HORIZONTAL;
 
        g.gridx=0; g.gridy=0; g.weightx=0; pForm.add(new JLabel("Curso"), g);
        cmbCurso = new JComboBox<>();
        g.gridx=1; g.weightx=1; pForm.add(cmbCurso, g);
 
        g.gridx=0; g.gridy=1; g.weightx=0; pForm.add(new JLabel("Campus"), g);
        cmbCampus = new JComboBox<>();
        g.gridx=1; g.weightx=1; pForm.add(cmbCampus, g);
 
        // Período: selecionado na interface, NÃO é salvo no banco
        g.gridx=0; g.gridy=2; g.weightx=0; pForm.add(new JLabel("Período"), g);
        JPanel pPeriodo = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rbMatutino   = new JRadioButton("Matutino");
        rbVespertino = new JRadioButton("Vespertino");
        rbNoturno    = new JRadioButton("Noturno");
        ButtonGroup grp = new ButtonGroup();
        grp.add(rbMatutino); grp.add(rbVespertino); grp.add(rbNoturno);
        rbNoturno.setSelected(true); // padrão
        pPeriodo.add(rbMatutino);
        pPeriodo.add(rbVespertino);
        pPeriodo.add(rbNoturno);
        g.gridx=1; pForm.add(pPeriodo, g);
 
        g.gridx=0; g.gridy=3; g.gridwidth=2; g.weightx=1;
        pForm.add(new JLabel(""), g);
        g.gridwidth=1;
 
        p.add(pForm, BorderLayout.NORTH);
 
        // Botões com ícones do sistema (parte inferior)
        JPanel pBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
 
        JButton btnSair   = criarBotaoIconeSistema("Sair",   "shutdown",    UIManager.getIcon("OptionPane.errorIcon"));
        JButton btnAbrir  = criarBotaoIconeSistema("Abrir",  "abrir",       UIManager.getIcon("FileView.directoryIcon"));
        JButton btnNovo   = criarBotaoIconeSistema("Novo",   "novo",        UIManager.getIcon("FileView.fileIcon"));
        JButton btnSalvar = criarBotaoIconeSistema("Salvar", "salvar",      UIManager.getIcon("FileChooser.floppyDriveIcon"));
        JButton btnInfo   = criarBotaoIconeSistema("Java",   "informações", UIManager.getIcon("OptionPane.informationIcon"));
 
        btnSair.addActionListener(e   -> System.exit(0));
        btnAbrir.addActionListener(e  -> consultarAluno());
        btnNovo.addActionListener(e   -> limparTela());
        btnSalvar.addActionListener(e -> salvarAluno());
        btnInfo.addActionListener(e   -> JOptionPane.showMessageDialog(this,
                "Java + MySQL + Swing\nProgramação Orientada a Objetos", "Info",
                JOptionPane.INFORMATION_MESSAGE));
 
        pBotoes.add(btnSair);
        pBotoes.add(btnAbrir);
        pBotoes.add(btnNovo);
        pBotoes.add(btnSalvar);
        pBotoes.add(btnInfo);
 
        p.add(pBotoes, BorderLayout.SOUTH);
        return p;
    }
 
 
    // ABA 3: NOTAS E FALTAS
    private JPanel criarPainelNotasFaltas() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
 
        // Formulário
        JPanel pForm = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.fill   = GridBagConstraints.HORIZONTAL;
 
        // RGM + Buscar + Nome
        g.gridx=0; g.gridy=0; g.weightx=0; pForm.add(new JLabel("RGM"), g);
        txtRgmNota = new JTextField(10);
        g.gridx=1; g.weightx=0.3; pForm.add(txtRgmNota, g);
 
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarAlunoParaNota());
        g.gridx=2; g.weightx=0; pForm.add(btnBuscar, g);
 
        lblNomeNota = new JLabel(" ");
        g.gridx=3; g.weightx=0.7; pForm.add(lblNomeNota, g);
 
        // Curso do aluno
        lblCursoNota = new JLabel(" ");
        g.gridx=0; g.gridy=1; g.gridwidth=4; g.weightx=1; pForm.add(lblCursoNota, g);
        g.gridwidth=1;
 
        // Disciplina
        g.gridx=0; g.gridy=2; g.weightx=0; pForm.add(new JLabel("Disciplina"), g);
        cmbDisciplina = new JComboBox<>(new String[]{
            "Programação Orientada a Objetos",
            "Estrutura de Dados",
            "Banco de Dados",
            "Engenharia de Software",
            "Redes de Computadores",
            "Sistemas Operacionais",
            "Cálculo I",
            "Álgebra Linear",
            "Interface Humano-Computador",
            "Estágio Supervisionado"
        });
        cmbDisciplina.setEditable(true);
        g.gridx=1; g.gridwidth=3; g.weightx=1; pForm.add(cmbDisciplina, g);
        g.gridwidth=1;
 
        // Semestre + Nota + Faltas
        g.gridx=0; g.gridy=3; g.weightx=0; pForm.add(new JLabel("Semestre"), g);
        cmbSemestre = new JComboBox<>(new String[]{
            "2026-1","2026-2","2025-2","2025-1","2024-2","2024-1","2023-2","2023-1"
        });
        g.gridx=1; g.weightx=0.3; pForm.add(cmbSemestre, g);
 
        g.gridx=2; g.weightx=0; pForm.add(new JLabel("Nota"), g);
        Double[] notas = {0.0,0.5,1.0,1.5,2.0,2.5,3.0,3.5,4.0,4.5,5.0,
                          5.5,6.0,6.5,7.0,7.5,8.0,8.5,9.0,9.5,10.0};
        cmbNota = new JComboBox<>(notas);
        cmbNota.setSelectedItem(5.0);
        g.gridx=3; g.weightx=0; pForm.add(cmbNota, g);
 
        g.gridx=0; g.gridy=4; g.weightx=0; pForm.add(new JLabel("Faltas"), g);
        txtFaltas = new JTextField("0", 5);
        g.gridx=1; g.weightx=0.2; pForm.add(txtFaltas, g);
 
        p.add(pForm, BorderLayout.NORTH);
 
        // Tabela de notas
        String[] colunas = {"ID", "Disciplina", "Semestre", "Nota", "Faltas"};
        modeloTabelaNotas = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaNotas = new JTable(modeloTabelaNotas);
        tabelaNotas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabelaNotas.getColumnModel().getColumn(0).setMaxWidth(40);
        tabelaNotas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tabelaNotas.getSelectedRow() >= 0) {
                preencherFormNotaDaTabela();
            }
        });
        p.add(new JScrollPane(tabelaNotas), BorderLayout.CENTER);
 
        // Botões com ícones do sistema (parte inferior)
        JPanel pBotoes = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
 
        JButton btnSair    = criarBotaoIconeSistema("Sair",    "sair",    UIManager.getIcon("OptionPane.errorIcon"));
        JButton btnAbrir   = criarBotaoIconeSistema("Abrir",   "abrir",   UIManager.getIcon("FileView.directoryIcon"));
        JButton btnSalvar  = criarBotaoIconeSistema("Salvar",  "salvar",  UIManager.getIcon("FileChooser.floppyDriveIcon"));
        JButton btnAlterar = criarBotaoIconeSistema("Alterar", "alterar", UIManager.getIcon("FileView.computerIcon"));
        JButton btnExcluir = criarBotaoIconeSistema("Excluir", "excluir", UIManager.getIcon("OptionPane.warningIcon"));
 
        btnSair.addActionListener(e    -> System.exit(0));
        btnAbrir.addActionListener(e   -> buscarAlunoParaNota());
        btnSalvar.addActionListener(e  -> salvarNota());
        btnAlterar.addActionListener(e -> prepararAlteracaoNota());
        btnExcluir.addActionListener(e -> excluirNota());
 
        pBotoes.add(btnSair);
        pBotoes.add(btnAbrir);
        pBotoes.add(btnSalvar);
        pBotoes.add(btnAlterar);
        pBotoes.add(btnExcluir);
 
        p.add(pBotoes, BorderLayout.SOUTH);
        return p;
    }
 
 
    // ABA 4: BOLETIM
    private JPanel criarPainelBoletim() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
 
        // Cabeçalho — 4 linhas (inclui Período)
        JPanel pCab = new JPanel(new GridLayout(4, 1, 2, 2));
        pCab.setBorder(new TitledBorder("Aluno"));
        lblBoletimRgm     = new JLabel("RGM: ");
        lblBoletimNome    = new JLabel("Nome: ");
        lblBoletimCurso   = new JLabel("Curso: ");
        lblBoletimPeriodo = new JLabel("Período: ");
        pCab.add(lblBoletimRgm);
        pCab.add(lblBoletimNome);
        pCab.add(lblBoletimCurso);
        pCab.add(lblBoletimPeriodo);
        p.add(pCab, BorderLayout.NORTH);
 
        // Tabela
        String[] colunas = {"Disciplina", "Semestre", "Nota", "Faltas", "Situação"};
        modeloBoletim = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tabelaBoletim = new JTable(modeloBoletim);
        tabelaBoletim.setRowHeight(22);
 
        // Colorir linhas por situação
        tabelaBoletim.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    String sit = (String) t.getModel().getValueAt(row, 4);
                    if ("Aprovado".equals(sit))       c.setBackground(new Color(200, 255, 200));
                    else if ("Reprovado".equals(sit)) c.setBackground(new Color(255, 200, 200));
                    else                              c.setBackground(Color.WHITE);
                }
                return c;
            }
        });
 
        p.add(new JScrollPane(tabelaBoletim), BorderLayout.CENTER);
 
        JLabel lblInfo = new JLabel(
            "Período exibido conforme seleção na aba Curso (não salvo no banco).",
            SwingConstants.CENTER);
        lblInfo.setForeground(Color.GRAY);
        lblInfo.setFont(lblInfo.getFont().deriveFont(Font.ITALIC, 11f));
        p.add(lblInfo, BorderLayout.SOUTH);
 
        return p;
    }
 
 
    // LÓGICA: ALUNO
    private void salvarAluno() {
        try {
            Aluno a = coletarDadosTela();
            alunoDAO.salvar(a);
            alunoAtual = a;
            JOptionPane.showMessageDialog(this,
                    "Aluno salvo com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Erro ao Salvar", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void alterarAluno() {
        if (alunoAtual == null) {
            JOptionPane.showMessageDialog(this,
                    "Consulte um aluno antes de alterar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            Aluno a = coletarDadosTela();
            if (alunoDAO.alterar(a)) {
                alunoAtual = a;
                JOptionPane.showMessageDialog(this,
                        "Aluno alterado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Aluno não encontrado no banco.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(), "Erro ao Alterar", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void consultarAluno() {
        String rgm = JOptionPane.showInputDialog(this,
                "Digite o RGM do aluno:", "Consultar", JOptionPane.QUESTION_MESSAGE);
        if (rgm == null || rgm.trim().isEmpty()) return;
 
        Aluno a = alunoDAO.buscarPorRgm(rgm.trim());
        if (a == null) {
            JOptionPane.showMessageDialog(this,
                    "Aluno não encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        alunoAtual = a;
        preencherTela(a);
        tabbedPane.setSelectedIndex(0);
    }
 
    private void excluirAluno() {
        if (alunoAtual == null) {
            JOptionPane.showMessageDialog(this,
                    "Consulte um aluno antes de excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int op = JOptionPane.showConfirmDialog(this,
                "Excluir aluno " + alunoAtual.getNome() + "?\n(Notas e faltas também serão excluídas)",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (op != JOptionPane.YES_OPTION) return;
 
        if (alunoDAO.excluir(alunoAtual.getRgm())) {
            JOptionPane.showMessageDialog(this,
                    "Aluno excluído com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparTela();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erro ao excluir aluno.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
 
 
    // LÓGICA: NOTAS E FALTAS
    private void buscarAlunoParaNota() {
        String rgm = txtRgmNota.getText().trim();
        if (rgm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite o RGM.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Aluno a = alunoDAO.buscarPorRgm(rgm);
        if (a == null) {
            JOptionPane.showMessageDialog(this, "Aluno não encontrado.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        alunoAtual = a;
        lblNomeNota.setText(a.getNome());
        lblCursoNota.setText(a.getCurso() != null ? a.getCurso().getNome() : "");
        consultarNotas();
    }
 
    private void consultarNotas() {
        if (alunoAtual == null) return;
        modeloTabelaNotas.setRowCount(0);
        List<NotaFalta> lista = notaFaltaDAO.listarPorAluno(alunoAtual.getRgm());
        for (NotaFalta nf : lista) {
            modeloTabelaNotas.addRow(new Object[]{
                nf.getId(), nf.getDisciplina(), nf.getSemestre(), nf.getNota(), nf.getFaltas()
            });
        }
    }
 
    private void salvarNota() {
        if (alunoAtual == null) {
            JOptionPane.showMessageDialog(this,
                    "Busque um aluno antes de salvar notas.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            NotaFalta nf = coletarDadosNota();
            if (idNotaEditando >= 0) {
                nf.setId(idNotaEditando);
                notaFaltaDAO.alterar(nf);
                JOptionPane.showMessageDialog(this, "Nota alterada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                idNotaEditando = -1;
            } else {
                notaFaltaDAO.salvar(nf);
                JOptionPane.showMessageDialog(this, "Nota salva!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            }
            consultarNotas();
            limparFormNota();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
 
    private void prepararAlteracaoNota() {
        int linha = tabelaNotas.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma nota na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        preencherFormNotaDaTabela();
        JOptionPane.showMessageDialog(this,
                "Edite os campos e clique em Salvar.", "Alterar Nota", JOptionPane.INFORMATION_MESSAGE);
    }
 
    private void preencherFormNotaDaTabela() {
        int linha = tabelaNotas.getSelectedRow();
        if (linha < 0) return;
        idNotaEditando = (int) modeloTabelaNotas.getValueAt(linha, 0);
        cmbDisciplina.setSelectedItem(modeloTabelaNotas.getValueAt(linha, 1));
        cmbSemestre.setSelectedItem(modeloTabelaNotas.getValueAt(linha, 2));
        cmbNota.setSelectedItem(modeloTabelaNotas.getValueAt(linha, 3));
        txtFaltas.setText(String.valueOf(modeloTabelaNotas.getValueAt(linha, 4)));
    }
 
    private void excluirNota() {
        int linha = tabelaNotas.getSelectedRow();
        if (linha < 0) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma nota na tabela.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) modeloTabelaNotas.getValueAt(linha, 0);
        int op = JOptionPane.showConfirmDialog(this,
                "Excluir este registro de nota/falta?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (op != JOptionPane.YES_OPTION) return;
 
        if (notaFaltaDAO.excluir(id)) {
            consultarNotas();
            limparFormNota();
            JOptionPane.showMessageDialog(this, "Excluído com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }
 
    private void limparFormNota() {
        cmbDisciplina.setSelectedIndex(0);
        cmbSemestre.setSelectedIndex(0);
        cmbNota.setSelectedItem(5.0);
        txtFaltas.setText("0");
        idNotaEditando = -1;
        tabelaNotas.clearSelection();
    }
 
 
    // LÓGICA: BOLETIM
    private void atualizarBoletim(String rgm) {
        Aluno a = alunoDAO.buscarPorRgm(rgm);
        if (a == null) return;
 
        lblBoletimRgm.setText("RGM: " + a.getRgm());
        lblBoletimNome.setText("Nome: " + a.getNome());
        lblBoletimCurso.setText("Curso: " + (a.getCurso() != null ? a.getCurso().toString() : "-"));
        lblBoletimPeriodo.setText("Período: " + getPeriodoSelecionado());
 
        modeloBoletim.setRowCount(0);
        List<NotaFalta> lista = notaFaltaDAO.listarPorAluno(rgm);
        for (NotaFalta nf : lista) {
            String sit = calcularSituacao(nf.getNota(), nf.getFaltas());
            modeloBoletim.addRow(new Object[]{
                nf.getDisciplina(), nf.getSemestre(), nf.getNota(), nf.getFaltas(), sit
            });
        }
    }
 
    private String getPeriodoSelecionado() {
        if (rbMatutino.isSelected())   return "Matutino";
        if (rbVespertino.isSelected()) return "Vespertino";
        return "Noturno";
    }
 
    private String calcularSituacao(double nota, int faltas) {
        if (faltas > 18)  return "Rep. Falta";
        if (nota >= 5.0)  return "Aprovado";
        return "Reprovado";
    }
 
 
    // MÉTODOS AUXILIARES
 
    /*
      Coleta os dados da tela e valida:
      - RGM não pode ser vazio
      - RGM deve conter apenas números (0-9)
      - Nome não pode ser vazio
    */
    private Aluno coletarDadosTela() throws Exception {
        String rgm = txtRgm.getText().trim();
        if (rgm.isEmpty()) throw new Exception("O RGM não pode ser vazio.");
        if (!rgm.matches("\\d+")) throw new Exception("O RGM deve conter apenas números.");
 
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) throw new Exception("O Nome não pode ser vazio.");
 
        Curso cursoSelecionado = (Curso) cmbCurso.getSelectedItem();
 
        // Pega a data do JSpinner e converte para String dd/MM/yyyy
        Date dataSelecionada = (Date) spnDataNasc.getValue();
        String dataNasc = new SimpleDateFormat("dd/MM/yyyy").format(dataSelecionada);
 
        return new Aluno(
            rgm, nome,
            dataNasc,
            txtCpf.getText().trim(),
            txtEmail.getText().trim(),
            txtEndereco.getText().trim(),
            txtMunicipio.getText().trim(),
            (String) cmbUf.getSelectedItem(),
            txtCelular.getText().trim(),
            cursoSelecionado
        );
    }
 
    private NotaFalta coletarDadosNota() throws Exception {
        if (alunoAtual == null) throw new Exception("Nenhum aluno selecionado.");
        String disc = (String) cmbDisciplina.getSelectedItem();
        if (disc == null || disc.trim().isEmpty()) throw new Exception("Informe a disciplina.");
        int faltas;
        try { faltas = Integer.parseInt(txtFaltas.getText().trim()); }
        catch (NumberFormatException e) { throw new Exception("Faltas deve ser um número inteiro."); }
 
        NotaFalta nf = new NotaFalta();
        nf.setRgmAluno(alunoAtual.getRgm());
        nf.setDisciplina(disc.trim());
        nf.setSemestre((String) cmbSemestre.getSelectedItem());
        nf.setNota((Double) cmbNota.getSelectedItem());
        nf.setFaltas(faltas);
        return nf;
    }
 
    /*
      Preenche a tela com os dados do aluno consultado.
      ATENÇÃO: 'periodo' não vem mais do banco — o RadioButton permanece
      com o valor que o usuário tiver selecionado (padrão: Noturno).
    */
    private void preencherTela(Aluno a) {
        txtRgm.setText(a.getRgm());
        txtNome.setText(a.getNome());
 
        // Preenche o JSpinner com a data vinda do banco (formato dd/MM/yyyy)
        if (a.getDataNascimento() != null && !a.getDataNascimento().trim().isEmpty()) {
            try {
                Date data = new SimpleDateFormat("dd/MM/yyyy").parse(a.getDataNascimento());
                spnDataNasc.setValue(data);
            } catch (ParseException ignored) {
                spnDataNasc.setValue(new Date());
            }
        } else {
            spnDataNasc.setValue(new Date());
        }
 
        txtCpf.setText(a.getCpf());
        txtEmail.setText(a.getEmail());
        txtEndereco.setText(a.getEndereco());
        txtMunicipio.setText(a.getMunicipio());
        if (a.getUf() != null) cmbUf.setSelectedItem(a.getUf());
        txtCelular.setText(a.getCelular());
 
        // Seleciona o curso no ComboBox pelo ID
        if (a.getCurso() != null) {
            for (int i = 0; i < cmbCurso.getItemCount(); i++) {
                if (cmbCurso.getItemAt(i).getId() == a.getCurso().getId()) {
                    cmbCurso.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
 
    private void limparTela() {
        alunoAtual     = null;
        idNotaEditando = -1;
        txtRgm.setText("");
        txtNome.setText("");
        spnDataNasc.setValue(new Date()); // ← reseta o JSpinner para hoje
        txtCpf.setText("   .   .   -  ");
        txtEmail.setText("");
        txtEndereco.setText("");
        txtMunicipio.setText("");
        cmbUf.setSelectedIndex(0);
        txtCelular.setText("(  )     -    ");
        if (cmbCurso.getItemCount() > 0) cmbCurso.setSelectedIndex(0);
        rbNoturno.setSelected(true);
        modeloTabelaNotas.setRowCount(0);
        modeloBoletim.setRowCount(0);
        lblBoletimRgm.setText("RGM: ");
        lblBoletimNome.setText("Nome: ");
        lblBoletimCurso.setText("Curso: ");
        lblBoletimPeriodo.setText("Período: ");
        txtRgmNota.setText("");
        lblNomeNota.setText(" ");
        lblCursoNota.setText(" ");
    }
 
    private void carregarDadosIniciais() {
        List<Curso> cursos = cursoDAO.listarTodos();
        cmbCurso.removeAllItems();
        for (Curso c : cursos) cmbCurso.addItem(c);
 
        List<String> campus = cursoDAO.listarCampus();
        cmbCampus.removeAllItems();
        for (String camp : campus) cmbCampus.addItem(camp);
    }
 
    private JFormattedTextField criarCampoMascarado(String mascara) {
        try {
            MaskFormatter mf = new MaskFormatter(mascara);
            mf.setPlaceholderCharacter(' ');
            return new JFormattedTextField(mf);
        } catch (ParseException e) {
            return new JFormattedTextField();
        }
    }
 
    /*
      Cria um botão quadrado (64×64) com ícone do UIManager e tooltip.
    */
    private JButton criarBotaoIconeSistema(String tooltip, String label, Icon icone) {
        JButton btn = new JButton();
        btn.setToolTipText(tooltip);
        btn.setPreferredSize(new Dimension(64, 64));
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setText("<html><small>" + label + "</small></html>");
        btn.setFont(btn.getFont().deriveFont(9f));
        if (icone != null) {
            btn.setIcon(redimensionarIcone(icone, 32, 32));
        } else {
            btn.setIcon(redimensionarIcone(
                UIManager.getIcon("OptionPane.informationIcon"), 32, 32));
        }
        btn.setFocusPainted(false);
        return btn;
    }
 
    /*
      Redimensiona qualquer Icon (do UIManager) para largura x altura desejados.
    */
    private Icon redimensionarIcone(Icon icone, int largura, int altura) {
        if (icone == null) return null;
        try {
            java.awt.image.BufferedImage img =
                new java.awt.image.BufferedImage(largura, altura,
                    java.awt.image.BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = img.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.scale((double) largura / icone.getIconWidth(),
                     (double) altura  / icone.getIconHeight());
            icone.paintIcon(null, g2, 0, 0);
            g2.dispose();
            return new ImageIcon(img);
        } catch (Exception e) {
            return icone;
        }
    }
 
    private String[] obterUFs() {
        return new String[]{
            "AC","AL","AP","AM","BA","CE","DF","ES","GO",
            "MA","MT","MS","MG","PA","PB","PR","PE","PI",
            "RJ","RN","RS","RO","RR","SC","SP","SE","TO"
        };
    }
 
 
    // MÉTODO MAIN
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SistemaAcademico app = new SistemaAcademico();
            app.setVisible(true);
        });
    }
}
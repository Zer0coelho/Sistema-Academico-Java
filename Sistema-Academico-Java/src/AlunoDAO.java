import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/*
  Classe de acesso a dados (DAO) para a entidade Aluno.
  Responsável pelo CRUD completo no banco de dados.
 */
public class AlunoDAO {

    // Formato de data usado na interface (entrada e exibição)
    private static final SimpleDateFormat SDF_BR  = new SimpleDateFormat("dd/MM/yyyy");
    // Formato de data do banco MySQL (DATE → String auxiliar de leitura)
    private static final SimpleDateFormat SDF_SQL = new SimpleDateFormat("yyyy-MM-dd");

    static {
        // setLenient(false) em ambos: rejeita datas como 32/13/2000
        SDF_BR.setLenient(false);
        SDF_SQL.setLenient(false);
    }


    // Métodos auxiliares de conversão de data
    /*
      Converte String no formato "dd/MM/yyyy" para java.sql.Date.
      @param dataStr data em texto no formato brasileiro
      @return java.sql.Date pronto para uso no PreparedStatement
      @throws Exception se o formato ou os valores forem inválidos
     */
    private java.sql.Date converterParaSqlDate(String dataStr) throws Exception {
        try {
            java.util.Date dataUtil = SDF_BR.parse(dataStr);
            return new java.sql.Date(dataUtil.getTime());
        } catch (ParseException e) {
            throw new Exception("Data inválida: '" + dataStr + "'. Use o formato DD/MM/AAAA.");
        }
    }

    /*
      Converte java.sql.Date lido do banco para String "dd/MM/yyyy".
      @param sqlDate data retornada pelo ResultSet
      @return String formatada no padrão brasileiro
     */
    private String converterParaStringBr(java.sql.Date sqlDate) {
        if (sqlDate == null) return "";
        return SDF_BR.format(sqlDate);
    }


    // CRUD
    /*
      Salva (INSERT) um novo aluno no banco de dados.
      Verifica duplicidade de RGM e de CPF antes de inserir.
      @param aluno objeto Aluno a ser salvo
      @return true se salvo com sucesso
      @throws Exception se o RGM ou CPF já existirem, ou se a data for inválida
     */
    public boolean salvar(Aluno aluno) throws Exception {

        // Verifica RGM duplicado
        if (buscarPorRgm(aluno.getRgm()) != null) {
            throw new Exception("Já existe um aluno cadastrado com o RGM: " + aluno.getRgm());
        }

        // Verifica CPF duplicado
        if (buscarPorCpf(aluno.getCpf()) != null) {
            throw new Exception("Já existe um aluno cadastrado com o CPF: " + aluno.getCpf());
        }

        // Converte data (valida formato e valores antes de chegar ao banco)
        java.sql.Date dataSql = converterParaSqlDate(aluno.getDataNascimento());

        String sql = "INSERT INTO aluno (rgm, nome, data_nascimento, cpf, email, " +
                     "endereco, municipio, uf, celular, id_curso) VALUES (?,?,?,?,?,?,?,?,?,?)";
        Connection con = null;
        try {
            con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, aluno.getRgm());
            ps.setString(2, aluno.getNome());
            ps.setDate(3, dataSql);           // DATE correto
            ps.setString(4, aluno.getCpf());
            ps.setString(5, aluno.getEmail());
            ps.setString(6, aluno.getEndereco());
            ps.setString(7, aluno.getMunicipio());
            ps.setString(8, aluno.getUf());
            ps.setString(9, aluno.getCelular());
            if (aluno.getCurso() != null) {
                ps.setInt(10, aluno.getCurso().getId());
            } else {
                ps.setNull(10, Types.INTEGER);
            }
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            // Captura violação de UNIQUE do banco (segunda linha de defesa para CPF)
            throw new Exception("CPF já cadastrado no banco de dados.");
        } catch (SQLException e) {
            System.err.println("Erro ao salvar aluno: " + e.getMessage());
            return false;
        } finally {
            ConexaoBD.fecharConexao(con);
        }
    }

    /*
      Atualiza (UPDATE) os dados de um aluno existente.
      @param aluno objeto Aluno com os dados atualizados
      @return true se atualizado com sucesso
      @throws Exception se a data de nascimento for inválida
     */
    public boolean alterar(Aluno aluno) throws Exception {

        // Converte data (valida formato e valores antes de chegar ao banco)
        java.sql.Date dataSql = converterParaSqlDate(aluno.getDataNascimento());

        String sql = "UPDATE aluno SET nome=?, data_nascimento=?, cpf=?, email=?, " +
                     "endereco=?, municipio=?, uf=?, celular=?, id_curso=? WHERE rgm=?";
        Connection con = null;
        try {
            con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, aluno.getNome());
            ps.setDate(2, dataSql);           // DATE correto
            ps.setString(3, aluno.getCpf());
            ps.setString(4, aluno.getEmail());
            ps.setString(5, aluno.getEndereco());
            ps.setString(6, aluno.getMunicipio());
            ps.setString(7, aluno.getUf());
            ps.setString(8, aluno.getCelular());
            if (aluno.getCurso() != null) {
                ps.setInt(9, aluno.getCurso().getId());
            } else {
                ps.setNull(9, Types.INTEGER);
            }
            ps.setString(10, aluno.getRgm());
            int linhas = ps.executeUpdate();
            return linhas > 0;
        } catch (SQLIntegrityConstraintViolationException e) {
            throw new Exception("CPF já cadastrado para outro aluno.");
        } catch (SQLException e) {
            System.err.println("Erro ao alterar aluno: " + e.getMessage());
            return false;
        } finally {
            ConexaoBD.fecharConexao(con);
        }
    }

    /*
      Remove (DELETE) um aluno pelo RGM.
      As notas e faltas são excluídas em cascata pelo banco de dados.
      @param rgm identificador único do aluno
      @return true se excluído com sucesso
     */
    public boolean excluir(String rgm) {
        String sql = "DELETE FROM aluno WHERE rgm = ?";
        Connection con = null;
        try {
            con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, rgm);
            int linhas = ps.executeUpdate();
            return linhas > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir aluno: " + e.getMessage());
            return false;
        } finally {
            ConexaoBD.fecharConexao(con);
        }
    }


    // Buscas
    /*
      Busca um aluno pelo RGM (chave primária).
      @param rgm identificador único do aluno
      @return objeto Aluno ou null se não encontrado
     */
    public Aluno buscarPorRgm(String rgm) {
        String sql = "SELECT a.*, c.id as cid, c.nome as cnome, c.campus " +
        			"FROM aluno a LEFT JOIN curso c ON a.id_curso = c.id WHERE a.rgm = ?";
        Connection con = null;
        try {
            con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, rgm);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return montarAluno(rs);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar aluno por RGM: " + e.getMessage());
        } finally {
            ConexaoBD.fecharConexao(con);
        }
        return null;
    }

    /*
      Busca um aluno pelo CPF.
      Usado para verificar duplicidade antes de salvar ou alterar.
      @param cpf CPF no formato 000.000.000-00
      @return objeto Aluno ou null se não encontrado
     */
    public Aluno buscarPorCpf(String cpf) {
        String sql = "SELECT a.*, c.id as cid, c.nome as cnome, c.campus " +
        			"FROM aluno a LEFT JOIN curso c ON a.id_curso = c.id WHERE a.rgm = ?";
        Connection con = null;
        try {
            con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, cpf);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return montarAluno(rs);
        } catch (SQLException e) {
            System.err.println("Erro ao buscar aluno por CPF: " + e.getMessage());
        } finally {
            ConexaoBD.fecharConexao(con);
        }
        return null;
    }

    /*
      Retorna todos os alunos cadastrados no banco.
      @return lista de objetos Aluno ordenada por nome
     */
    public List<Aluno> listarTodos() {
        List<Aluno> lista = new ArrayList<>();
        String sql = "SELECT a.*, c.id as cid, c.nome as cnome, c.campus " +
        			"FROM aluno a LEFT JOIN curso c ON a.id_curso = c.id WHERE a.rgm = ?";
        Connection con = null;
        try {
            con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(montarAluno(rs));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar alunos: " + e.getMessage());
        } finally {
            ConexaoBD.fecharConexao(con);
        }
        return lista;
    }


    // Auxiliar interno
    /*
      Monta um objeto Aluno a partir do ResultSet.
      Converte data_nascimento do tipo DATE do banco para String "dd/MM/yyyy".
      @param rs ResultSet posicionado na linha desejada
      @return objeto Aluno preenchido
      @throws SQLException em caso de erro de leitura
     */
    private Aluno montarAluno(ResultSet rs) throws SQLException {
        Curso curso = null;
        int idCurso = rs.getInt("cid");
        if (!rs.wasNull()) {
        	curso = new Curso(
        		    idCurso,
        		    rs.getString("cnome"),
        		    rs.getString("campus")
        		    // periodo não vem do banco
        	);
        }

        // Lê como java.sql.Date e converte para String brasileira dd/MM/yyyy
        String dataNasc = converterParaStringBr(rs.getDate("data_nascimento"));

        return new Aluno(
            rs.getString("rgm"),
            rs.getString("nome"),
            dataNasc,
            rs.getString("cpf"),
            rs.getString("email"),
            rs.getString("endereco"),
            rs.getString("municipio"),
            rs.getString("uf"),
            rs.getString("celular"),
            curso
        );
    }
}/**/
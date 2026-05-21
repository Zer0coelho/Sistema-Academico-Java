
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
  Classe de acesso a dados (DAO) para a entidade NotaFalta.
  Responsável pelo CRUD de notas e faltas no banco de dados.
 */
public class NotaFaltaDAO {

    /*
      Salva (INSERT) um novo registro de nota/falta no banco.
      @param nf objeto NotaFalta a ser salvo
      @return true se salvo com sucesso
     */
    public boolean salvar(NotaFalta nf) {
        String sql = "INSERT INTO nota_falta (rgm_aluno, disciplina, semestre, nota, faltas) VALUES (?,?,?,?,?)";
        Connection con = null;
        try {
            con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nf.getRgmAluno());
            ps.setString(2, nf.getDisciplina());
            ps.setString(3, nf.getSemestre());
            ps.setDouble(4, nf.getNota());
            ps.setInt(5, nf.getFaltas());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar nota/falta: " + e.getMessage());
            return false;
        } finally {
            ConexaoBD.fecharConexao(con);
        }
    }

    /*
      Atualiza (UPDATE) um registro de nota/falta existente.
      @param nf objeto NotaFalta com dados atualizados
      @return true se atualizado com sucesso
     */
    public boolean alterar(NotaFalta nf) {
        String sql = "UPDATE nota_falta SET disciplina=?, semestre=?, nota=?, faltas=? WHERE id=?";
        Connection con = null;
        try {
            con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, nf.getDisciplina());
            ps.setString(2, nf.getSemestre());
            ps.setDouble(3, nf.getNota());
            ps.setInt(4, nf.getFaltas());
            ps.setInt(5, nf.getId());
            int linhas = ps.executeUpdate();
            return linhas > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao alterar nota/falta: " + e.getMessage());
            return false;
        } finally {
            ConexaoBD.fecharConexao(con);
        }
    }

    /*
      Remove (DELETE) um registro de nota/falta pelo ID
      @param id identificador do registro
      @return true se excluído com sucesso
     */
    public boolean excluir(int id) {
        String sql = "DELETE FROM nota_falta WHERE id = ?";
        Connection con = null;
        try {
            con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            int linhas = ps.executeUpdate();
            return linhas > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao excluir nota/falta: " + e.getMessage());
            return false;
        } finally {
            ConexaoBD.fecharConexao(con);
        }
    }

    /*
      Lista todas as notas e faltas de um determinado alun
      @param rgmAluno RGM do aluno
      @return lista de objetos NotaFalta
     */
    public List<NotaFalta> listarPorAluno(String rgmAluno) {
        List<NotaFalta> lista = new ArrayList<>();
        String sql = "SELECT * FROM nota_falta WHERE rgm_aluno = ? ORDER BY semestre, disciplina";
        Connection con = null;
        try {
            con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, rgmAluno);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new NotaFalta(
                    rs.getInt("id"),
                    rs.getString("rgm_aluno"),
                    rs.getString("disciplina"),
                    rs.getString("semestre"),
                    rs.getDouble("nota"),
                    rs.getInt("faltas")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar notas: " + e.getMessage());
        } finally {
            ConexaoBD.fecharConexao(con);
        }
        return lista;
    }

    /*
      Busca um registro de nota/falta pelo ID.
     @param id identificador do registro
     @return objeto NotaFalta ou null se não encontrado
     */
    public NotaFalta buscarPorId(int id) {
        String sql = "SELECT * FROM nota_falta WHERE id = ?";
        Connection con = null;
        try {
            con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new NotaFalta(
                    rs.getInt("id"),
                    rs.getString("rgm_aluno"),
                    rs.getString("disciplina"),
                    rs.getString("semestre"),
                    rs.getDouble("nota"),
                    rs.getInt("faltas")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar nota/falta: " + e.getMessage());
        } finally {
            ConexaoBD.fecharConexao(con);
        }
        return null;
    }
}

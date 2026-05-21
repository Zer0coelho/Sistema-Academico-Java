import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/*
  Classe de acesso a dados (DAO) para a entidade Curso.
  'periodo' foi removido do banco; é gerenciado apenas na interface.
 */
public class CursoDAO {

    /*
      Retorna todos os cursos cadastrados no banco.
      @return lista de objetos Curso
     */
    public List<Curso> listarTodos() {
        List<Curso> lista = new ArrayList<>();
        String sql = "SELECT * FROM curso ORDER BY nome";
        Connection con = null;
        try {
            con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(new Curso(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("campus")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar cursos: " + e.getMessage());
        } finally {
            ConexaoBD.fecharConexao(con);
        }
        return lista;
    }

    /*
      Busca um curso pelo seu ID.
      @param id identificador do curso
      @return objeto Curso ou null se não encontrado
     */
    public Curso buscarPorId(int id) {
        String sql = "SELECT * FROM curso WHERE id = ?";
        Connection con = null;
        try {
            con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Curso(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("campus")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar curso: " + e.getMessage());
        } finally {
            ConexaoBD.fecharConexao(con);
        }
        return null;
    }

    /*
      Retorna lista de campus distintos cadastrados.
      @return lista de strings com os campus
     */
    public List<String> listarCampus() {
        List<String> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT campus FROM curso ORDER BY campus";
        Connection con = null;
        try {
            con = ConexaoBD.getConexao();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(rs.getString("campus"));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar campus: " + e.getMessage());
        } finally {
            ConexaoBD.fecharConexao(con);
        }
        return lista;
    }
}
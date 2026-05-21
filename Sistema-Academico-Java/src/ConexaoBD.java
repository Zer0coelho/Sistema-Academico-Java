
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
  Classe utilitária responsável por gerenciar a conexão com o banco de dados MySQL.
  Utiliza o padrão Singleton para garantir uma única instância de conexão.
 */
public class ConexaoBD {

    // URL de conexão com o banco de dados
    private static final String URL      = "jdbc:mysql://localhost:3306/sistema_academico";
    private static final String USUARIO  = "root";   // Altere para seu usuário MySQL
    private static final String SENHA    = "admin_Coelho@";   // Altere para sua senha MySQL
    //private static final String SENHA    = "Coelho_180506";   // Altere para sua senha MySQL

    /*
      Abre e retorna uma nova conexão com o banco de dados.
      @return Connection objeto de conexão ativa
      @throws SQLException se não for possível conectar
     */
    public static Connection getConexao() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL não encontrado. Adicione o mysql-connector-java ao build path.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    /*
      Fecha a conexão com o banco de dados com segurança.
      @param con conexão a ser fechada
     */
    public static void fecharConexao(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}

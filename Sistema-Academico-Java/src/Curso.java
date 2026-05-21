
/*
  Classe modelo que representa um Curso no sistema acadêmico.
 */
public class Curso {

    private int    id;
    private String nome;
    private String campus;
    // private String periodo; // "Matutino", "Vespertino" ou "Noturno"

    // Construtores 

    public Curso() {}

    public Curso(int id, String nome, String campus) {
        this.id     = id;
        this.nome   = nome;
        this.campus = campus;
    }

    // Getters e Setters 

    public int getId()           { return id; }
    public void setId(int id)    { this.id = id; }

    public String getNome()              { return nome; }
    public void setNome(String nome)     { this.nome = nome; }

    public String getCampus()                { return campus; }
    public void setCampus(String campus)     { this.campus = campus; }

    /**
     * Representação textual usada nos JComboBox.
     * Período não é exibido aqui pois não vem do banco;
     * a interface acrescenta o período selecionado pelo usuário.
     */
    @Override
    public String toString() {
        return nome + " - " + campus;
    }
}


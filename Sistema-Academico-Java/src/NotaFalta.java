

/*
  Classe modelo que representa o registro de Notas e Faltas de um aluno em uma disciplina.
 */
public class NotaFalta {

    private int    id;
    private String rgmAluno;
    private String disciplina;
    private String semestre;   // ex.: "2026-1"
    private double nota;
    private int    faltas;

    // Construtores 

    public NotaFalta() {}

    public NotaFalta(int id, String rgmAluno, String disciplina,
                     String semestre, double nota, int faltas) {
        this.id         = id;
        this.rgmAluno   = rgmAluno;
        this.disciplina = disciplina;
        this.semestre   = semestre;
        this.nota       = nota;
        this.faltas     = faltas;
    }

    // Getters e Setters 

    public int getId()           { return id; }
    public void setId(int id)    { this.id = id; }

    public String getRgmAluno()              { return rgmAluno; }
    public void setRgmAluno(String rgmAluno) { this.rgmAluno = rgmAluno; }

    public String getDisciplina()                    { return disciplina; }
    public void setDisciplina(String disciplina)     { this.disciplina = disciplina; }

    public String getSemestre()              { return semestre; }
    public void setSemestre(String semestre) { this.semestre = semestre; }

    public double getNota()          { return nota; }
    public void setNota(double nota) { this.nota = nota; }

    public int getFaltas()           { return faltas; }
    public void setFaltas(int faltas){ this.faltas = faltas; }

    @Override
    public String toString() {
        return disciplina + " [" + semestre + "] Nota: " + nota + " Faltas: " + faltas;
    }
}

package Area;

import Enum.*;

import java.io.Serializable;
import java.util.Scanner;

public class AreaOperacao implements Serializable {
    private static final long serialVersionUID = 6686230772481661001L;
    private String id;
    private boolean idBloqueado;
    private String nome;
    private String localizacao;
    private double altitude;
    private Horario horario;
    private TipoTerreno tipoTerreno;
    private NivelRisco nivelRisco;
    private String descricao;

    public AreaOperacao() {
        this.id = "";
        this.nome = "";
        this.localizacao = "";
        this.altitude = 0;
        this.horario = null;
        this.tipoTerreno = null;
        this.nivelRisco = null;
        this.descricao = "";
    }

    public AreaOperacao(String id, String nome, String localizacao, Horario horario,TipoTerreno tipoTerreno, NivelRisco nivelRisco, String descricao) {
        this(id, nome, localizacao, horario, tipoTerreno, nivelRisco, descricao, 0);
    }

    public AreaOperacao(String id, String nome, String localizacao, Horario horario, TipoTerreno tipoTerreno, NivelRisco nivelRisco, String descricao, double altitude) {
       this.id = id;
       this.nome = nome;
       this.localizacao = localizacao;
       this.altitude = altitude;
       this.horario = horario;
       this.tipoTerreno = tipoTerreno;
       this.nivelRisco = nivelRisco;
       this.descricao = descricao;
    }

    public void cadastroArea(Scanner in){
        if (idBloqueado) throw new IllegalArgumentException("A area ja esta cadastrada.");
        System.out.println("Digite o id da area");
        String novoId = in.nextLine().trim();
        if (novoId.isBlank()) {
            throw new IllegalArgumentException("A area precisa de um ID.");
        }
        System.out.println("Digite o nome da area");
        String novoNome = in.nextLine();
        System.out.println("Digite o localizacao da area");
        String novaLocalizacao = in.nextLine();
        System.out.println("Digite o horario da area: DIA ou NOITE");
        Horario novoHorario = Horario.valueOf(in.nextLine().trim());
        System.out.println("Digite o tipo de terreno da area");
        TipoTerreno novoTerreno = TipoTerreno.valueOf(in.nextLine().trim());
        System.out.println("Digite o nivel de risco da area");
        NivelRisco novoRisco = NivelRisco.valueOf(in.nextLine().trim());
        System.out.println("Digite a altitude da area em metros");
        double novaAltitude = Double.parseDouble(in.nextLine().trim().replace(',', '.'));
        if (!Double.isFinite(novaAltitude) || novaAltitude < 0) {
            throw new IllegalArgumentException("A altitude precisa ser finita e nao negativa.");
        }
        System.out.println("Digite a descricao da area");
        String novaDescricao = in.nextLine();
        this.id = novoId;
        this.nome = novoNome;
        this.localizacao = novaLocalizacao;
        this.horario = novoHorario;
        this.tipoTerreno = novoTerreno;
        this.nivelRisco = novoRisco;
        this.altitude = novaAltitude;
        this.descricao = novaDescricao;
    }

    public void atualizarNivelRisco(NivelRisco novoRisco) {
        if (novoRisco == null) {
            throw new IllegalArgumentException("Informe o nivel de risco.");
        }
        this.nivelRisco = novoRisco;
    }

    public void exibirInformacoes(){
        try {
            System.out.println("ID: " + id);
            System.out.println("Nome: " + nome);
            System.out.println("Localizacao: " + localizacao);
            System.out.println("Altitude: " + altitude + " m");
            System.out.println("Horario: " + horario);
            System.out.println("TipoTerreno: " + tipoTerreno);
            System.out.println("NivelRisco: " + nivelRisco);
            System.out.println("Descricao: " + descricao);
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }
    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        if (!Double.isFinite(altitude) || altitude < 0) throw new IllegalArgumentException("Altitude invalida.");
        this.altitude = altitude;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        if (horario == null) throw new IllegalArgumentException("Informe horario.");
        this.horario = horario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id == null || id.isBlank() || (idBloqueado && !java.util.Objects.equals(this.id, id))) {
            throw new IllegalArgumentException("ID de area invalido ou ja cadastrado.");
        }
        this.id = id;
    }

    public void bloquearId() {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Informe o ID da area.");
        idBloqueado = true;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public TipoTerreno getTipoTerreno() {
        return tipoTerreno;
    }

    public void setTipoTerreno(TipoTerreno tipoTerreno) {
        if (tipoTerreno == null) throw new IllegalArgumentException("Informe tipoTerreno.");
        this.tipoTerreno = tipoTerreno;
    }

    public NivelRisco getNivelRisco() {
        return nivelRisco;
    }

    public void setNivelRisco(NivelRisco nivelRisco) {
        if (nivelRisco == null) throw new IllegalArgumentException("Informe nivelRisco.");
        this.nivelRisco = nivelRisco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}

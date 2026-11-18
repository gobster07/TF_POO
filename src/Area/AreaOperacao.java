package Area;

import Enum.*;

import java.io.Serializable;
import java.util.Scanner;

public class AreaOperacao implements Serializable {
    private String id;
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
        try {
            System.out.println("Digite o id da area");
            this.id = in.nextLine();
            System.out.println("Digite o nome da area");
            this.nome = in.nextLine();
            System.out.println("Digite o localizacao da area");
            this.localizacao = in.nextLine();
            System.out.println("Digite o horario da area: DIA ou NOITE");
            this.horario = Horario.valueOf(in.nextLine().trim());
            System.out.println("Digite o tipo de terreno da area");
            this.tipoTerreno = TipoTerreno.valueOf(in.nextLine().trim());
            System.out.println("Digite o nivel de risco da area");
            this.nivelRisco = NivelRisco.valueOf(in.nextLine().trim());
            System.out.println("Digite a altitude da area em metros");
            this.altitude = Double.parseDouble(in.nextLine().trim().replace(',', '.'));
        } catch (Exception e) {
            System.out.println("Erro ao cadastrar area");
        }

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
        this.altitude = altitude;
    }

    public Horario getHorario() {
        return horario;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
        this.tipoTerreno = tipoTerreno;
    }

    public NivelRisco getNivelRisco() {
        return nivelRisco;
    }

    public void setNivelRisco(NivelRisco nivelRisco) {
        this.nivelRisco = nivelRisco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}

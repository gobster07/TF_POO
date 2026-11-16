package Area;

import Enum.*;

import java.time.LocalTime;
import java.time.Clock;

import java.io.Serializable;

public class AreaOperacao implements Serializable {
    private String id;
    private String nome;
    private String localizacao;
    private LocalTime horario;
    private TipoTerreno tipoTerreno;
    private NivelRisco nivelRisco;
    private String descricao;

    public AreaOperacao() {
        this.id = "";
        this.nome = "";
        this.localizacao = "";
        this.horario = null;
        this.tipoTerreno = null;
        this.nivelRisco = null;
        this.descricao = "";
    }

    public AreaOperacao(String id, String nome, String localizacao, LocalTime horario,TipoTerreno tipoTerreno, NivelRisco nivelRisco, String descricao) {
       this.id = id;
       this.nome = nome;
       this.localizacao = localizacao;
       this.horario = horario;
       this.tipoTerreno = tipoTerreno;
       this.nivelRisco = nivelRisco;
       this.descricao = descricao;
    }

    public void exibirInformacoes(){
        try {
            System.out.println("ID: " + id);
            System.out.println("Nome: " + nome);
            System.out.println("Localizacao: " + localizacao);
            System.out.println("Horario: " + horario);
            System.out.println("TipoTerreno: " + tipoTerreno);
            System.out.println("NivelRisco: " + nivelRisco);
            System.out.println("Descricao: " + descricao);
        } catch (Exception e) {
            System.out.println("ERRO: " + e.getMessage());
        }
    }
    public LocalTime getHorario() {
        return horario;
    }

    public void setHorario(LocalTime horario) {
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

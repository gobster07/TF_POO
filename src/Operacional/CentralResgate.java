package Operacional;

import Area.AreaOperacao;
import Robos.Robo;

import java.io.Serializable;
import java.util.List;
import java.util.Queue;

public class CentralResgate implements Serializable {
    private String nome;
    private List<Robo> robos;
    private List<AreaOperacao> areas;
    private List<Missao> missoes;
    private Queue<Missao> missoesPendentes;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Robo> getRobos() {
        return robos;
    }

    public void setRobos(List<Robo> robos) {
        this.robos = robos;
    }

    public List<AreaOperacao> getAreas() {
        return areas;
    }

    public void setAreas(List<AreaOperacao> areas) {
        this.areas = areas;
    }

    public List<Missao> getMissoes() {
        return missoes;
    }

    public void setMissoes(List<Missao> missoes) {
        this.missoes = missoes;
    }

    public Queue<Missao> getMissoesPendentes() {
        return missoesPendentes;
    }

    public void setMissoesPendentes(Queue<Missao> missoesPendentes) {
        this.missoesPendentes = missoesPendentes;
    }
}

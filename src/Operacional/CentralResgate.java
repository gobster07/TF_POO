package Operacional;

import Area.AreaOperacao;
import Robos.Robo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import Enum.StatusMissao;

public class CentralResgate implements Serializable {
    private String nome;
    private List<Robo> robos;
    private List<AreaOperacao> areas;
    private List<Missao> missoes;
    private Queue<Missao> missoesPendentes;

    public CentralResgate() {
        this.robos = new ArrayList<>();
        this.areas = new ArrayList<>();
        this.missoes = new ArrayList<>();
        this.missoesPendentes = new LinkedList<>();
    }

    public void registrarMissao(Missao missao) {
        if (missao == null) {
            throw new IllegalArgumentException("A missão não pode ser nula.");
        }
        if (missao.getId() == null || missao.getId().isBlank()) {
            throw new IllegalArgumentException("A missão precisa de um identificador.");
        }
        if (missao.getArea() == null) {
            throw new IllegalArgumentException("A missão precisa estar associada a uma área.");
        }
        if (missoes.stream().anyMatch(m -> missao.getId().equals(m.getId()))) {
            throw new IllegalArgumentException("Já existe uma missão com o ID " + missao.getId() + ".");
        }

        missao.setStatus(StatusMissao.PENDENTE);
        missoes.add(missao);
        missoesPendentes.add(missao);
    }

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

package Robos;

import Enum.StatusRobo;
import Enum.Roda;
import Enum.Horario;
import Enum.StatusMissao;
import Enum.TipoMissao;
import Enum.TipoTerreno;
import Operacional.Missao;
import interfaces.Comunicavel;

import java.io.Serializable;
import java.util.Scanner;

public class RoboTerrestre extends Robo implements Comunicavel, Serializable {
    private static final long serialVersionUID = -6610246368448461173L;
    private boolean bouyancy;
    private double capacidadeCarga;
    private Roda roda;


    public RoboTerrestre() {
        super();
        this.bouyancy = false;
        roda = null;
        capacidadeCarga = 0;
    }

    public RoboTerrestre(String id, String nome, int bateria, StatusRobo status,Roda roda, double capacidadeCarga, boolean bouyancy) {
        super(id, nome, bateria, status);
        this.bouyancy = bouyancy;
        this.roda = roda;
        this.capacidadeCarga = capacidadeCarga;
    }

    @Override
    public void cadastraRobo(Scanner in){
        super.cadastraRobo(in);
        System.out.println("O Robo possui equipamento de flutuamneto?");
        System.out.println("1 - Sim");
        System.out.println("0 - Nao");
        int opcaoFlutuacao = Integer.parseInt(in.nextLine().trim());
        if (opcaoFlutuacao != 0 && opcaoFlutuacao != 1) {
            throw new IllegalArgumentException("Digite 1 para Sim ou 0 para Nao.");
        }
        this.bouyancy = opcaoFlutuacao == 1;
        System.out.println("Qual a capacidade de carga do robo em KG?");
        this.capacidadeCarga = Double.parseDouble(in.nextLine().trim().replace(',', '.'));
        System.out.println("Digite o tipo da roda: NORMAL ou OFFROAD");
        this.roda = Roda.valueOf(in.nextLine().trim());
    }

    @Override
    public void carregar() {
        super.setBateria(100);
        if (super.getStatus() == StatusRobo.SEM_BATERIA) {
            super.setStatus(StatusRobo.DISPONIVEL);
        }
    }


    @Override
    public void exibirRobo() {
        super.exibirRobo();
        System.out.println("Tipo: Robo terrestre");
        System.out.println("Capacidade de carga: " + capacidadeCarga + " kg");
        System.out.println("Roda: " + roda);
        System.out.println("Equipamento de flutuacao: " + bouyancy);
    }

    public double getCapacidadeCarga() {
        return capacidadeCarga;
    }

    public void setCapacidadeCarga(float capacidadeCarga) {
        this.capacidadeCarga = capacidadeCarga;
    }

    @Override
    public void executaMissao(Missao missao) {
        if (missao == null || missao.getArea() == null || missao.getArea().getHorario() == null) {
            throw new IllegalArgumentException("A missao precisa de uma area com horario definido.");
        }
        if (missao.getArea().getHorario() == Horario.NOITE) {
            throw new IllegalArgumentException("Missoes noturnas permitem apenas drones com camera termal.");
        }
        if (missao.getStatus() != StatusMissao.PENDENTE) {
            throw new IllegalArgumentException("A missao precisa estar pendente.");
        }
        if (getStatus() != StatusRobo.DISPONIVEL) {
            throw new IllegalArgumentException("O robo terrestre nao esta disponivel.");
        }
        if (getBateria() <= 0) {
            throw new IllegalArgumentException("O robo precisa de bateria para iniciar a missao.");
        }
        if (missao.getRoboResponsavel() != null && missao.getRoboResponsavel() != this) {
            throw new IllegalArgumentException("A missao ja esta atribuida a outro robo.");
        }
        if (missao.getTipo() == null) {
            throw new IllegalArgumentException("A missao precisa de um tipo definido.");
        }
        if (missao.getArea().getTipoTerreno() == null) {
            throw new IllegalArgumentException("A area precisa de um tipo de terreno definido.");
        }
        if (roda == null) {
            throw new IllegalArgumentException("O robo precisa de um tipo de roda definido.");
        }
        if (missao.getArea().getTipoTerreno() == TipoTerreno.ALAGADO && !bouyancy) {
            throw new IllegalArgumentException("Areas alagadas exigem um robo com flutuacao.");
        }
        if (missao.getArea().getTipoTerreno() == TipoTerreno.IRREGULAR && roda != Roda.OFFROAD) {
            throw new IllegalArgumentException("Terrenos irregulares exigem rodas OFFROAD.");
        }
        if (missao.getTipo() == TipoMissao.TRANSPORTE_DE_SUPRIMENTOS
                && (!Double.isFinite(capacidadeCarga) || capacidadeCarga <= 0)) {
            throw new IllegalArgumentException("O transporte exige capacidade de carga valida e maior que zero.");
        }
    }

    @Override
    public void enviarMensagem(String mensagem) {
        super.enviarMensagem(mensagem);
    }

    @Override
    public void receberMensagem(String mensagem) {
        super.receberMensagem(mensagem);
    }
}

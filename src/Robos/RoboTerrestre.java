package Robos;

import Enum.StatusRobo;
import Enum.Roda;
import interfaces.Comunicavel;

import java.io.Serializable;
import java.util.Scanner;

public class RoboTerrestre extends Robo implements Comunicavel, Serializable {
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
        this.bouyancy = in.nextInt() == 1;
        System.out.println("Qual a capacidade de carga do robo em KG?");
        this.capacidadeCarga = in.nextDouble();
        System.out.println("Digite o tipo da roda: Normal ou OffRoad");
        this.roda = Roda.valueOf(in.next());
        in.nextLine();
    }

    @Override
    public void carregar() {
        super.setBateria(100);
        if (super.getStatus() == StatusRobo.SEM_BATERIA) {
            super.setStatus(StatusRobo.DISPONIVEL);
        }
    }


    public double getCapacidadeCarga() {
        return capacidadeCarga;
    }

    public void setCapacidadeCarga(float capacidadeCarga) {
        this.capacidadeCarga = capacidadeCarga;
    }

    @Override
    public void executaMissao() {
    }

    @Override
    public void enviarMensagem(String mensagem) {
    }

    @Override
    public void receberMensagem(String mensagem) {
    }
}

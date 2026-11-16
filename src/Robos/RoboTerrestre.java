package Robos;

import Enum.StatusRobo;
import interfaces.Comunicavel;

import java.io.Serializable;

public class RoboTerrestre extends Robo implements Comunicavel, Serializable {
    private boolean bouyancy;

    private float capacidadeCarga;

    public RoboTerrestre() {
        super();
        this.bouyancy = false;
        capacidadeCarga = 0;
    }

    public RoboTerrestre(String id, String nome, int bateria, StatusRobo status, float capacidadeCarga, boolean bouyancy) {
        super(id, nome, bateria, status);
        this.bouyancy = bouyancy;
    }

    public void carregar() {
        super.setBateria(100);
    }


    public float getCapacidadeCarga() {
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

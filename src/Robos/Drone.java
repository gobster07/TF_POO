package Robos;

import Enum.*;

import interfaces.*;

import java.io.Serializable;
import java.util.Scanner;

public class Drone extends Robo implements Comunicavel, Serializable {
    private double altitudeMaxima;
    private boolean thermal;
    private double consumo = 2.5;

    public Drone(String id, String nome, int bateria, StatusRobo status, double altitudeMaxima, boolean thermal) {
        super(id, nome, bateria, status);
        this.altitudeMaxima = altitudeMaxima;
        this.thermal = thermal;
    }

    public Drone(){
        super();
        altitudeMaxima = 0;
        thermal = false;
    }

    @Override
    public void carregar() {
        super.setBateria(100);
        if (super.getStatus() == StatusRobo.SEM_BATERIA){
            super.setStatus(StatusRobo.DISPONIVEL);
        }
    }

    public double getAltitudeMaxima() {
        return altitudeMaxima;
    }

    public void setAltitudeMaxima(float altitudeMaxima) {
        this.altitudeMaxima = altitudeMaxima;
    }

    @Override
    public void cadastraRobo(Scanner in){
        super.cadastraRobo(in);
        System.out.println("Digite a altitude maxima: ");
        this.altitudeMaxima = in.nextFloat();
        System.out.println("O Drone possui camera termal?");
        System.out.println("1 - Sim");
        System.out.println("0 - Nao");
        int opcaoThermal = in.nextInt();
        if (opcaoThermal != 0 && opcaoThermal != 1) {
            throw new IllegalArgumentException("Digite 1 para Sim ou 0 para Nao.");
        }
        this.thermal = opcaoThermal == 1;
        in.nextLine();
    }

    @Override
    public void executaMissao() {
        System.out.println("Qual o id da missão?");
    }

    @Override
    public void enviarMensagem(String mensagem) {
    }

    @Override
    public void receberMensagem(String mensagem) {
    }
}

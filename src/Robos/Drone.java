package Robos;

import Enum.*;
import Operacional.Missao;

import interfaces.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Drone extends Robo implements Comunicavel, Serializable {
    private double altitudeMaxima;
    private boolean thermal;
    private double consumoCameraPorHora;

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
        if (thermal) {
            System.out.println("Consumo da camera por hora:");
            consumoCameraPorHora = in.nextDouble();
        }
        in.nextLine();
    }

    @Override
    public void executaMissao(Missao missao) {
        if (missao == null || missao.getArea() == null || missao.getArea().getHorario() == null) {
            throw new IllegalArgumentException("A missao precisa de uma area com horario definido.");
        }
        if (missao.getArea().getHorario() == Horario.NOITE && !thermal) {
            throw new IllegalArgumentException("Missoes noturnas exigem um drone com camera termal.");
        }
        if (missao.getStatus() != StatusMissao.PENDENTE) {
            throw new IllegalArgumentException("A missao precisa estar pendente.");
        }
        if (getStatus() != StatusRobo.DISPONIVEL) {
            throw new IllegalArgumentException("O drone nao esta disponivel.");
        }
        if (getBateria() <= 0) {
            throw new IllegalArgumentException("O drone precisa de bateria para iniciar a missao.");
        }
        if (missao.getRoboResponsavel() != null && missao.getRoboResponsavel() != this) {
            throw new IllegalArgumentException("A missao ja esta atribuida a outro robo.");
        }
        double altitude = missao.getArea().getAltitude();
        if (!Double.isFinite(altitude) || altitude < 0
                || !Double.isFinite(altitudeMaxima) || altitudeMaxima < 0) {
            throw new IllegalArgumentException("As altitudes devem ser numeros finitos e nao negativos.");
        }
        if (altitude > altitudeMaxima + 50) {
            throw new IllegalArgumentException("Missão em terreno alto demais para o drone.");
        }
        if (missao.getTipo() == null) {
            throw new IllegalArgumentException("A missao precisa de um tipo definido.");
        }
        if (missao.getTipo() == TipoMissao.TRANSPORTE_DE_SUPRIMENTOS){
            throw new IllegalArgumentException("Drones não podem carregar suprimentos");
        }
    }
    @Override
    public double calculoBateria(Missao missao, double horas) {
        double altura = missao.getArea().getAltitude();
        int multiplicador = 1;
        int faixas = (int) (altura / 200);
        for (int i = 0; i < faixas; i++) {
            multiplicador = multiplicador * 2;
        }
        double consumoPorHora = getConsumoPorHora();
        if (thermal) {
            consumoPorHora = consumoPorHora + consumoCameraPorHora;
        }
        return missao.getConsumoFixoBateria()
                + consumoPorHora * horas * multiplicador;
    }
    @Override
    public void enviarMensagem(String mensagem) {
    }

    @Override
    public void receberMensagem(String mensagem) {
    }
}

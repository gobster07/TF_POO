package Robos;

import Enum.*;
import Operacional.Missao;

import interfaces.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Drone extends Robo implements Comunicavel, Serializable {
    private static final long serialVersionUID = 84035629436560638L;
    private static final int MAX_FAIXAS_ALTITUDE = 30;
    private double altitudeMaxima;
    private boolean thermal;
    private double consumoCameraPorHora;

    public Drone(String id, String nome, int bateria, StatusRobo status, double altitudeMaxima, boolean thermal) {
        super(id, nome, bateria, status);
        validarNaoNegativoFinito(altitudeMaxima, "Altitude maxima");
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

    @Override
    public void exibirRobo() {
        super.exibirRobo();
        System.out.println("Tipo: Drone");
        System.out.println("Altitude maxima: " + altitudeMaxima + " m");
        System.out.println("Camera termal: " + thermal);
        System.out.println("Consumo da camera por hora: " + consumoCameraPorHora);
    }

    public double getAltitudeMaxima() {
        return altitudeMaxima;
    }

    public void setAltitudeMaxima(float altitudeMaxima) {
        validarNaoNegativoFinito(altitudeMaxima, "Altitude maxima");
        this.altitudeMaxima = altitudeMaxima;
    }

    @Override
    public void cadastraRobo(Scanner in){
        super.cadastraRobo(in);
        System.out.println("Digite a altitude maxima: ");
        double altitude = Double.parseDouble(in.nextLine().trim().replace(',', '.'));
        validarNaoNegativoFinito(altitude, "Altitude maxima");
        this.altitudeMaxima = altitude;
        System.out.println("O Drone possui camera termal?");
        System.out.println("1 - Sim");
        System.out.println("0 - Nao");
        int opcaoThermal = Integer.parseInt(in.nextLine().trim());
        if (opcaoThermal != 0 && opcaoThermal != 1) {
            throw new IllegalArgumentException("Digite 1 para Sim ou 0 para Nao.");
        }
        this.thermal = opcaoThermal == 1;
        if (thermal) {
            System.out.println("Consumo da camera por hora:");
            setConsumoCameraPorHora(Double.parseDouble(in.nextLine().trim().replace(',', '.')));
        }
    }

    @Override
    public void executarMissao(Missao missao) {
        if (precisaManutencao()) {
            throw new IllegalArgumentException("O drone precisa de manutencao preventiva.");
        }
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
        super.calculoBateria(missao, horas);
        if (missao.getArea() == null) {
            throw new IllegalArgumentException("A missao precisa de uma area.");
        }
        double altura = missao.getArea().getAltitude();
        validarNaoNegativoFinito(altura, "Altitude");
        validarNaoNegativoFinito(consumoCameraPorHora, "Consumo da camera por hora");
        int multiplicador = 1;
        // 2^30 e a ultima potencia de dois positiva que cabe em int.
        int faixas = Math.min(MAX_FAIXAS_ALTITUDE, (int) (altura / 200));
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
    protected int calcularDesgaste(Missao missao) {
        return super.calcularDesgaste(missao) + (thermal ? 5 : 0)
                + (missao.getArea().getAltitude() >= 200 ? 5 : 0);
    }

    public double getConsumoCameraPorHora() {
        return consumoCameraPorHora;
    }

    public void setConsumoCameraPorHora(double consumoCameraPorHora) {
        validarNaoNegativoFinito(consumoCameraPorHora, "Consumo da camera por hora");
        this.consumoCameraPorHora = consumoCameraPorHora;
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

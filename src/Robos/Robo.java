package Robos;

import Enum.*;
import interfaces.*;
import Operacional.Missao;
import Mensagens.RegistroMensagens;
import java.io.IOException;

import java.io.Serializable;
import java.util.Scanner;
public abstract class Robo implements Comunicavel, Serializable {
    private static final long serialVersionUID = -1720325145472735421L;
    private String id;
    private String nome;
    private int bateria;
    private StatusRobo status;
    private int missoesRealizadas;
    private double consumoPorHora;

    public Robo(String id, String nome, int bateria, StatusRobo status) {
        this.id = id;
        this.nome = nome;
        setBateria(bateria);
        this.status = status;
        this.missoesRealizadas = 0;
        this.consumoPorHora = 0;
    }

    public Robo(){
        this.id = null;
        this.nome = null;
        this.bateria = 100;
        this.status = StatusRobo.DISPONIVEL;
        this.missoesRealizadas = 0;
        this.consumoPorHora = 0;
    }

    public abstract void carregar();

    public abstract void executaMissao(Missao missao);

    public void exibirRobo() {
        System.out.println("ID: " + id);
        System.out.println("Nome: " + nome);
        System.out.println("Bateria: " + bateria + "%");
        System.out.println("Status: " + status);
        System.out.println("Missoes realizadas: " + missoesRealizadas);
        System.out.println("Consumo por hora: " + consumoPorHora);
    }

    public void cadastraRobo(Scanner in){
        System.out.println("Digite o id do robo");
        this.id = in.nextLine();
        System.out.println("Digite o nome do robo: ");
        this.nome = in.nextLine();
        System.out.println("Consumo de bateria por hora:");
        setConsumoPorHora(Double.parseDouble(in.nextLine().trim().replace(',', '.')));
    }
    public void descontarBateria(double consumo) {
        validarNaoNegativoFinito(consumo, "Consumo");
        int consumoInteiro = (int) Math.ceil(Math.min(consumo, getBateria()));
        int bateriaRestante = getBateria() - consumoInteiro;
        if (bateriaRestante < 0) {
            bateriaRestante = 0;
        }
        setBateria(bateriaRestante);
        if (bateriaRestante == 0) {
            setStatus(StatusRobo.SEM_BATERIA);
        }
    }
    public double calculoBateria(Missao missao, double horas) {
        if (missao == null) {
            throw new IllegalArgumentException("Informe a missao.");
        }
        validarNaoNegativoFinito(horas, "Duracao");
        validarNaoNegativoFinito(missao.getConsumoFixoBateria(), "Consumo fixo");
        validarNaoNegativoFinito(getConsumoPorHora(), "Consumo por hora");
        return missao.getConsumoFixoBateria() + getConsumoPorHora() * horas;
    }

    protected static void validarNaoNegativoFinito(double valor, String campo) {
        if (!Double.isFinite(valor) || valor < 0) {
            throw new IllegalArgumentException(campo + " deve ser finito e nao negativo.");
        }
    }
    @Override
    public void enviarMensagem(String mensagem) {
        registrarMensagem(getClass().getSimpleName() + " " + id + " (" + nome + ")", "Central", mensagem);
    }

    @Override
    public void receberMensagem(String mensagem) {
        registrarMensagem("Central", getClass().getSimpleName() + " " + id + " (" + nome + ")", mensagem);
    }

    private void registrarMensagem(String remetente, String destinatario, String mensagem) {
        try {
            RegistroMensagens.registrar(remetente, destinatario, mensagem);
        } catch (IOException e) {
            System.out.println("Erro ao registrar mensagem: " + e.getMessage());
        }
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

    public int getBateria() {
        return bateria;
    }
    public double getConsumoPorHora() {
        return consumoPorHora;
    }
    public void setConsumoPorHora(double consumoPorHora) {
        validarNaoNegativoFinito(consumoPorHora, "Consumo por hora");
        this.consumoPorHora = consumoPorHora;
    }

    public void setBateria(int bateria) {
        if (bateria < 0 || bateria > 100) {
            throw new IllegalArgumentException("A bateria deve estar entre 0 e 100.");
        }
        this.bateria = bateria;
    }

    public StatusRobo getStatus() {
        return status;
    }

    public void setStatus(StatusRobo status) {
        this.status = status;
    }

    public int getMissoesRealizadas() {
        return missoesRealizadas;
    }

    public void setMissoesRealizadas(int missoesRealizadas) {
        this.missoesRealizadas = missoesRealizadas;
    }
}

package Robos;

import Enum.*;
import interfaces.*;

import java.io.Serializable;
import java.util.Scanner;
public abstract class Robo implements Comunicavel, Serializable {
    private String id;
    private String nome;
    private int bateria;
    private StatusRobo status;
    private int missoesRealizadas;

    public Robo(String id, String nome, int bateria, StatusRobo status) {
        this.id = id;
        this.nome = nome;
        this.bateria = bateria;
        this.status = status;
        this.missoesRealizadas = 0;
    }

    public Robo(){
        this.id = null;
        this.nome = null;
        this.bateria = 100;
        this.status = StatusRobo.DISPONIVEL;
        this.missoesRealizadas = 0;
    }

    public abstract void carregar();

    public abstract void executaMissao();

    public void cadastraRobo(Scanner in){
        try {
            System.out.println("Digite o id do robo");
            this.id = in.nextLine();
            System.out.println("Digite o nome do robo: ");
            this.nome = in.nextLine();
        } catch (Exception e){
            System.out.println("Erro ao cadastrar robo");
        }

    }

    @Override
    public abstract void enviarMensagem(String mensagem);

    @Override
    public abstract void receberMensagem(String mensagem);

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

    public void setBateria(int bateria) {
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

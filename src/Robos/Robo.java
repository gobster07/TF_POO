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
    private boolean idBloqueado;
    private String nome;
    private int bateria;
    private StatusRobo status;
    private int missoesRealizadas;
    private double consumoPorHora;
    private int desgaste;
    private Missao missaoAtual;
    public static final int LIMITE_MANUTENCAO = 80;

    public Robo(String id, String nome, int bateria, StatusRobo status) {
        this.id = id;
        this.nome = nome;
        setBateria(bateria);
        setStatus(status);
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

    public abstract void executarMissao(Missao missao);

    public void exibirRobo() {
        System.out.println("ID: " + id);
        System.out.println("Nome: " + nome);
        System.out.println("Bateria: " + bateria + "%");
        System.out.println("Status: " + status);
        System.out.println("Missoes realizadas: " + missoesRealizadas);
        System.out.println("Consumo por hora: " + consumoPorHora);
        System.out.println("Desgaste: " + desgaste + "% (manutencao a partir de " + LIMITE_MANUTENCAO + "%)");
    }

    public int getDesgaste() {
        return desgaste;
    }

    public boolean precisaManutencao() {
        return desgaste >= LIMITE_MANUTENCAO;
    }

    protected int calcularDesgaste(Missao missao) {
        int acrescimo = 10;
        if (missao.getArea().getNivelRisco() == NivelRisco.MEDIO) {
            acrescimo += 5;
        } else if (missao.getArea().getNivelRisco() == NivelRisco.ALTO) {
            acrescimo += 10;
        }
        return acrescimo;
    }

    public void registrarDesgaste(Missao missao) {
        if (missao == null || missao.getArea() == null) {
            throw new IllegalArgumentException("Informe uma missao com area para calcular o desgaste.");
        }
        desgaste = Math.min(100, desgaste + calcularDesgaste(missao));
        if (precisaManutencao()) {
            status = StatusRobo.EM_MANUTENCAO;
        }
    }

    public boolean verificarDisponibilidade() {
        return status == StatusRobo.DISPONIVEL && bateria > 0 && !precisaManutencao() && missaoAtual == null;
    }

    public int consultarBateria() { return getBateria(); }
    public void recarregar() { carregar(); }
    public void exibirResumo() { exibirRobo(); }
    public void executaMissao(Missao missao) { executarMissao(missao); }

    public void iniciarOperacao(Missao missao) {
        if (missao == null || missao.getRoboResponsavel() != this
                || missao.getStatus() != StatusMissao.PENDENTE || !verificarDisponibilidade()) {
            throw new IllegalArgumentException("O robo nao pode iniciar esta missao.");
        }
        executaMissao(missao);
        missaoAtual = missao;
        status = StatusRobo.EM_MISSAO;
    }

    public void atualizarStatusAposOperacao(Missao missao) {
        if (missaoAtual != missao || missao == null || missao.getRoboResponsavel() != this
                || (missao.getStatus() != StatusMissao.CONCLUIDA && missao.getStatus() != StatusMissao.CANCELADA)) {
            throw new IllegalStateException("A operacao precisa ser encerrada pela missao responsavel.");
        }
        missaoAtual = null;
        definirStatusAposOperacao();
    }

    public void registrarFalha(Missao missao) {
        if (missaoAtual != missao || missao == null || missao.getRoboResponsavel() != this) {
            throw new IllegalStateException("O robo nao esta executando esta missao.");
        }
        missaoAtual = null;
        status = StatusRobo.EM_MANUTENCAO;
    }

    public void restaurarOperacao(Missao missao) {
        if (missao == null || missao.getRoboResponsavel() != this || missao.getStatus() != StatusMissao.EM_ANDAMENTO
                || status != StatusRobo.EM_MISSAO || (missaoAtual != null && missaoAtual != missao)) {
            throw new IllegalStateException("Vinculo de operacao invalido.");
        }
        missaoAtual = missao;
    }

    private void definirStatusAposOperacao() {
        status = precisaManutencao() ? StatusRobo.EM_MANUTENCAO
                : bateria == 0 ? StatusRobo.SEM_BATERIA : StatusRobo.DISPONIVEL;
    }

    public void realizarManutencao() {
        if (missaoAtual != null || getStatus() == StatusRobo.EM_MISSAO) {
            throw new IllegalStateException("Nao e possivel fazer manutencao durante uma missao.");
        }
        if (getStatus() != StatusRobo.EM_MANUTENCAO && !precisaManutencao()) {
            throw new IllegalStateException("O robo nao necessita de manutencao.");
        }
        desgaste = 0;
        definirStatusAposOperacao();
        enviarMensagem("Manutencao concluida. Desgaste zerado. Bateria: " + getBateria() + "%.");
    }

    public void cadastraRobo(Scanner in){
        System.out.println("Digite o id do robo");
        setId(in.nextLine().trim());
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
            status = StatusRobo.SEM_BATERIA;
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
        if (idBloqueado && !java.util.Objects.equals(this.id, id)) {
            throw new IllegalArgumentException("O ID de um robo cadastrado nao pode ser alterado.");
        }
        this.id = id;
    }

    public void bloquearId() {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("O robo precisa de um ID antes de ser cadastrado.");
        }
        idBloqueado = true;
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
        if (missaoAtual != null && bateria > this.bateria) {
            throw new IllegalArgumentException("Nao e possivel recarregar durante uma missao.");
        }
        this.bateria = bateria;
    }

    public StatusRobo getStatus() {
        return status;
    }

    public void setStatus(StatusRobo status) {
        if (status == null) throw new IllegalArgumentException("Informe o status do robo.");
        if (status == StatusRobo.EM_MISSAO && this.status != status) {
            throw new IllegalArgumentException("Use a missao para iniciar uma operacao.");
        }
        if (this.status == StatusRobo.EM_MANUTENCAO && status != this.status) {
            throw new IllegalArgumentException("Realize manutencao para liberar o robo.");
        }
        if (missaoAtual != null && status != this.status) {
            throw new IllegalArgumentException("Use a missao para encerrar ou interromper a operacao.");
        }
        if (status == StatusRobo.DISPONIVEL && (bateria == 0 || precisaManutencao())) {
            throw new IllegalArgumentException("O robo precisa de bateria e manutencao em dia.");
        }
        this.status = status;
    }

    public int getMissoesRealizadas() {
        return missoesRealizadas;
    }

    public void registrarConclusao(Missao missao) {
        if (missaoAtual != missao || missao == null || missao.getStatus() != StatusMissao.CONCLUIDA
                || missao.getRoboResponsavel() != this) {
            throw new IllegalStateException("A conclusao deve pertencer a operacao atual.");
        }
        missoesRealizadas++;
    }

    public void setMissoesRealizadas(int missoesRealizadas) {
        if (idBloqueado || missaoAtual != null) throw new IllegalArgumentException("O contador e atualizado pela conclusao das missoes.");
        if (missoesRealizadas < this.missoesRealizadas || missoesRealizadas < 0) {
            throw new IllegalArgumentException("O contador de missoes nao pode diminuir.");
        }
        this.missoesRealizadas = missoesRealizadas;
    }
}

package Operacional;

import Area.AreaOperacao;
import Enum.*;
import Robos.Robo;
import Mensagens.ConversasRobos;
import java.io.Serializable;
import java.util.Scanner;
import java.time.Duration;
import java.time.LocalDateTime;

public class Missao implements Serializable {
    private static final long serialVersionUID = 4188654762073110789L;
    private String id;
    private boolean cadastrada;
    private String descricao;
    private TipoMissao tipo;
    private AreaOperacao area;
    private int prioridade;
    private StatusMissao status = StatusMissao.PENDENTE;
    private Robo roboResponsavel;
    private String resultado;
    private double consumoFixoBateria;
    private LocalDateTime dataInicio;

    public void exibirMissao() {
        System.out.println("ID: " + id);
        System.out.println("Descricao: " + descricao);
        System.out.println("Tipo: " + tipo);
        System.out.println("Area: " + (area == null ? "Nao definida" : area.getNome()));
        System.out.println("Prioridade: " + prioridade);
        System.out.println("Status: " + status);
        System.out.println("Robo responsavel: " + (roboResponsavel == null ? "Nao definido" : roboResponsavel.getNome()));
        System.out.println("Resultado: " + resultado);
        System.out.println();
    }

    private void validarAtribuicao(Robo robo) {
        if (status != StatusMissao.PENDENTE) throw new IllegalArgumentException("A missao precisa estar pendente.");
        if (robo == null || !robo.verificarDisponibilidade()) throw new IllegalArgumentException("Selecione um robo disponivel.");
        if (roboResponsavel != null && roboResponsavel != robo) throw new IllegalArgumentException("A missao ja possui outro responsavel.");
        robo.executaMissao(this);
        if (robo.calculoBateria(this, 0) > robo.getBateria()) throw new IllegalArgumentException("Bateria insuficiente para iniciar a missao.");
    }

    public void atribuirRobo(Robo robo) {
        validarAtribuicao(robo);
        roboResponsavel = robo;
    }

    public void iniciar() {
        validarAtribuicao(roboResponsavel);
        roboResponsavel.iniciarOperacao(this);
        status = StatusMissao.EM_ANDAMENTO;
        dataInicio = LocalDateTime.now();
        roboResponsavel.receberMensagem("Iniciar missao " + id + " do tipo " + tipo + ".");
        roboResponsavel.enviarMensagem("Missao " + id + " iniciada.");
        ConversasRobos.conversar(this);
    }

    public void executar(Robo robo) {
        try {
            atribuirRobo(robo);
            iniciar();
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao executar missao: " + e.getMessage());
        }
    }

    public void exibirResumo() { exibirMissao(); }

    public void bloquearCadastro() { cadastrada = true; }

    private void validarEdicao() {
        if (dataInicio != null || status == StatusMissao.CONCLUIDA || status == StatusMissao.CANCELADA) {
            throw new IllegalArgumentException("Nao e permitido editar uma missao iniciada ou encerrada.");
        }
    }

    public void registrarMissao(Scanner in, CentralResgate central) {
        if (cadastrada) throw new IllegalArgumentException("A missao ja esta cadastrada.");
        if (central.getMissoes().contains(this)) {
            System.out.println("Esta missao ja esta cadastrada. Crie uma nova missao.");
            return;
        }
        if (central.getAreas().isEmpty()) {
            System.out.println("Cadastre uma area antes de cadastrar uma missao.");
            return;
        }

        try {
            System.out.println("Digite o ID da missao:");
            String novoId = in.nextLine().trim();
            if (novoId.isBlank()) {
                System.out.println("O ID nao pode ficar vazio.");
                return;
            }
            if (central.getMissoes().stream().anyMatch(m -> novoId.equals(m.getId()))) {
                System.out.println("Ja existe uma missao com esse ID.");
                return;
            }

            System.out.println("Digite a descricao da missao:");
            String novaDescricao = in.nextLine();
            System.out.println("Digite o tipo da missao: RECONHECIMENTO, LOCALIZACAO_DE_PESSOAS ou TRANSPORTE_DE_SUPRIMENTOS");
            TipoMissao novoTipo = TipoMissao.valueOf(in.nextLine().trim());
            System.out.println("Digite a prioridade da missao (numero inteiro):");
            int novaPrioridade = Integer.parseInt(in.nextLine().trim());

            System.out.println("Areas cadastradas:");
            for (AreaOperacao areaCadastrada : central.getAreas()) {
                System.out.println(areaCadastrada.getId() + " - " + areaCadastrada.getNome());
            }
            System.out.println("Digite o ID da area da missao:");
            String idArea = in.nextLine().trim();
            AreaOperacao novaArea = null;
            for (AreaOperacao areaCadastrada : central.getAreas()) {
                if (idArea.equals(areaCadastrada.getId())) {
                    novaArea = areaCadastrada;
                    break;
                }
            }
            if (novaArea == null) {
                System.out.println("Area nao encontrada.");
                return;
            }

            this.id = novoId;
            this.descricao = novaDescricao;
            this.tipo = novoTipo;
            this.prioridade = novaPrioridade;
            this.area = novaArea;
            if (novaArea.getNivelRisco() == NivelRisco.BAIXO) {this.consumoFixoBateria = 10;
            } else if (novaArea.getNivelRisco() == NivelRisco.MEDIO) {this.consumoFixoBateria = 25;
            } else if (novaArea.getNivelRisco() == NivelRisco.ALTO) {this.consumoFixoBateria = 50;
            } else {throw new IllegalArgumentException("A area precisa ter nivel de risco definido.");}
            this.status = StatusMissao.PENDENTE;
            this.roboResponsavel = null;
            this.resultado = null;
            central.registrarMissao(this);
            System.out.println("Missao cadastrada com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro no Cadastro.");
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        validarEdicao();
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Informe o ID da missao.");
        if (cadastrada && !java.util.Objects.equals(this.id, id)) throw new IllegalArgumentException("O ID cadastrado nao pode mudar.");
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        validarEdicao();
        this.descricao = descricao;
    }

    public TipoMissao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMissao tipo) {
        validarEdicao();
        if (cadastrada && this.tipo != tipo) throw new IllegalArgumentException("O tipo de uma missao cadastrada nao pode mudar.");
        if (tipo == null) throw new IllegalArgumentException("Informe tipo.");
        this.tipo = tipo;
    }

    public AreaOperacao getArea() {
        return area;
    }

    public void setArea(AreaOperacao area) {
        validarEdicao();
        if (cadastrada && this.area != area) throw new IllegalArgumentException("A area de uma missao cadastrada nao pode mudar.");
        if (area == null) throw new IllegalArgumentException("Informe area.");
        this.area = area;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        validarEdicao();
        if (prioridade < 0) throw new IllegalArgumentException("Prioridade nao pode ser negativa.");
        this.prioridade = prioridade;
    }

    public StatusMissao getStatus() {
        return status;
    }

    public void setStatus(StatusMissao status) {
        if (status == null || this.status != status) {
            throw new IllegalArgumentException("Use iniciar, concluir, cancelar ou registrar falha para mudar o status.");
        }
    }

    public Robo getRoboResponsavel() {
        return roboResponsavel;
    }

    public void setRoboResponsavel(Robo roboResponsavel) {
        atribuirRobo(roboResponsavel);
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        validarEdicao();
        this.resultado = resultado;
    }
    public double getConsumoFixoBateria() {
        return consumoFixoBateria;
    }

    public void setConsumoFixoBateria(double consumoFixoBateria) {
        validarEdicao();
        if (!Double.isFinite(consumoFixoBateria) || consumoFixoBateria < 0) {
            throw new IllegalArgumentException("O consumo fixo deve ser finito e nao negativo.");
        }
        this.consumoFixoBateria = consumoFixoBateria;
    }

    public void interromperPorFalha() {
        if (status != StatusMissao.EM_ANDAMENTO || roboResponsavel == null || dataInicio == null) {
            throw new IllegalStateException("A missao precisa estar em andamento para registrar uma falha.");
        }
        long minutos = Duration.between(dataInicio, LocalDateTime.now()).toMinutes();
        double consumo = roboResponsavel.calculoBateria(this, minutos / 60.0);
        roboResponsavel.descontarBateria(consumo);
        roboResponsavel.registrarDesgaste(this);
        Robo roboComFalha = roboResponsavel;
        roboComFalha.registrarFalha(this);
        status = StatusMissao.PENDENTE;
        roboResponsavel = null;
        dataInicio = null;
        resultado = "Missao interrompida por falha do robo " + roboComFalha.getId() + ".";
        roboComFalha.enviarMensagem("Missao " + id + ": " + resultado + " Solicitando substituicao.");
    }

    public void cancelar() {
        if (status != StatusMissao.PENDENTE && status != StatusMissao.EM_ANDAMENTO) {
            throw new IllegalStateException("Apenas missoes pendentes ou em andamento podem ser canceladas.");
        }
        if (status == StatusMissao.EM_ANDAMENTO) {
            if (roboResponsavel == null || dataInicio == null) {
                throw new IllegalStateException("A missao em andamento precisa de robo e data de inicio.");
            }
            long minutos = Duration.between(dataInicio, LocalDateTime.now()).toMinutes();
            double consumo = roboResponsavel.calculoBateria(this, minutos / 60.0);
            roboResponsavel.descontarBateria(consumo);
            roboResponsavel.registrarDesgaste(this);
            roboResponsavel.receberMensagem("Cancelar missao " + id + ".");
            roboResponsavel.enviarMensagem("Cancelamento da missao " + id + " confirmado.");
        }
        boolean estavaEmAndamento = dataInicio != null;
        status = StatusMissao.CANCELADA;
        resultado = "Missao cancelada.";
        if (estavaEmAndamento) roboResponsavel.atualizarStatusAposOperacao(this);
        System.out.println(resultado);
    }

    public void concluir() {
        if (status != StatusMissao.EM_ANDAMENTO || roboResponsavel == null || dataInicio == null) {
            throw new IllegalStateException("A missao nao esta em andamento.");
        }
        long minutos = Duration.between(dataInicio, LocalDateTime.now()).toMinutes();
        double horas = minutos / 60.0;

        double consumo = roboResponsavel.calculoBateria(this, horas);

        if (consumo > roboResponsavel.getBateria()) {
            roboResponsavel.setBateria(0);
            roboResponsavel.registrarDesgaste(this);
            status = StatusMissao.CANCELADA;
            roboResponsavel.atualizarStatusAposOperacao(this);
            resultado = "A bateria teria acabado durante a missao.";
            System.out.println(resultado);
        } else {
            roboResponsavel.descontarBateria(consumo);
            roboResponsavel.registrarDesgaste(this);
            status = StatusMissao.CONCLUIDA;
            resultado = "Missao concluida com sucesso pelo robo " + roboResponsavel.getNome() + ".";
            roboResponsavel.registrarConclusao(this);

            roboResponsavel.atualizarStatusAposOperacao(this);
            System.out.println(resultado);
        }
        roboResponsavel.enviarMensagem("Missao " + id + ": " + resultado
                + " Bateria restante: " + roboResponsavel.getBateria() + "%. Desgaste: "
                + roboResponsavel.getDesgaste() + "%.");
    }
}

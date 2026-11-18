package Operacional;

import Area.AreaOperacao;
import Enum.*;
import Robos.Robo;
import java.io.Serializable;
import java.util.Scanner;
import java.time.Duration;
import java.time.LocalDateTime;

public class Missao implements Serializable {
    private String id;
    private String descricao;
    private TipoMissao tipo;
    private AreaOperacao area;
    private int prioridade;
    private StatusMissao status;
    private Robo roboResponsavel;
    private String resultado;
    private double consumoFixoBateria;
    private LocalDateTime dataInicio;

    public void executar(Robo robo) {
        try {
            if (robo == null) {
                throw new IllegalArgumentException("Selecione um robo.");
            }
            if (status != StatusMissao.PENDENTE) {
                throw new IllegalArgumentException("A missao precisa estar pendente.");
            }
            if (robo.getStatus() != StatusRobo.DISPONIVEL) {
                throw new IllegalArgumentException("O robo nao esta disponivel.");
            }
            robo.executaMissao(this);
            if (robo.calculoBateria(this, 0) > robo.getBateria()) {
                throw new IllegalArgumentException("Bateria insuficiente para iniciar a missao.");
            }
            if (this.status != StatusMissao.CONCLUIDA) {
                this.roboResponsavel = robo;
                this.status = StatusMissao.EM_ANDAMENTO;
                dataInicio = LocalDateTime.now();
                robo.setStatus(StatusRobo.EM_MISSAO);
            }

        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao executar missao: " + e.getMessage());
        }
    }




    public void registrarMissao(Scanner in, CentralResgate central) {
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
            central.getMissoes().add(this);
            central.getMissoesPendentes().add(this);
            System.out.println("Missao cadastrada com sucesso.");
        } catch (Exception e) {
            System.out.println("Erro no Cadastro.");
        }
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public TipoMissao getTipo() {
        return tipo;
    }

    public void setTipo(TipoMissao tipo) {
        this.tipo = tipo;
    }

    public AreaOperacao getArea() {
        return area;
    }

    public void setArea(AreaOperacao area) {
        this.area = area;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(int prioridade) {
        this.prioridade = prioridade;
    }

    public StatusMissao getStatus() {
        return status;
    }

    public void setStatus(StatusMissao status) {
        this.status = status;
    }

    public Robo getRoboResponsavel() {
        return roboResponsavel;
    }

    public void setRoboResponsavel(Robo roboResponsavel) {
        this.roboResponsavel = roboResponsavel;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
    public double getConsumoFixoBateria() {
        return consumoFixoBateria;
    }

    public void setConsumoFixoBateria(double consumoFixoBateria) {
        this.consumoFixoBateria = consumoFixoBateria;
    }
    public void encerrar() {
        if (status != StatusMissao.EM_ANDAMENTO || roboResponsavel == null || dataInicio == null) {
            throw new IllegalStateException("A missao nao esta em andamento.");
        }
        long minutos = Duration.between(dataInicio, LocalDateTime.now()).toMinutes();
        double horas = minutos / 60.0;

        double consumo = roboResponsavel.calculoBateria(this, horas);

        if (consumo > roboResponsavel.getBateria()) {
            roboResponsavel.setBateria(0);
            roboResponsavel.setStatus(StatusRobo.SEM_BATERIA);
            status = StatusMissao.CANCELADA;
            resultado = "A bateria teria acabado durante a missao.";
            System.out.println(resultado);
        } else {
            roboResponsavel.descontarBateria(consumo);
            status = StatusMissao.CONCLUIDA;

            if (roboResponsavel.getBateria() > 0) {
                roboResponsavel.setStatus(StatusRobo.DISPONIVEL);
            }
        }
    }
}

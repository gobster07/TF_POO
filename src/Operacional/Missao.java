package Operacional;

import Area.AreaOperacao;
import Enum.*;
import Robos.Robo;
import java.io.Serializable;

public class Missao implements Serializable {
    private String id;
    private String descricao;
    private String tipo;
    private AreaOperacao area;
    private int prioridade;
    private StatusMissao status;
    private Robo roboResponsavel;
    private String resultado;



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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
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
}

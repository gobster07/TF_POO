package Operacional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import Save.Salvamento;
import java.io.IOException;
import java.util.List;

public class Relatorio implements Serializable {
    private LocalDateTime dataGeracao;
    private int totalRobos;
    private int totalMissoes;
    private int missoesConcluidas;
    private int missoesPendentes;

    public LocalDateTime getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(LocalDateTime dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public int getTotalRobos() {
        return totalRobos;
    }

    public void setTotalRobos(int totalRobos) {
        this.totalRobos = totalRobos;
    }

    public int getTotalMissoes() {
        return totalMissoes;
    }

    public void setTotalMissoes(int totalMissoes) {
        this.totalMissoes = totalMissoes;
    }

    public int getMissoesConcluidas() {
        return missoesConcluidas;
    }

    public void setMissoesConcluidas(int missoesConcluidas) {
        this.missoesConcluidas = missoesConcluidas;
    }

    public int getMissoesPendentes() {
        return missoesPendentes;
    }

    public void setMissoesPendentes(int missoesPendentes) {
        this.missoesPendentes = missoesPendentes;
    }
    public String gerarResumo() {
        setDataGeracao(LocalDateTime.now());
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return "Resumo da atuação da central\n"
                + "Data de geração: " + getDataGeracao().format(formato) + "\n"
                + "Total de robôs: " + getTotalRobos() + "\n"
                + "Total de missões: " + getTotalMissoes() + "\n"
                + "Missões concluídas: " + getMissoesConcluidas() + "\n"
                + "Missões pendentes: " + getMissoesPendentes() + "\n";
    }
    public void salvarEmArquivo() throws IOException {
        Salvamento salvamento = new Salvamento();
        salvamento.gravarRelatorio("relatorio_central.txt", gerarResumo());
    }

    public String gerarResumo(Missao missao) {
        setDataGeracao(LocalDateTime.now());

        String area;
        if (missao.getArea() == null) {
            area = "Não informada";
        } else {
            area = missao.getArea().getNome();
        }

        String robo;
        if (missao.getRoboResponsavel() == null) {
            robo = "Não atribuído";
        } else {
            robo = missao.getRoboResponsavel().getNome();
        }

        String resultado;
        if (missao.getResultado() == null) {
            resultado = "Não informado";
        } else {
            resultado = missao.getResultado();
        }

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return "Relatório da missão\n"
                + "Data de geração: " + getDataGeracao().format(formato) + "\n"
                + "ID: " + missao.getId() + "\n"
                + "Descrição: " + missao.getDescricao() + "\n"
                + "Tipo: " + missao.getTipo() + "\n"
                + "Área: " + area + "\n"
                + "Prioridade: " + missao.getPrioridade() + "\n"
                + "Status: " + missao.getStatus() + "\n"
                + "Robô responsável: " + robo + "\n"
                + "Resultado: " + resultado + "\n";
    }
    public void salvarEmArquivo(Missao missao) throws IOException {
        Salvamento salvamento = new Salvamento();
        salvamento.gravarRelatorio(
                "relatorio_missao_" + missao.getId() + ".txt",
                gerarResumo(missao)
        );
    }
    public String gerarResumo(List<Missao> missoes) {
        String resumo = "";
        for (Missao missao : missoes) {
            resumo = resumo + gerarResumo(missao);
            resumo = resumo + "\n--------------------\n\n";
        }
        return resumo;
    }
    public void salvarEmArquivo(List<Missao> missoes) throws IOException {
        Salvamento salvamento = new Salvamento();
        salvamento.gravarRelatorio(
                "relatorios_missoes.txt",
                gerarResumo(missoes)
        );
    }
}

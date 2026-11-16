package Operacional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
}

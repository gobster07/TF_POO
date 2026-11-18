package Operacional;

import Area.AreaOperacao;
import Robos.Drone;
import Robos.Robo;
import Robos.RoboTerrestre;
import Save.Salvamento;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;


public class CentralResgate implements Serializable {
    private String nome;
    private List<Robo> robos;
    private List<AreaOperacao> areas;
    private List<Missao> missoes;
    private Queue<Missao> missoesPendentes;

    public CentralResgate() {
        this.nome = "Central Resgate";
        robos = new ArrayList<>();
        areas = new ArrayList<>();
        missoes = new ArrayList<>();
        missoesPendentes = new LinkedList<>();
        load();
    }

    public void load(){
        try {
            Salvamento salvamento = new Salvamento();
            CentralResgate centralSalva = salvamento.carregarCentral();
            this.nome = centralSalva.getNome();
            this.robos = centralSalva.getRobos();
            this.areas = centralSalva.getAreas();
            this.missoes = centralSalva.getMissoes();
            this.missoesPendentes = centralSalva.getMissoesPendentes();
            System.out.println("Dados carregados com sucesso.");
        } catch (FileNotFoundException e) {
            System.out.println("Nenhum salvamento encontrado.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar os dados: " + e.getMessage());
        }
    }

    public void registroArea(Scanner in){
        AreaOperacao area = new AreaOperacao();
        area.cadastroArea(in);
        try {
            areas.add(area);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao cadastrar area.");
        }
    }

    public void cadastroMissao(){
        cadastroMissao(new Scanner(System.in));
    }

    public void cadastroMissao(Scanner in){
        Missao missao = new Missao();
        missao.registrarMissao(in,this);
    }

    public void cadastroRobo(Scanner in){
        try {
            System.out.println("Qual tipo de robo gostaria de cadastrar");
            System.out.println("1 - Drone");
            System.out.println("2 - Robo Terresetre");
            int opcao = in.nextInt();
            in.nextLine();
            switch (opcao) {
                case 1:
                    Drone drone = new Drone();
                    drone.cadastraRobo(in);
                    this.robos.add(drone);
                    break;
                case 2:
                    RoboTerrestre roboTerrestre = new RoboTerrestre();
                    roboTerrestre.cadastraRobo(in);
                    this.robos.add(roboTerrestre);
                    break;
                case 0:
                    break;
            }
        } catch (Exception e){
            System.out.println("Erro ao cadastrar robo: " + e.getMessage());
        }


    }

    public void lerAreas() {
        if (areas == null || areas.isEmpty()) {
            System.out.println("Nenhuma area cadastrada.");
            return;
        }
        for (AreaOperacao area : areas) {
            area.exibirInformacoes();
            System.out.println();
        }
    }

    public void lerRobos() {
        if (robos == null || robos.isEmpty()) {
            System.out.println("Nenhum robo cadastrado.");
            return;
        }
        for (Robo robo : robos) {
            System.out.println("ID: " + robo.getId());
            System.out.println("Nome: " + robo.getNome());
            System.out.println("Bateria: " + robo.getBateria());
            System.out.println("Status: " + robo.getStatus());
            System.out.println("Missoes realizadas: " + robo.getMissoesRealizadas());
            System.out.println();
        }
    }

    public void lerMissoes() {
        if (missoes == null || missoes.isEmpty()) {
            System.out.println("Nenhuma missao cadastrada.");
            return;
        }
        for (Missao missao : missoes) {
            exibirMissao(missao);
        }
    }

    public void lerMissoesPendentes() {
        if (missoesPendentes == null || missoesPendentes.isEmpty()) {
            System.out.println("Nenhuma missao pendente.");
            return;
        }
        for (Missao missao : missoesPendentes) {
            exibirMissao(missao);
        }
    }

    private void exibirMissao(Missao missao) {
        System.out.println("ID: " + missao.getId());
        System.out.println("Descricao: " + missao.getDescricao());
        System.out.println("Tipo: " + missao.getTipo());
        System.out.println("Area: " + (missao.getArea() == null ? "Nao definida" : missao.getArea().getNome()));
        System.out.println("Prioridade: " + missao.getPrioridade());
        System.out.println("Status: " + missao.getStatus());
        System.out.println("Robo responsavel: " + (missao.getRoboResponsavel() == null ? "Nao definido" : missao.getRoboResponsavel().getNome()));
        System.out.println("Resultado: " + missao.getResultado());
        System.out.println();
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Robo> getRobos() {
        return robos;
    }

    public void setRobos(List<Robo> robos) {
        this.robos = robos;
    }

    public List<AreaOperacao> getAreas() {
        return areas;
    }

    public void setAreas(List<AreaOperacao> areas) {
        this.areas = areas;
    }

    public List<Missao> getMissoes() {
        return missoes;
    }

    public void setMissoes(List<Missao> missoes) {
        this.missoes = missoes;
    }

    public Queue<Missao> getMissoesPendentes() {
        return missoesPendentes;
    }

    public void setMissoesPendentes(Queue<Missao> missoesPendentes) {
        this.missoesPendentes = missoesPendentes;
    }
}

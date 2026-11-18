package Operacional;

import Area.AreaOperacao;
import Enum.StatusMissao;
import Enum.StatusRobo;
import Robos.Drone;
import Robos.Robo;
import Robos.RoboTerrestre;
import Save.Salvamento;
import Mensagens.ConversasRobos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.InvalidObjectException;
import java.util.*;


public class CentralResgate implements Serializable {
    private static final long serialVersionUID = -6496946857787795122L;
    private String nome;
    private Map<String, Robo> robos;
    private List<AreaOperacao> areas;
    private List<Missao> missoes;
    private Queue<Missao> missoesPendentes;

    public CentralResgate() {
        this.nome = "Central Resgate";
        robos = new LinkedHashMap<>();
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



    public void cadastroMissao(Scanner in){
        Missao missao = new Missao();
        missao.registrarMissao(in,this);
    }

    public void cadastroRobo(Scanner in){
        try {
            System.out.println("Qual tipo de robo gostaria de cadastrar");
            System.out.println("1 - Drone");
            System.out.println("2 - Robo Terresetre");
            int opcao = Integer.parseInt(in.nextLine().trim());
            switch (opcao) {
                case 1:
                    Drone drone = new Drone();
                    drone.cadastraRobo(in);
                    cadastrarRobo(drone);
                    break;
                case 2:
                    RoboTerrestre roboTerrestre = new RoboTerrestre();
                    roboTerrestre.cadastraRobo(in);
                    cadastrarRobo(roboTerrestre);
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

    public void recarregarRobos() {
        int quantidade = 0;
        for (Robo robo : robos.values()) {
            if (robo.getStatus() == StatusRobo.SEM_BATERIA || robo.getStatus() == StatusRobo.DISPONIVEL) {
                robo.carregar();
                quantidade++;
            }
        }
        System.out.println("Robos recarregados: " + quantidade);
    }

    public void lerRobos() {
        if (robos == null || robos.isEmpty()) {
            System.out.println("Nenhum robo cadastrado.");
            return;
        }
        for (Robo robo : robos.values()) {
            robo.exibirRobo();
            System.out.println();
        }
    }

    public void lerMissoes() {
        if (missoes == null || missoes.isEmpty()) {
            System.out.println("Nenhuma missao cadastrada.");
            return;
        }
        for (Missao missao : missoes) {
            missao.exibirMissao();
        }
    }

    public void lerMissoesPendentes() {
        if (missoesPendentes == null || missoesPendentes.isEmpty()) {
            System.out.println("Nenhuma missao pendente.");
            return;
        }
        for (Missao missao : missoesPendentes) {
            missao.exibirMissao();
        }
    }

    public void salvar(){
        try {
            Salvamento salvamento = new Salvamento();
            salvamento.gravarCentral(this);
            System.out.println("Central salva com sucesso.");
        } catch (IOException e) {
            System.out.println("Erro ao salvar a central: " + e.getMessage());
        }
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Map<String, Robo> getRobos() {
        return robos;
    }

    public void setRobos(Map<String, Robo> robos) {
        if (robos == null) {
            throw new IllegalArgumentException("Informe a colecao de robos.");
        }
        Map<String, Robo> novosRobos = new LinkedHashMap<>();
        for (Robo robo : robos.values()) {
            validarCadastroRobo(robo, novosRobos);
            novosRobos.put(robo.getId(), robo);
        }
        this.robos = novosRobos;
    }

    public void cadastrarRobo(Robo robo) {
        validarCadastroRobo(robo, robos);
        robos.put(robo.getId(), robo);
    }

    private static void validarCadastroRobo(Robo robo, Map<String, Robo> cadastrados) {
        if (robo == null || robo.getId() == null || robo.getId().isBlank()) {
            throw new IllegalArgumentException("O robo precisa de um ID.");
        }
        if (cadastrados.containsKey(robo.getId())) {
            throw new IllegalArgumentException("Ja existe um robo com o ID " + robo.getId() + ".");
        }
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream entrada) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField campos = entrada.readFields();
        nome = (String) campos.get("nome", null);
        areas = (List<AreaOperacao>) campos.get("areas", null);
        missoes = (List<Missao>) campos.get("missoes", null);
        missoesPendentes = (Queue<Missao>) campos.get("missoesPendentes", null);
        Object colecaoSalva = campos.get("robos", null);
        robos = new LinkedHashMap<>();
        Collection<?> antigos;
        if (colecaoSalva instanceof Map<?, ?> mapa) {
            antigos = mapa.values();
        } else if (colecaoSalva instanceof List<?> lista) {
            antigos = lista;
        } else {
            throw new InvalidObjectException("Colecao de robos invalida no salvamento.");
        }
        try {
            for (Object objeto : antigos) {
                if (!(objeto instanceof Robo robo)) {
                    throw new IllegalArgumentException("Objeto invalido na colecao de robos.");
                }
                cadastrarRobo(robo);
            }
        } catch (IllegalArgumentException e) {
            InvalidObjectException erro = new InvalidObjectException(e.getMessage());
            erro.initCause(e);
            throw erro;
        }
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

    public void concluirMissao(Scanner in) {
        List<Missao> emAndamento = missoes.stream()
                .filter(m -> m.getStatus() == StatusMissao.EM_ANDAMENTO).toList();
        if (emAndamento.isEmpty()) {
            System.out.println("Nenhuma missao em andamento.");
            return;
        }
        System.out.println("Missoes em andamento:");
        emAndamento.forEach(m -> System.out.println(m.getId() + " - " + m.getDescricao() + " - " + m.getTipo()));
        System.out.println("Digite o ID da missao que deseja encerrar:");
        if (!in.hasNextLine()) {
            return;
        }
        String id = in.nextLine().trim();
        Missao missao = emAndamento.stream().filter(m -> id.equals(m.getId())).findFirst().orElse(null);
        if (missao == null) {
            System.out.println("Missao em andamento nao encontrada.");
            return;
        }
        try {
            missao.concluir();
            missoesPendentes.remove(missao);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao concluir missao: " + e.getMessage());
        }
    }

    public void cancelarMissao(Scanner in) {
        List<Missao> cancelaveis = missoes.stream()
                .filter(m -> m.getStatus() == StatusMissao.PENDENTE || m.getStatus() == StatusMissao.EM_ANDAMENTO)
                .toList();
        if (cancelaveis.isEmpty()) {
            System.out.println("Nenhuma missao disponivel para cancelamento.");
            return;
        }
        System.out.println("Missoes pendentes ou em andamento:");
        cancelaveis.forEach(m -> System.out.println(m.getId() + " - " + m.getDescricao() + " - " + m.getStatus()));
        System.out.println("Digite o ID da missao que deseja cancelar:");
        if (!in.hasNextLine()) {
            return;
        }
        String id = in.nextLine().trim();
        Missao missao = cancelaveis.stream().filter(m -> id.equals(m.getId())).findFirst().orElse(null);
        if (missao == null) {
            System.out.println("Missao disponivel para cancelamento nao encontrada.");
            return;
        }
        try {
            missao.cancelar();
            missoesPendentes.remove(missao);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao cancelar missao: " + e.getMessage());
        }
    }

    public void registrarFalhaRobo(Scanner in) {
        List<Missao> emAndamento = missoes.stream()
                .filter(m -> m.getStatus() == StatusMissao.EM_ANDAMENTO && m.getRoboResponsavel() != null)
                .toList();
        if (emAndamento.isEmpty()) {
            System.out.println("Nenhum robo em missao para registrar falha.");
            return;
        }
        System.out.println("Robos em missao:");
        emAndamento.forEach(m -> System.out.println(m.getRoboResponsavel().getId() + " - "
                + m.getRoboResponsavel().getNome() + " - Missao " + m.getId()));
        System.out.println("Digite o ID do robo que apresentou falha:");
        if (!in.hasNextLine()) {
            return;
        }
        try {
            reatribuirMissaoPorFalha(in.nextLine().trim());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao registrar falha: " + e.getMessage());
        }
    }

    public void reatribuirMissaoPorFalha(String idRobo) {
        Robo roboComFalha = robos.get(idRobo);
        if (roboComFalha == null) {
            throw new IllegalArgumentException("Robo nao encontrado.");
        }
        Missao missao = missoes.stream()
                .filter(m -> m.getStatus() == StatusMissao.EM_ANDAMENTO && m.getRoboResponsavel() == roboComFalha)
                .findFirst().orElse(null);
        if (missao == null) {
            throw new IllegalStateException("O robo nao possui missao em andamento.");
        }
        missao.interromperPorFalha();
        if (!missoesPendentes.contains(missao)) {
            missoesPendentes.add(missao);
        }
        for (Robo candidato : robos.values()) {
            if (candidato == roboComFalha || candidato.getStatus() != StatusRobo.DISPONIVEL) {
                continue;
            }
            try {
                candidato.executaMissao(missao);
                if (candidato.calculoBateria(missao, 0) > candidato.getBateria()) {
                    continue;
                }
            } catch (IllegalArgumentException e) {
                continue;
            }
            missao.executar(candidato);
            if (missao.getStatus() == StatusMissao.EM_ANDAMENTO && missao.getRoboResponsavel() == candidato) {
                missoesPendentes.remove(missao);
                candidato.receberMensagem("Assumir missao " + missao.getId()
                        + " apos falha do robo " + roboComFalha.getId() + ".");
                System.out.println("Missao " + missao.getId() + " reatribuida ao robo " + candidato.getId() + ".");
                return;
            }
        }
        System.out.println("Nenhum substituto compativel disponivel. Missao " + missao.getId() + " permanece pendente.");
    }

    public void conversarComRobo(Scanner in) {
        List<Missao> emOperacao = missoes.stream()
                .filter(m -> m.getStatus() == StatusMissao.EM_ANDAMENTO
                        && m.getRoboResponsavel() != null
                        && m.getRoboResponsavel().getStatus() == StatusRobo.EM_MISSAO)
                .toList();
        if (emOperacao.isEmpty()) {
            System.out.println("Nenhum robo em operacao para conversar.");
            return;
        }
        System.out.println("Missoes em andamento e robos em operacao:");
        emOperacao.forEach(m -> System.out.println(m.getId() + " - " + m.getDescricao()
                + " - " + m.getRoboResponsavel().getNome()));
        System.out.println("Digite o ID da missao para conversar com o robo:");
        if (!in.hasNextLine()) {
            return;
        }
        String id = in.nextLine().trim();
        Missao missao = emOperacao.stream().filter(m -> id.equals(m.getId())).findFirst().orElse(null);
        if (missao == null) {
            System.out.println("Missao com robo em operacao nao encontrada.");
            return;
        }
        ConversasRobos.conversar(missao);
    }

    public void executarMissao(Scanner in) {
        if (missoes.stream().noneMatch(m -> m.getStatus() == StatusMissao.PENDENTE)) {
            System.out.println("Nenhuma missao pendente.");
            return;
        }
        if (robos.values().stream().noneMatch(r -> r.getStatus() == StatusRobo.DISPONIVEL)) {
            System.out.println("Nenhum robo disponivel.");
            return;
        }
        System.out.println("Missoes pendentes:");
        missoes.stream()
                .filter(m -> m.getStatus() == StatusMissao.PENDENTE)
                .forEach(m -> System.out.println(m.getId() + " - " + m.getDescricao() + " - " + m.getTipo()));
        System.out.println("Digite o ID da missao:");
        if (!in.hasNextLine()) {
            return;
        }
        String idMissao = in.nextLine().trim();
        Missao missao = missoes.stream()
                .filter(m -> idMissao.equals(m.getId()) && m.getStatus() == StatusMissao.PENDENTE)
                .findFirst().orElse(null);
        if (missao == null) {
            System.out.println("Missao pendente nao encontrada.");
            return;
        }
        System.out.println("Robos disponiveis:");
        robos.values().stream()
                .filter(r -> r.getStatus() == StatusRobo.DISPONIVEL)
                .forEach(r -> System.out.println(r.getId() + " - " + r.getNome() + " - " + r.getBateria() + "% de bateria"));
        System.out.println("Digite o ID do robo:");
        if (!in.hasNextLine()) {
            return;
        }
        String idRobo = in.nextLine().trim();
        Robo robo = robos.get(idRobo);
        if (robo == null || robo.getStatus() != StatusRobo.DISPONIVEL) {
            System.out.println("Robo disponivel nao encontrado.");
            return;
        }
        missao.executar(robo);
        if (missao.getStatus() == StatusMissao.EM_ANDAMENTO && missao.getRoboResponsavel() == robo) {
            missoesPendentes.remove(missao);
            System.out.println("Missao " + missao.getId() + " iniciada com o robo " + robo.getId() + ".");
        }
    }
}

package Operacional;

import Area.AreaOperacao;
import Enum.StatusMissao;
import Enum.StatusRobo;
import Enum.NivelRisco;
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
            this.robos = new LinkedHashMap<>(centralSalva.getRobos());
            this.areas = new ArrayList<>(centralSalva.getAreas());
            this.missoes = new ArrayList<>(centralSalva.getMissoes());
            this.missoesPendentes = new LinkedList<>(centralSalva.getMissoesPendentes());
            System.out.println("Dados carregados com sucesso.");
        } catch (FileNotFoundException e) {
            System.out.println("Nenhum salvamento encontrado.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao carregar os dados: " + e.getMessage());
        }
    }

    public void registroArea(Scanner in){
        try {
            AreaOperacao area = new AreaOperacao();
            area.cadastroArea(in);
            cadastrarArea(area);
            System.out.println("Area cadastrada com sucesso.");
        } catch (IllegalArgumentException | NoSuchElementException e) {
            System.out.println("Erro ao cadastrar area: " + e.getMessage());
        }
    }

    private static void validarArea(AreaOperacao area) {
        if (area == null || area.getId() == null || area.getId().isBlank() || area.getHorario() == null
                || area.getTipoTerreno() == null || area.getNivelRisco() == null
                || !Double.isFinite(area.getAltitude()) || area.getAltitude() < 0) {
            throw new IllegalArgumentException("Informe uma area com ID, horario, terreno e risco.");
        }
    }

    public void cadastrarArea(AreaOperacao area) {
        validarArea(area);
        if (areas.stream().anyMatch(a -> a.getId().equals(area.getId()))) throw new IllegalArgumentException("ID de area repetido.");
        area.bloquearId();
        areas.add(area);
    }

    private void validarMissao(Missao missao) {
        if (missao == null || missao.getId() == null || missao.getId().isBlank() || missao.getTipo() == null
                || missao.getStatus() == null || !areas.contains(missao.getArea())) {
            throw new IllegalArgumentException("Informe uma missao com ID, tipo, status e area cadastrada.");
        }
        Robo responsavel = missao.getRoboResponsavel();
        if (responsavel != null && robos.get(responsavel.getId()) != responsavel) throw new IllegalArgumentException("Robo nao cadastrado.");
    }

    public void registrarMissao(Missao missao) {
        validarMissao(missao);
        if (missao.getStatus() != StatusMissao.PENDENTE) throw new IllegalArgumentException("A missao deve estar pendente.");
        if (missoes.stream().anyMatch(m -> m.getId().equals(missao.getId()))) throw new IllegalArgumentException("ID de missao repetido.");
        missao.bloquearCadastro();
        missoes.add(missao);
        missoesPendentes.add(missao);
    }

    public void atribuirRoboMissao(String idMissao, String idRobo) {
        Missao missao = missoes.stream().filter(m -> Objects.equals(idMissao, m.getId())).findFirst().orElse(null);
        Robo robo = robos.get(idRobo);
        if (missao == null || robo == null) throw new IllegalArgumentException("Missao ou robo nao encontrado.");
        missao.atribuirRobo(robo);
    }

    public void listarMissoesPendentes() { lerMissoesPendentes(); }

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

    public void atualizarNivelRiscoArea(Scanner in) {
        if (areas.isEmpty()) {
            System.out.println("Nenhuma area cadastrada.");
            return;
        }
        areas.forEach(a -> System.out.println(a.getId() + " - " + a.getNome() + " - " + a.getNivelRisco()));
        System.out.println("Digite o ID da area:");
        if (!in.hasNextLine()) {
            return;
        }
        String id = in.nextLine().trim();
        AreaOperacao area = areas.stream().filter(a -> id.equals(a.getId())).findFirst().orElse(null);
        if (area == null) {
            System.out.println("Area nao encontrada.");
            return;
        }
        System.out.println("Digite o novo nivel de risco: BAIXO, MEDIO ou ALTO");
        if (!in.hasNextLine()) {
            return;
        }
        try {
            NivelRisco risco = NivelRisco.valueOf(in.nextLine().trim());
            area.atualizarNivelRisco(risco);
            double consumo = switch (risco) {
                case BAIXO -> 10;
                case MEDIO -> 25;
                case ALTO -> 50;
            };
            // Atualiza apenas as missoes que ainda nao iniciaram.
            missoes.stream().filter(m -> m.getArea() == area && m.getStatus() == StatusMissao.PENDENTE)
                    .forEach(m -> m.setConsumoFixoBateria(consumo));
            System.out.println("Nivel de risco atualizado com sucesso.");
        } catch (IllegalArgumentException e) {
            System.out.println("Nivel de risco invalido.");
        }
    }

    public void listarRobosDisponiveis() {
        List<Robo> disponiveis = robos.values().stream()
                .filter(Robo::verificarDisponibilidade).toList();
        if (disponiveis.isEmpty()) {
            System.out.println("Nenhum robo disponivel.");
            return;
        }
        disponiveis.forEach(r -> {
            r.exibirRobo();
            System.out.println();
        });
    }

    public void listarMissoesEmAndamento() {
        List<Missao> emAndamento = missoes.stream()
                .filter(m -> m.getStatus() == StatusMissao.EM_ANDAMENTO).toList();
        if (emAndamento.isEmpty()) {
            System.out.println("Nenhuma missao em andamento.");
            return;
        }
        emAndamento.forEach(Missao::exibirMissao);
    }

    public void realizarManutencaoRobo(Scanner in) {
        List<Robo> emManutencao = robos.values().stream()
                .filter(r -> r.getStatus() == StatusRobo.EM_MANUTENCAO || r.precisaManutencao()).toList();
        if (emManutencao.isEmpty()) {
            System.out.println("Nenhum robo necessita de manutencao.");
            return;
        }
        emManutencao.forEach(r -> System.out.println(r.getId() + " - " + r.getNome()
                + " - Desgaste: " + r.getDesgaste() + "% - Bateria: " + r.getBateria() + "%"));
        System.out.println("Digite o ID do robo para realizar manutencao:");
        if (!in.hasNextLine()) {
            return;
        }
        Robo robo = robos.get(in.nextLine().trim());
        if (robo == null) {
            System.out.println("Robo nao encontrado.");
            return;
        }
        try {
            robo.realizarManutencao();
            System.out.println("Manutencao realizada. Status: " + robo.getStatus());
        } catch (IllegalStateException e) {
            System.out.println("Erro ao realizar manutencao: " + e.getMessage());
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
        for (Missao missao : getMissoesPendentes()) {
            missao.exibirMissao();
        }
    }

    public void gerarRelatorioGeral() {
        Relatorio relatorio = new Relatorio(this);
        System.out.println(relatorio.gerarResumo());
        try {
            relatorio.salvarEmArquivo();
            System.out.println("Relatorio salvo em src/Saves/relatorio_central.txt");
        } catch (IOException e) {
            System.out.println("Erro ao salvar relatorio: " + e.getMessage());
        }
    }

    public void gerarRelatorioMissao(Scanner in) {
        if (missoes.isEmpty()) {
            System.out.println("Nenhuma missao cadastrada.");
            return;
        }
        missoes.forEach(m -> System.out.println(m.getId() + " - " + m.getDescricao() + " - " + m.getStatus()));
        System.out.println("Digite o ID da missao para gerar o relatorio:");
        if (!in.hasNextLine()) {
            return;
        }
        String id = in.nextLine().trim();
        Missao missao = missoes.stream().filter(m -> id.equals(m.getId())).findFirst().orElse(null);
        if (missao == null) {
            System.out.println("Missao nao encontrada.");
            return;
        }
        Relatorio relatorio = new Relatorio();
        System.out.println(relatorio.gerarResumo(missao));
        try {
            relatorio.salvarEmArquivo(missao);
            System.out.println("Relatorio da missao salvo na pasta src/Saves.");
        } catch (IOException e) {
            System.out.println("Erro ao salvar relatorio: " + e.getMessage());
        }
    }

    public void gerarRelatorioMissoes() {
        if (missoes.isEmpty()) {
            System.out.println("Nenhuma missao cadastrada.");
            return;
        }
        Relatorio relatorio = new Relatorio();
        System.out.println(relatorio.gerarResumo(missoes));
        try {
            relatorio.salvarEmArquivo(missoes);
            System.out.println("Relatorio salvo em src/Saves/relatorios_missoes.txt");
        } catch (IOException e) {
            System.out.println("Erro ao salvar relatorio: " + e.getMessage());
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
        return Collections.unmodifiableMap(robos);
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
        for (Missao missao : missoes) {
            Robo responsavel = missao.getRoboResponsavel();
            if (responsavel != null && novosRobos.get(responsavel.getId()) != responsavel) {
                throw new IllegalArgumentException("A colecao precisa preservar os robos vinculados as missoes.");
            }
        }
        novosRobos.values().forEach(Robo::bloquearId);
        this.robos = novosRobos;
    }

    public void cadastrarRobo(Robo robo) {
        validarCadastroRobo(robo, robos);
        robo.bloquearId();
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
        if (missoesPendentes == null) missoesPendentes = new LinkedList<>();
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
            if (areas == null || missoes == null) throw new IllegalArgumentException("Colecoes ausentes no salvamento.");
            areas.forEach(AreaOperacao::bloquearId);
            for (Missao missao : missoes) {
                missao.bloquearCadastro();
                if (missao.getStatus() == StatusMissao.EM_ANDAMENTO && missao.getRoboResponsavel() != null) {
                    missao.getRoboResponsavel().restaurarOperacao(missao);
                }
            }
            missoesPendentes = new LinkedList<>(getMissoesPendentes());
        } catch (IllegalArgumentException e) {
            InvalidObjectException erro = new InvalidObjectException(e.getMessage());
            erro.initCause(e);
            throw erro;
        }
    }

    public List<AreaOperacao> getAreas() {
        return Collections.unmodifiableList(areas);
    }

    public void setAreas(List<AreaOperacao> areas) {
        if (areas == null) throw new IllegalArgumentException("Informe as areas.");
        List<AreaOperacao> copia = new ArrayList<>(areas);
        Set<String> ids = new HashSet<>();
        for (AreaOperacao area : copia) {
            validarArea(area);
            if (!ids.add(area.getId())) throw new IllegalArgumentException("ID de area repetido.");
        }
        for (Missao missao : missoes) {
            if (!copia.contains(missao.getArea())) throw new IllegalArgumentException("Preserve as areas das missoes.");
        }
        copia.forEach(AreaOperacao::bloquearId);
        this.areas = copia;
    }

    public List<Missao> getMissoes() {
        return Collections.unmodifiableList(missoes);
    }

    public void setMissoes(List<Missao> missoes) {
        if (missoes == null) throw new IllegalArgumentException("Informe as missoes.");
        List<Missao> copia = new ArrayList<>(missoes);
        Set<String> ids = new HashSet<>();
        for (Missao missao : copia) {
            validarMissao(missao);
            if (!ids.add(missao.getId())) throw new IllegalArgumentException("ID de missao repetido.");
        }
        for (Missao existente : this.missoes) {
            if (existente.getStatus() == StatusMissao.EM_ANDAMENTO && !copia.contains(existente)) {
                throw new IllegalArgumentException("Uma missao em andamento nao pode ser removida.");
            }
        }
        copia.forEach(Missao::bloquearCadastro);
        this.missoes = copia;
        this.missoesPendentes = new LinkedList<>(getMissoesPendentes());
    }

    public Queue<Missao> getMissoesPendentes() {
        Queue<Missao> copia = new LinkedList<>();
        for (Missao missao : missoesPendentes) {
            if (missoes.contains(missao) && missao.getStatus() == StatusMissao.PENDENTE && !copia.contains(missao)) copia.add(missao);
        }
        for (Missao missao : missoes) {
            if (missao.getStatus() == StatusMissao.PENDENTE && !copia.contains(missao)) copia.add(missao);
        }
        return copia;
    }

    public void setMissoesPendentes(Queue<Missao> missoesPendentes) {
        if (missoesPendentes == null || !new ArrayList<>(missoesPendentes).equals(new ArrayList<>(getMissoesPendentes()))) {
            throw new IllegalArgumentException("A fila deve corresponder as missoes pendentes cadastradas.");
        }
        this.missoesPendentes = new LinkedList<>(missoesPendentes);
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
        if (robos.values().stream().noneMatch(Robo::verificarDisponibilidade)) {
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
                .filter(Robo::verificarDisponibilidade)
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
        try {
            atribuirRoboMissao(idMissao, idRobo);
            missao.iniciar();
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao iniciar missao: " + e.getMessage());
        }
        if (missao.getStatus() == StatusMissao.EM_ANDAMENTO && missao.getRoboResponsavel() == robo) {
            missoesPendentes.remove(missao);
            System.out.println("Missao " + missao.getId() + " iniciada com o robo " + robo.getId() + ".");
        }
    }
}

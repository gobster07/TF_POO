import Area.AreaOperacao;
import Enum.*;
import Operacional.Missao;
import Robos.*;

public class ExecucaoMissaoTest {
    private static int passou, falhou;
    private static Drone drone() { return new Drone("D1", "Drone", 100, StatusRobo.DISPONIVEL, 200, false); }
    private static Missao missao() {
        AreaOperacao a = new AreaOperacao(); a.setHorario(Horario.DIA); a.setAltitude(100);
        Missao m = new Missao(); m.setArea(a); m.setTipo(TipoMissao.RECONHECIMENTO); m.setStatus(StatusMissao.PENDENTE); return m;
    }
    private static void check(boolean b) { if (!b) throw new AssertionError("Resultado inesperado"); }
    private static void teste(String nome, Runnable acao) {
        try { acao.run(); passou++; System.out.println("PASSOU: " + nome); }
        catch (Throwable e) { falhou++; System.out.println("FALHOU: " + nome + " - " + e); }
    }
    private static void rejeita(Drone d, Missao m) {
        int bateria = d.getBateria(), contador = d.getMissoesRealizadas(); StatusRobo status = d.getStatus();
        StatusMissao estado = m == null ? null : m.getStatus(); Robo responsavel = m == null ? null : m.getRoboResponsavel();
        boolean rejeitou = false;
        try { d.executaMissao(m); } catch (IllegalArgumentException e) { rejeitou = true; }
        check(rejeitou && d.getBateria() == bateria && d.getStatus() == status && d.getMissoesRealizadas() == contador);
        check(m == null || (m.getStatus() == estado && m.getRoboResponsavel() == responsavel));
    }
    public static void main(String[] args) {
        teste("Reconhecimento diurno permitido", () -> drone().executaMissao(missao()));
        teste("Localizacao de pessoas permitida", () -> { Missao m=missao();m.setTipo(TipoMissao.LOCALIZACAO_DE_PESSOAS);drone().executaMissao(m); });
        teste("Transporte bloqueado sem alterar estados", () -> { Missao m=missao();m.setTipo(TipoMissao.TRANSPORTE_DE_SUPRIMENTOS);rejeita(drone(),m); });
        teste("Noite sem termal bloqueada", () -> { Missao m=missao();m.getArea().setHorario(Horario.NOITE);rejeita(drone(),m); });
        teste("Noite com termal permitida", () -> { Missao m=missao();m.getArea().setHorario(Horario.NOITE);new Drone("D2","Termal",100,StatusRobo.DISPONIVEL,200,true).executaMissao(m); });
        teste("Missao nula rejeitada", () -> rejeita(drone(),null));
        teste("Area ausente rejeitada", () -> { Missao m=missao();m.setArea(null);rejeita(drone(),m); });
        teste("Horario ausente rejeitado", () -> { Missao m=missao();m.getArea().setHorario(null);rejeita(drone(),m); });
        for (StatusMissao s : StatusMissao.values()) if (s != StatusMissao.PENDENTE)
            teste("Missao " + s + " rejeitada", () -> { Missao m=missao();m.setStatus(s);rejeita(drone(),m); });
        for (StatusRobo s : StatusRobo.values()) if (s != StatusRobo.DISPONIVEL)
            teste("Drone " + s + " rejeitado", () -> { Drone d=drone();d.setStatus(s);rejeita(d,missao()); });
        teste("Outro responsavel bloqueado", () -> { Missao m=missao();m.setRoboResponsavel(drone());rejeita(drone(),m); });
        teste("Mesmo responsavel permitido", () -> { Drone d=drone();Missao m=missao();m.setRoboResponsavel(d);d.executaMissao(m); });
        teste("Altitude no limite permitido", () -> { Missao m=missao();m.getArea().setAltitude(250);drone().executaMissao(m); });
        teste("Altitude acima do limite rejeitada", () -> { Missao m=missao();m.getArea().setAltitude(250.1);rejeita(drone(),m); });
        teste("Chamada direta apenas valida", () -> { Drone d=drone();Missao m=missao();d.executaMissao(m);check(d.getBateria()==100 && d.getStatus()==StatusRobo.DISPONIVEL && m.getStatus()==StatusMissao.PENDENTE && m.getRoboResponsavel()==null); });
        teste("Missao.executar inicia operacao", () -> { Drone d=drone();Missao m=missao();m.executar(d);check(m.getStatus()==StatusMissao.EM_ANDAMENTO && m.getRoboResponsavel()==d && d.getStatus()==StatusRobo.EM_MISSAO && d.getBateria()==100 && m.getResultado()==null && d.getMissoesRealizadas()==0); });
        teste("Falha via executar preserva estados", () -> { Drone d=drone();Missao m=missao();m.getArea().setHorario(Horario.NOITE);m.executar(d);check(m.getStatus()==StatusMissao.PENDENTE && m.getRoboResponsavel()==null && d.getStatus()==StatusRobo.DISPONIVEL); });
        teste("Repetir execucao nao inicia novamente", () -> { Drone d=drone();Missao m=missao();m.executar(d);Drone outro=drone();m.executar(outro);check(m.getRoboResponsavel()==d && outro.getStatus()==StatusRobo.DISPONIVEL); });
        teste("Tipo nulo deve ser rejeitado sem NullPointerException", () -> { Missao m=missao();m.setTipo(null);rejeita(drone(),m); });
        teste("Bateria zero deve impedir inicio", () -> { Drone d=drone();d.setBateria(0);rejeita(d,missao()); });
        teste("Altitude negativa deve ser rejeitada", () -> { Missao m=missao();m.getArea().setAltitude(-1);rejeita(drone(),m); });
        teste("Altitude NaN deve ser rejeitada", () -> { Missao m=missao();m.getArea().setAltitude(Double.NaN);rejeita(drone(),m); });
        teste("Altitude maxima NaN deve ser rejeitada", () -> rejeita(new Drone("D","Drone",100,StatusRobo.DISPONIVEL,Double.NaN,false),missao()));
        System.out.println("TOTAL: " + passou + " passaram; " + falhou + " falharam.");
        if (falhou > 0) System.exit(1);
    }
}

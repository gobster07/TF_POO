import Area.AreaOperacao;
import Enum.*;
import Operacional.Missao;
import Robos.*;
import java.time.LocalDateTime;
import java.lang.reflect.Field;
public class CancelamentoMissaoTest {
 static int n;
 static void check(boolean b){if(!b)throw new AssertionError("Teste "+(n+1));n++;}
 static Missao missao(){AreaOperacao a=new AreaOperacao();a.setHorario(Horario.DIA);a.setTipoTerreno(TipoTerreno.URBANO);Missao m=new Missao();m.setArea(a);TestFixtures.status(m,StatusMissao.PENDENTE);m.setTipo(TipoMissao.RECONHECIMENTO);m.setConsumoFixoBateria(10);return m;}
 static Drone drone(){Drone d=new Drone("D","Drone",100,StatusRobo.DISPONIVEL,100,false);d.setConsumoPorHora(10);return d;}
 static void rejeita(Missao m){StatusMissao s=m.getStatus();boolean erro=false;try{m.cancelar();}catch(IllegalStateException e){erro=true;}check(erro&&m.getStatus()==s);}
 public static void main(String[] args)throws Exception{
 Missao m=missao();m.cancelar();check(m.getStatus()==StatusMissao.CANCELADA&&m.getResultado()!=null);rejeita(m);
 m=missao();TestFixtures.status(m,StatusMissao.CONCLUIDA);rejeita(m);
 m=missao();TestFixtures.status(m,null);rejeita(m);
 m=missao();Drone d=drone();m.executar(d);Field f=Missao.class.getDeclaredField("dataInicio");f.setAccessible(true);f.set(m,LocalDateTime.now().minusMinutes(60).minusSeconds(5));m.cancelar();check(m.getStatus()==StatusMissao.CANCELADA&&d.getStatus()==StatusRobo.DISPONIVEL&&d.getBateria()==80&&d.getMissoesRealizadas()==0&&m.getRoboResponsavel()==d);rejeita(m);check(d.getBateria()==80);
 boolean erro=false;try{m.concluir();}catch(IllegalStateException e){erro=true;}check(erro);
 m=missao();d=drone();m.executar(d);d.setBateria(5);m.cancelar();check(d.getBateria()==0&&d.getStatus()==StatusRobo.SEM_BATERIA&&m.getStatus()==StatusMissao.CANCELADA);
 m=missao();d=drone();TestFixtures.status(d,StatusRobo.EM_MISSAO);TestFixtures.responsavel(m,d);m.cancelar();check(d.getStatus()==StatusRobo.EM_MISSAO&&d.getBateria()==100);
 m=missao();TestFixtures.status(m,StatusMissao.EM_ANDAMENTO);rejeita(m);
 System.out.println(n+" verificacoes de cancelamento passaram.");
 }
}

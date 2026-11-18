import Area.AreaOperacao;
import Enum.*;
import Operacional.*;
import Robos.*;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
public class ConclusaoMissaoTest {
 static int total;
 static void check(boolean b){if(!b)throw new AssertionError("Cenario "+(total+1));total++;}
 static Missao missao(){AreaOperacao a=new AreaOperacao();a.setHorario(Horario.DIA);a.setTipoTerreno(TipoTerreno.URBANO);Missao m=new Missao();m.setArea(a);m.setTipo(TipoMissao.RECONHECIMENTO);m.setStatus(StatusMissao.PENDENTE);m.setConsumoFixoBateria(10);return m;}
 static void rejeita(Missao m){StatusMissao s=m.getStatus();boolean erro=false;try{m.concluir();}catch(IllegalStateException e){erro=true;}check(erro&&m.getStatus()==s);}
 public static void main(String[] args)throws Exception{
 for(Robo r:new Robo[]{new Drone("D","Drone",100,StatusRobo.DISPONIVEL,100,false),new RoboTerrestre("R","Terrestre",100,StatusRobo.DISPONIVEL,Roda.NORMAL,10,true)}){
 Missao m=missao();m.executar(r);m.concluir();
 check(m.getStatus()==StatusMissao.CONCLUIDA);check(r.getBateria()==90&&r.getStatus()==StatusRobo.DISPONIVEL);check(r.getMissoesRealizadas()==1&&m.getResultado()!=null&&!m.getResultado().isBlank());
 rejeita(m);check(r.getBateria()==90&&r.getMissoesRealizadas()==1);
 }
 for(StatusMissao s:new StatusMissao[]{StatusMissao.PENDENTE,StatusMissao.CANCELADA}){Missao m=missao();m.setStatus(s);rejeita(m);}
 Drone d=new Drone("D","Drone",10,StatusRobo.DISPONIVEL,100,false);Missao m=missao();m.executar(d);m.concluir();check(m.getStatus()==StatusMissao.CONCLUIDA&&d.getBateria()==0&&d.getStatus()==StatusRobo.SEM_BATERIA&&d.getMissoesRealizadas()==1);
 d=new Drone("D","Drone",15,StatusRobo.DISPONIVEL,100,false);d.setConsumoPorHora(10);m=missao();m.executar(d);
 Field inicio=Missao.class.getDeclaredField("dataInicio");inicio.setAccessible(true);inicio.set(m,LocalDateTime.now().minusHours(1).minusSeconds(5));m.concluir();
 check(m.getStatus()==StatusMissao.CANCELADA&&d.getStatus()==StatusRobo.SEM_BATERIA&&d.getMissoesRealizadas()==0);
 m=missao();m.setStatus(StatusMissao.EM_ANDAMENTO);rejeita(m);
 System.out.println(total+" verificacoes de conclusao passaram.");
 }
}

import Area.AreaOperacao;
import Enum.*;
import Operacional.*;
import Robos.*;
import java.lang.reflect.Field;
public class InicioMissaoTest {
 static int n;
 static Missao missao(){AreaOperacao a=new AreaOperacao();a.setHorario(Horario.DIA);a.setTipoTerreno(TipoTerreno.URBANO);Missao m=new Missao();m.setArea(a);m.setTipo(TipoMissao.RECONHECIMENTO);m.setStatus(StatusMissao.PENDENTE);m.setConsumoFixoBateria(10);return m;}
 static void check(boolean b){if(!b)throw new AssertionError("Cenario "+(n+1));n++;}
 static Object inicio(Missao m)throws Exception{Field f=Missao.class.getDeclaredField("dataInicio");f.setAccessible(true);return f.get(m);}
 public static void main(String[] args)throws Exception{
 for(Robo r:new Robo[]{new Drone("D","Drone",100,StatusRobo.DISPONIVEL,100,false),new RoboTerrestre("R","Robo",100,StatusRobo.DISPONIVEL,Roda.NORMAL,10,false)}){
 Missao m=missao();r.setBateria(9);m.executar(r);
 check(m.getStatus()==StatusMissao.PENDENTE&&r.getStatus()==StatusRobo.DISPONIVEL&&m.getRoboResponsavel()==null&&inicio(m)==null&&r.getBateria()==9);
 r.setBateria(10);m.executar(r);check(m.getStatus()==StatusMissao.EM_ANDAMENTO&&r.getStatus()==StatusRobo.EM_MISSAO&&m.getRoboResponsavel()==r&&inicio(m)!=null&&r.getBateria()==10);
 Object data=inicio(m);m.executar(r);check(inicio(m).equals(data)&&r.getBateria()==10);
 Missao outra=missao();outra.executar(r);check(outra.getStatus()==StatusMissao.PENDENTE&&outra.getRoboResponsavel()==null&&inicio(outra)==null);
 r.setStatus(StatusRobo.DISPONIVEL);m=missao();m.setRoboResponsavel(new Drone());m.executar(r);check(m.getStatus()==StatusMissao.PENDENTE&&r.getStatus()==StatusRobo.DISPONIVEL&&inicio(m)==null);
 }
 Missao m=missao();m.executar(null);check(m.getStatus()==StatusMissao.PENDENTE&&inicio(m)==null);
 System.out.println(n+" cenarios de inicio passaram.");
 }
}

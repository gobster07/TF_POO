import Main.Menu;
import Operacional.*;
import Robos.*;
import Area.*;
import Enum.*;
import java.util.Scanner;
import java.io.*;
public class MenuFinalizacaoTest {
 static int n;
 static void check(boolean b){if(!b)throw new AssertionError("Cenario "+(n+1));n++;}
 static Missao preparar(boolean inicia){
 Menu.centralResgate=new CentralResgate();TestFixtures.missoes(Menu.centralResgate).clear();TestFixtures.pendentes(Menu.centralResgate).clear();TestFixtures.robos(Menu.centralResgate).clear();
 AreaOperacao a=new AreaOperacao();a.setHorario(Horario.DIA);a.setTipoTerreno(TipoTerreno.URBANO);
 Missao m=new Missao();m.setId("M1");m.setDescricao("Missao teste");m.setTipo(TipoMissao.RECONHECIMENTO);TestFixtures.status(m,StatusMissao.PENDENTE);m.setConsumoFixoBateria(10);m.setArea(a);
 Drone d=new Drone("D1","Drone",100,StatusRobo.DISPONIVEL,100,false);Menu.centralResgate.cadastrarRobo(d);TestFixtures.missoes(Menu.centralResgate).add(m);
 if(inicia)m.executar(d);else TestFixtures.pendentes(Menu.centralResgate).add(m);return m;
 }
 static String menu(String entrada){PrintStream original=System.out;ByteArrayOutputStream log=new ByteArrayOutputStream();try{System.setOut(new PrintStream(log));new Menu().MenuStart(new Scanner(entrada));}finally{System.setOut(original);}return log.toString();}
 public static void main(String[] args){
 Missao m=preparar(true);String log=menu("9\nM1\n0\n");check(m.getStatus()==StatusMissao.CONCLUIDA&&m.getRoboResponsavel().getStatus()==StatusRobo.DISPONIVEL&&m.getRoboResponsavel().getMissoesRealizadas()==1);check(log.contains("M1 - Missao teste"));
 m=preparar(false);log=menu("9\n0\n");check(m.getStatus()==StatusMissao.PENDENTE&&log.contains("Nenhuma missao em andamento."));
 m=preparar(true);menu("9\nX\n0\n");check(m.getStatus()==StatusMissao.EM_ANDAMENTO);
 m=preparar(true);menu("9\n");check(m.getStatus()==StatusMissao.EM_ANDAMENTO);
 m=preparar(false);menu("10\nM1\n0\n");check(m.getStatus()==StatusMissao.CANCELADA&&Menu.centralResgate.getMissoesPendentes().isEmpty());
 m=preparar(true);menu("10\nM1\n0\n");check(m.getStatus()==StatusMissao.CANCELADA&&m.getRoboResponsavel().getStatus()==StatusRobo.DISPONIVEL);
 m=preparar(false);menu("10\nX\n0\n");check(m.getStatus()==StatusMissao.PENDENTE&&Menu.centralResgate.getMissoesPendentes().contains(m));
 m=preparar(false);TestFixtures.status(m,StatusMissao.CONCLUIDA);log=menu("10\n0\n");check(m.getStatus()==StatusMissao.CONCLUIDA&&log.contains("Nenhuma missao disponivel"));
 m=preparar(true);menu("9\nM1\n9\n10\n0\n");check(m.getStatus()==StatusMissao.CONCLUIDA&&m.getRoboResponsavel().getMissoesRealizadas()==1);
 m=preparar(true);m.getRoboResponsavel().setBateria(5);menu("9\nM1\n0\n");check(m.getStatus()==StatusMissao.CANCELADA&&m.getRoboResponsavel().getStatus()==StatusRobo.SEM_BATERIA);
 System.out.println(n+" verificacoes do menu passaram.");
 }
}

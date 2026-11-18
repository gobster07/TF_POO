import Main.Menu;
import Area.AreaOperacao;
import Enum.*;
import Operacional.*;
import Robos.*;
import java.util.Scanner;
public class MenuExecucaoTest {
 static int total;
 static Missao preparar(){
 Menu.centralResgate=new CentralResgate();
 Menu.centralResgate.getMissoes().clear(); Menu.centralResgate.getMissoesPendentes().clear(); Menu.centralResgate.getRobos().clear(); Menu.centralResgate.getAreas().clear();
 AreaOperacao a=new AreaOperacao();a.setHorario(Horario.DIA);a.setTipoTerreno(TipoTerreno.URBANO);
 Missao m=new Missao();m.setId("M1");m.setArea(a);m.setStatus(StatusMissao.PENDENTE);m.setTipo(TipoMissao.RECONHECIMENTO);m.setConsumoFixoBateria(10);
 Menu.centralResgate.getMissoes().add(m);Menu.centralResgate.getMissoesPendentes().add(m);
 Menu.centralResgate.cadastrarRobo(new Drone("D1","Drone",100,StatusRobo.DISPONIVEL,100,false));return m;
 }
 static void check(boolean ok){if(!ok)throw new AssertionError("Cenario "+(total+1));total++;}
 static void menu(String entrada){new Menu().MenuStart(new Scanner(entrada));}
 static void pendente(Missao m){check(m.getStatus()==StatusMissao.PENDENTE&&m.getRoboResponsavel()==null&&Menu.centralResgate.getMissoesPendentes().contains(m));}
 public static void main(String[] args){
 Missao m=preparar();menu("8\nM1\nD1\n0\n");check(m.getStatus()==StatusMissao.EM_ANDAMENTO&&m.getRoboResponsavel()==Menu.centralResgate.getRobos().get("D1")&&m.getRoboResponsavel().getStatus()==StatusRobo.EM_MISSAO&&Menu.centralResgate.getMissoesPendentes().isEmpty());
 m=preparar();menu("8\nX\n0\n");pendente(m);
 m=preparar();menu("8\nM1\nX\n0\n");pendente(m);
 m=preparar();m.getArea().setHorario(Horario.NOITE);menu("8\nM1\nD1\n0\n");pendente(m);
 m=preparar();Menu.centralResgate.getRobos().get("D1").setBateria(1);menu("8\nM1\nD1\n0\n");pendente(m);
 m=preparar();Menu.centralResgate.getRobos().get("D1").setStatus(StatusRobo.EM_MANUTENCAO);menu("8\n0\n");pendente(m);
 m=preparar();menu("8\n");pendente(m);
 m=preparar();menu("8\nM1\n");pendente(m);
 m=preparar();m.setStatus(StatusMissao.CONCLUIDA);menu("8\n0\n");check(m.getStatus()==StatusMissao.CONCLUIDA);
 m=preparar();Menu.centralResgate.getRobos().clear();Menu.centralResgate.cadastrarRobo(new RoboTerrestre("R1","Terrestre",100,StatusRobo.DISPONIVEL,Roda.OFFROAD,20,true));menu("8\nM1\nR1\n0\n");check(m.getStatus()==StatusMissao.EM_ANDAMENTO&&Menu.centralResgate.getMissoesPendentes().isEmpty());
 System.out.println(total+" cenarios de execucao pelo menu passaram.");
 }
}

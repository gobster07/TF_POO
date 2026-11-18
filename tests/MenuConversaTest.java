import Main.Menu;
import Operacional.*;
import Robos.*;
import Enum.*;
import java.util.Scanner;
import java.nio.file.*;
public class MenuConversaTest {
 static int n;
 static void check(boolean b){if(!b)throw new AssertionError("Teste "+(n+1));n++;}
 static Missao preparar(){Menu.centralResgate=new CentralResgate();TestFixtures.robos(Menu.centralResgate).clear();TestFixtures.missoes(Menu.centralResgate).clear();TestFixtures.pendentes(Menu.centralResgate).clear();Drone d=new Drone();d.setId("D1");d.setNome("drone01");TestFixtures.status(d,StatusRobo.EM_MISSAO);Missao m=new Missao();m.setId("M1");TestFixtures.responsavel(m,d);TestFixtures.status(m,StatusMissao.EM_ANDAMENTO);Menu.centralResgate.cadastrarRobo(d);TestFixtures.missoes(Menu.centralResgate).add(m);return m;}
 public static void main(String[] args)throws Exception{
 Path arquivo=Path.of("src/Mensagens/mensagens.txt");Missao m=preparar();new Menu().MenuStart(new Scanner("12\nM1\n12\nM1\n12\nM1\n12\nM1\n0\n"));
 String log=Files.readString(arquivo);check(Files.readAllLines(arquivo).size()==16);check(log.indexOf("recebimento das instrucoes")<log.indexOf("Informe a situacao"));check(log.indexOf("Informe a situacao")<log.indexOf("funcionamento dos sistemas"));check(log.lastIndexOf("recebimento das instrucoes")>log.indexOf("funcionamento dos sistemas"));check(m.getStatus()==StatusMissao.EM_ANDAMENTO&&m.getRoboResponsavel().getBateria()==100);
 String antes=log;new Menu().MenuStart(new Scanner("12\nX\n0\n"));check(Files.readString(arquivo).equals(antes));
 new Menu().MenuStart(new Scanner("12\n"));check(Files.readString(arquivo).equals(antes));
 TestFixtures.status(m,StatusMissao.PENDENTE);new Menu().MenuStart(new Scanner("12\n0\n"));check(Files.readString(arquivo).equals(antes));
 TestFixtures.status(m,StatusMissao.EM_ANDAMENTO);m.getRoboResponsavel().setStatus(StatusRobo.DISPONIVEL);new Menu().MenuStart(new Scanner("12\n0\n"));check(Files.readString(arquivo).equals(antes));
 System.out.println(n+" verificacoes de conversa pelo menu passaram.");
 }
}

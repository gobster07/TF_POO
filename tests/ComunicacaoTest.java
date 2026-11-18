import Area.AreaOperacao;
import Enum.*;
import Operacional.Missao;
import Robos.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
public class ComunicacaoTest {
 static int n;
 static Path arquivo=Path.of("src","Mensagens","mensagens.txt");
 static void check(boolean b){if(!b)throw new AssertionError("Cenario "+(n+1));n++;}
 static Missao missao(String id){AreaOperacao a=new AreaOperacao();a.setHorario(Horario.DIA);a.setTipoTerreno(TipoTerreno.URBANO);Missao m=new Missao();m.setId(id);m.setArea(a);m.setTipo(TipoMissao.RECONHECIMENTO);TestFixtures.status(m,StatusMissao.PENDENTE);m.setConsumoFixoBateria(10);return m;}
 public static void main(String[] args)throws Exception{
 Drone d=new Drone("D1","drone01",100,StatusRobo.DISPONIVEL,100,false);
 RoboTerrestre r=new RoboTerrestre("R1","terrestre01",100,StatusRobo.DISPONIVEL,Roda.NORMAL,20,true);
 d.enviarMensagem("Área de risco identificada.");d.receberMensagem("Inspecionar a área.");r.enviarMensagem("Suprimentos entregues.");r.receberMensagem("Retornar à central.");
 List<String> linhas=Files.readAllLines(arquivo,StandardCharsets.UTF_8);check(linhas.size()==4);check(linhas.get(0).contains("Drone D1 (drone01) -> Central: Área de risco"));check(linhas.get(1).contains("Central -> Drone D1"));check(linhas.get(2).contains("RoboTerrestre R1 (terrestre01) -> Central"));check(linhas.get(3).contains("Retornar à central."));java.time.LocalDateTime.parse(linhas.get(0).substring(1,20),java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));check(true);
 d.enviarMensagem("Linha 1\nLinha 2");check(Files.readAllLines(arquivo).size()==5);
 for(String invalida:new String[]{null,"","   "}){boolean erro=false;try{d.enviarMensagem(invalida);}catch(IllegalArgumentException e){erro=true;}check(erro);}check(Files.readAllLines(arquivo).size()==5);
 Missao m=missao("M1");m.executar(d);check(Files.readString(arquivo).contains("Missao")||Files.readString(arquivo).contains("missao M1"));m.concluir();check(Files.readString(arquivo).contains("Missao M1: Missao concluida"));
 m=missao("M2");m.executar(r);m.cancelar();check(Files.readString(arquivo).contains("Cancelamento da missao M2 confirmado."));
 String antes=Files.readString(arquivo);m=missao("M3");m.getArea().setHorario(Horario.NOITE);m.executar(d);check(Files.readString(arquivo).equals(antes));
 System.out.println(n+" verificacoes de comunicacao passaram.");
 }
}

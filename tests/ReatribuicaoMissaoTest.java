import Area.AreaOperacao;
import Enum.*;
import Main.Menu;
import Operacional.*;
import Robos.*;
import java.io.*;
import java.time.LocalDateTime;
import java.lang.reflect.Field;
import java.util.Scanner;
public class ReatribuicaoMissaoTest {
 static int n;
 static void check(boolean ok){if(!ok)throw new AssertionError("Teste "+(n+1));n++;}
 static Drone drone(String id,boolean termal){Drone d=new Drone(id,id,100,StatusRobo.DISPONIVEL,500,termal);d.setConsumoPorHora(10);return d;}
 static CentralResgate central(){CentralResgate c=new CentralResgate();TestFixtures.robos(c).clear();TestFixtures.areas(c).clear();TestFixtures.missoes(c).clear();TestFixtures.pendentes(c).clear();return c;}
 static Missao iniciar(CentralResgate c,Robo r,Horario h,TipoTerreno t,TipoMissao tipo){
 AreaOperacao a=new AreaOperacao();a.setId("A1");a.setHorario(h);a.setTipoTerreno(t);TestFixtures.areas(c).add(a);
 Missao m=new Missao();m.setId("M1");m.setArea(a);TestFixtures.status(m,StatusMissao.PENDENTE);m.setTipo(tipo);m.setConsumoFixoBateria(10);TestFixtures.missoes(c).add(m);c.cadastrarRobo(r);m.executar(r);if(m.getStatus()!=StatusMissao.EM_ANDAMENTO)throw new AssertionError("Preparacao");return m;
 }
 static RoboTerrestre terrestre(String id,boolean flutua){return new RoboTerrestre(id,id,100,StatusRobo.DISPONIVEL,Roda.OFFROAD,20,flutua);}
 public static void main(String[] args)throws Exception{
 CentralResgate c=central();Drone antigo=drone("D1",true);Missao m=iniciar(c,antigo,Horario.DIA,TipoTerreno.URBANO,TipoMissao.RECONHECIMENTO);Drone novo=drone("D2",true);c.cadastrarRobo(novo);
 Field data=Missao.class.getDeclaredField("dataInicio");data.setAccessible(true);data.set(m,LocalDateTime.now().minusMinutes(60).minusSeconds(5));c.reatribuirMissaoPorFalha("D1");check(m.getRoboResponsavel()==novo&&m.getStatus()==StatusMissao.EM_ANDAMENTO&&novo.getStatus()==StatusRobo.EM_MISSAO&&c.getMissoesPendentes().isEmpty());check(antigo.getStatus()==StatusRobo.EM_MANUTENCAO&&antigo.getBateria()==80&&antigo.getMissoesRealizadas()==0);check(((LocalDateTime)data.get(m)).isAfter(LocalDateTime.now().minusSeconds(10)));
 m.concluir();check(m.getStatus()==StatusMissao.CONCLUIDA&&novo.getMissoesRealizadas()==1&&antigo.getMissoesRealizadas()==0);
 c=central();antigo=drone("D1",true);m=iniciar(c,antigo,Horario.NOITE,TipoTerreno.URBANO,TipoMissao.LOCALIZACAO_DE_PESSOAS);c.cadastrarRobo(drone("D2",false));novo=drone("D3",true);c.cadastrarRobo(novo);c.reatribuirMissaoPorFalha("D1");check(m.getRoboResponsavel()==novo&&c.getRobos().get("D2").getStatus()==StatusRobo.DISPONIVEL);
 c=central();RoboTerrestre rt=terrestre("R1",true);m=iniciar(c,rt,Horario.DIA,TipoTerreno.ALAGADO,TipoMissao.TRANSPORTE_DE_SUPRIMENTOS);c.cadastrarRobo(drone("D1",true));c.cadastrarRobo(terrestre("R2",false));RoboTerrestre substituto=terrestre("R3",true);c.cadastrarRobo(substituto);c.reatribuirMissaoPorFalha("R1");check(m.getRoboResponsavel()==substituto);
 c=central();antigo=drone("D1",true);m=iniciar(c,antigo,Horario.DIA,TipoTerreno.URBANO,TipoMissao.RECONHECIMENTO);Drone fraco=drone("D2",true);fraco.setBateria(9);c.cadastrarRobo(fraco);Drone ocupado=drone("D3",true);TestFixtures.status(ocupado,StatusRobo.EM_MISSAO);c.cadastrarRobo(ocupado);c.reatribuirMissaoPorFalha("D1");check(m.getStatus()==StatusMissao.PENDENTE&&m.getRoboResponsavel()==null&&data.get(m)==null&&c.getMissoesPendentes().size()==1&&c.getMissoesPendentes().peek()==m);check(ocupado.getStatus()==StatusRobo.EM_MISSAO&&fraco.getBateria()==9);
 boolean erro=false;try{c.reatribuirMissaoPorFalha("D1");}catch(IllegalStateException e){erro=true;}check(erro&&c.getMissoesPendentes().size()==1);
 novo=drone("D4",true);c.cadastrarRobo(novo);c.executarMissao(new Scanner("M1\nD4\n"));check(m.getRoboResponsavel()==novo&&c.getMissoesPendentes().isEmpty());
 erro=false;try{c.reatribuirMissaoPorFalha("INEXISTENTE");}catch(IllegalArgumentException e){erro=true;}check(erro&&m.getRoboResponsavel()==novo);
 c.cadastrarRobo(drone("D5",true));c.reatribuirMissaoPorFalha("D4");check(m.getRoboResponsavel()==c.getRobos().get("D5")&&novo.getStatus()==StatusRobo.EM_MANUTENCAO);
 ByteArrayOutputStream bytes=new ByteArrayOutputStream();try(ObjectOutputStream out=new ObjectOutputStream(bytes)){out.writeObject(c);}try(ObjectInputStream in=new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))){CentralResgate salva=(CentralResgate)in.readObject();check(salva.getMissoes().get(0).getRoboResponsavel()==salva.getRobos().get("D5")&&salva.getRobos().get("D4").getStatus()==StatusRobo.EM_MANUTENCAO);}
 c=central();antigo=drone("D1",true);m=iniciar(c,antigo,Horario.DIA,TipoTerreno.URBANO,TipoMissao.RECONHECIMENTO);c.cadastrarRobo(drone("D2",true));Menu.centralResgate=c;new Menu().MenuStart(new Scanner("13\nD1\n0\n"));check(m.getRoboResponsavel()==c.getRobos().get("D2"));
 c=central();c.registrarFalhaRobo(new Scanner(""));check(c.getMissoes().isEmpty());
 System.out.println(n+" verificacoes de reatribuicao passaram.");
 }
}

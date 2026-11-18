import Area.AreaOperacao;
import Enum.*;
import Main.Menu;
import Operacional.*;
import Robos.*;
import java.io.*;
import java.util.Scanner;
public class DesgasteTest {
 static int n;
 static void check(boolean b){if(!b)throw new AssertionError("Verificacao "+(n+1));n++;}
 static Missao missao(NivelRisco risco,TipoTerreno terreno,double altitude){AreaOperacao a=new AreaOperacao();a.setNivelRisco(risco);a.setTipoTerreno(terreno);a.setAltitude(altitude);a.setHorario(Horario.DIA);Missao m=new Missao();m.setId("M");m.setArea(a);m.setTipo(TipoMissao.RECONHECIMENTO);TestFixtures.status(m,StatusMissao.PENDENTE);m.setConsumoFixoBateria(0);return m;}
 static Drone drone(){return new Drone("D","drone01",100,StatusRobo.DISPONIVEL,1000,false);}
 static void conclui(Robo r,Missao m){m.executar(r);if(m.getStatus()!=StatusMissao.EM_ANDAMENTO)throw new AssertionError("Nao iniciou");m.concluir();}
 public static void main(String[] args)throws Exception{
 Drone d=drone();check(d.getDesgaste()==0);conclui(d,missao(NivelRisco.BAIXO,TipoTerreno.URBANO,0));check(d.getDesgaste()==10);
 conclui(d,missao(NivelRisco.MEDIO,TipoTerreno.URBANO,0));check(d.getDesgaste()==25);
 Drone termal=new Drone("D2","Termal",100,StatusRobo.DISPONIVEL,1000,true);conclui(termal,missao(NivelRisco.ALTO,TipoTerreno.URBANO,200));check(termal.getDesgaste()==30);
 for(TipoTerreno t:TipoTerreno.values()){RoboTerrestre r=new RoboTerrestre("R","R",100,StatusRobo.DISPONIVEL,Roda.OFFROAD,20,true);conclui(r,missao(NivelRisco.BAIXO,t,0));check(r.getDesgaste()==(t==TipoTerreno.IRREGULAR?20:t==TipoTerreno.ALAGADO?15:10));}
 d=drone();for(int i=0;i<8;i++)conclui(d,missao(NivelRisco.BAIXO,TipoTerreno.URBANO,0));check(d.getDesgaste()==80&&d.getStatus()==StatusRobo.EM_MANUTENCAO&&d.getMissoesRealizadas()==8);
 Missao bloqueada=missao(NivelRisco.BAIXO,TipoTerreno.URBANO,0);bloqueada.executar(d);check(bloqueada.getStatus()==StatusMissao.PENDENTE&&d.getDesgaste()==80);
 CentralResgate c=new CentralResgate();TestFixtures.robos(c).clear();TestFixtures.missoes(c).clear();TestFixtures.pendentes(c).clear();c.cadastrarRobo(d);d.setBateria(35);c.recarregarRobos();check(d.getBateria()==35&&d.getDesgaste()==80);
 Menu.centralResgate=c;new Menu().MenuStart(new Scanner("20\nD\n0\n"));check(d.getDesgaste()==0&&d.getBateria()==35&&d.getStatus()==StatusRobo.DISPONIVEL);
 TestFixtures.status(d,StatusRobo.EM_MANUTENCAO);d.setBateria(0);d.realizarManutencao();check(d.getStatus()==StatusRobo.SEM_BATERIA&&d.getBateria()==0);
 d=drone();Missao m=missao(NivelRisco.BAIXO,TipoTerreno.URBANO,0);m.executar(d);boolean erro=false;try{d.realizarManutencao();}catch(IllegalStateException e){erro=true;}check(erro&&d.getStatus()==StatusRobo.EM_MISSAO);
 m.cancelar();check(d.getDesgaste()==10&&d.getMissoesRealizadas()==0);erro=false;try{m.cancelar();}catch(IllegalStateException e){erro=true;}check(erro&&d.getDesgaste()==10);
 d=drone();m=missao(NivelRisco.BAIXO,TipoTerreno.URBANO,0);TestFixtures.responsavel(m,d);m.cancelar();check(d.getDesgaste()==0);
 d=drone();m=missao(NivelRisco.BAIXO,TipoTerreno.URBANO,0);m.executar(d);m.interromperPorFalha();check(d.getDesgaste()==10&&d.getStatus()==StatusRobo.EM_MANUTENCAO&&m.getStatus()==StatusMissao.PENDENTE);d.realizarManutencao();check(d.getDesgaste()==0&&d.getStatus()==StatusRobo.DISPONIVEL);
 d=drone();for(int i=0;i<20;i++)d.registrarDesgaste(missao(NivelRisco.ALTO,TipoTerreno.URBANO,0));check(d.getDesgaste()==100);
 ByteArrayOutputStream bytes=new ByteArrayOutputStream();try(ObjectOutputStream out=new ObjectOutputStream(bytes)){out.writeObject(d);}try(ObjectInputStream in=new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))){Drone salvo=(Drone)in.readObject();check(salvo.getDesgaste()==100&&salvo.getStatus()==StatusRobo.EM_MANUTENCAO);}
 System.out.println(n+" verificacoes de desgaste e manutencao passaram.");
 }
}

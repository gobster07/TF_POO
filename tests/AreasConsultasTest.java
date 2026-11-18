import Area.AreaOperacao;
import Enum.*;
import Operacional.*;
import Robos.*;
import Main.Menu;
import java.io.*;
import java.util.*;
public class AreasConsultasTest {
 static int n;
 static void check(boolean b){if(!b)throw new AssertionError("Teste "+(n+1));n++;}
 static String capturar(Runnable r){PrintStream anterior=System.out;ByteArrayOutputStream out=new ByteArrayOutputStream();try{System.setOut(new PrintStream(out));r.run();}finally{System.setOut(anterior);}return out.toString();}
 static Missao missao(String id,AreaOperacao a,StatusMissao s){Missao m=new Missao();m.setId(id);m.setArea(a);m.setConsumoFixoBateria(10);TestFixtures.status(m,s);return m;}
 public static void main(String[] args)throws Exception{
 CentralResgate c=new CentralResgate();TestFixtures.areas(c).clear();TestFixtures.robos(c).clear();TestFixtures.missoes(c).clear();TestFixtures.pendentes(c).clear();
 c.registroArea(new Scanner("A1\nCentro\nRua\nDIA\nURBANO\nBAIXO\n20\nDescricao completa da area\n"));AreaOperacao a=c.getAreas().get(0);check(a.getDescricao().equals("Descricao completa da area"));
 Missao pendente=missao("PENDENTE",a,StatusMissao.PENDENTE),ativa=missao("ATIVA",a,StatusMissao.EM_ANDAMENTO);TestFixtures.missoes(c).add(pendente);TestFixtures.missoes(c).add(ativa);
 for(NivelRisco risco:NivelRisco.values()){c.atualizarNivelRiscoArea(new Scanner("A1\n"+risco+"\n"));check(a.getNivelRisco()==risco);check(pendente.getConsumoFixoBateria()==(risco==NivelRisco.BAIXO?10:risco==NivelRisco.MEDIO?25:50));check(ativa.getConsumoFixoBateria()==10);}
 c.atualizarNivelRiscoArea(new Scanner("A1\nINVALIDO\n"));check(a.getNivelRisco()==NivelRisco.ALTO&&pendente.getConsumoFixoBateria()==50);
 c.atualizarNivelRiscoArea(new Scanner("X\n"));check(a.getNivelRisco()==NivelRisco.ALTO);
 c.atualizarNivelRiscoArea(new Scanner("A1\n"));check(a.getNivelRisco()==NivelRisco.ALTO);
 boolean erro=false;try{a.atualizarNivelRisco(null);}catch(IllegalArgumentException e){erro=true;}check(erro&&a.getNivelRisco()==NivelRisco.ALTO);
 for(StatusRobo status:StatusRobo.values()){Drone d=new Drone();d.setId("ROBO_"+status);TestFixtures.status(d,status);c.cadastrarRobo(d);}
 String texto=capturar(c::listarRobosDisponiveis);check(texto.contains("ID: ROBO_DISPONIVEL")&&!texto.contains("ID: ROBO_EM_MISSAO")&&!texto.contains("ID: ROBO_EM_MANUTENCAO")&&!texto.contains("ID: ROBO_SEM_BATERIA"));
 TestFixtures.missoes(c).add(missao("CONCLUIDA",a,StatusMissao.CONCLUIDA));texto=capturar(c::listarMissoesEmAndamento);check(texto.contains("ID: ATIVA")&&!texto.contains("ID: PENDENTE")&&!texto.contains("ID: CONCLUIDA"));
 Menu.centralResgate=c;texto=capturar(()->new Menu().MenuStart(new Scanner("14\nA1\nMEDIO\n15\n16\n0\n")));check(a.getNivelRisco()==NivelRisco.MEDIO&&texto.contains("ID: ROBO_DISPONIVEL")&&texto.contains("ID: ATIVA"));
 CentralResgate salva=new Save.Salvamento().carregarCentral();check(salva.getAreas().get(0).getDescricao().equals("Descricao completa da area")&&salva.getAreas().get(0).getNivelRisco()==NivelRisco.MEDIO);
 TestFixtures.robos(c).clear();TestFixtures.missoes(c).clear();check(capturar(c::listarRobosDisponiveis).contains("Nenhum robo disponivel"));check(capturar(c::listarMissoesEmAndamento).contains("Nenhuma missao em andamento"));
 TestFixtures.areas(c).clear();check(capturar(()->c.atualizarNivelRiscoArea(new Scanner(""))).contains("Nenhuma area cadastrada"));
 System.out.println(n+" verificacoes de areas e consultas passaram.");
 }
}
